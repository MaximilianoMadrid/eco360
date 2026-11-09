package cl.duoc.eco360.pedidos.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    // Scopes del Resource Server "rs-api-pedidos" en Cognito. El de lectura es el
    // que ya tenias; "pedidos-write" es nuevo y hay que crearlo en Cognito.
    // Spring Security les antepone el prefijo SCOPE_ al leerlos del JWT.
    private static final String SCOPE_LECTURA = "SCOPE_rs-api-pedidos/pedidos-read";
    private static final String SCOPE_ESCRITURA = "SCOPE_rs-api-pedidos/pedidos-write";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/pedidos", "/api/pedidos/**")
                    .hasAuthority(SCOPE_LECTURA)
                .requestMatchers(HttpMethod.POST, "/api/pedidos", "/api/pedidos/**")
                    .hasAuthority(SCOPE_ESCRITURA)
                .requestMatchers(HttpMethod.PUT, "/api/pedidos/**")
                    .hasAuthority(SCOPE_ESCRITURA)
                .requestMatchers(HttpMethod.DELETE, "/api/pedidos/**")
                    .hasAuthority(SCOPE_ESCRITURA)
                .requestMatchers("/api/rabbit-admin/**")
                    .hasAuthority(SCOPE_ESCRITURA)
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }

    // CORS en el backend: con integracion HTTP proxy, API Gateway no agrega los
    // encabezados Access-Control-* a la respuesta real, asi que los pone el servicio.
    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${eco360.cors.allowed-origin:http://localhost:4200}") String origenPermitido) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(origenPermitido));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
