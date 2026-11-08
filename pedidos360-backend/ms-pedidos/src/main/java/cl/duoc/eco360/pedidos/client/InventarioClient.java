package cl.duoc.eco360.pedidos.client;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import cl.duoc.eco360.pedidos.dto.MovimientoInventario;
import cl.duoc.eco360.pedidos.exception.ApiException;

// Llama a ms-inventario reenviando el mismo access token del usuario.
// ms-inventario lo valida por su cuenta, asi que el token debe incluir el scope
// rs-api-inventario/inventario-write (Angular lo pide al iniciar sesion).
@Component
public class InventarioClient {

    private final RestClient restClient;

    public InventarioClient(@Value("${eco360.inventario.base-url}") String baseUrl) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public void reservar(String producto, int cantidad) {
        enviar("/api/inventario/reservas", producto, cantidad);
    }

    public void liberar(String producto, int cantidad) {
        enviar("/api/inventario/liberaciones", producto, cantidad);
    }

    private void enviar(String ruta, String producto, int cantidad) {
        try {
            restClient.post()
                    .uri(ruta)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenActual())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new MovimientoInventario(producto, cantidad))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            int codigo = e.getStatusCode().value();
            if (codigo == 404) {
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "El producto '" + producto + "' no existe en el inventario");
            }
            if (codigo == 409) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "Stock insuficiente de '" + producto + "' para " + cantidad);
            }
            if (codigo == 401 || codigo == 403) {
                throw new ApiException(HttpStatus.FORBIDDEN,
                        "El token no tiene permiso en inventario (falta el scope inventario-write)");
            }
            throw new ApiException(HttpStatus.BAD_GATEWAY, "ms-inventario rechazo la solicitud (" + codigo + ")");
        } catch (RestClientException e) {
            throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "ms-inventario no esta disponible");
        }
    }

    private String tokenActual() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion instanceof JwtAuthenticationToken jwt) {
            return jwt.getToken().getTokenValue();
        }
        throw new ApiException(HttpStatus.UNAUTHORIZED, "No hay un token valido para consultar inventario");
    }
}
