package cl.duoc.pedidos360_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Nombre exacto del custom scope creado en Cognito (Resource server ->
    // pedidos360-api, scope -> pedidos-api-read). Si en tu User Pool usaste
    // otro identifier/scope, actualiza este valor para que coincida.
    private static final String CUSTOM_SCOPE_AUTHORITY = "SCOPE_rs-api-pedidos/pedidos-read";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Paso 21 de la guia: una vez validado que el JWT es correcto
                // (checkpoint "Resource Server" con .authenticated()), se exige
                // el custom scope exacto emitido por Cognito.
                // Para volver a probar solo la validez del JWT sin exigir el
                // scope, comenta la linea de abajo y descomenta la alternativa.
                .requestMatchers("/api/pedidos").hasAuthority(CUSTOM_SCOPE_AUTHORITY)
                // .requestMatchers("/api/pedidos").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));
        return http.build();
    }
}
