package cl.duoc.pedidos360_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Scope personalizado configurado en Amazon Cognito.
    private static final String CUSTOM_SCOPE_AUTHORITY =
            "SCOPE_rs-api-pedidos/pedidos-read";

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/pedidos")
                .hasAuthority(CUSTOM_SCOPE_AUTHORITY)
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> {})
            );

        return http.build();
    }
}

