package cl.duoc.eco360.pedidos.dto;

import cl.duoc.eco360.pedidos.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(
        @NotNull(message = "El estado es obligatorio") EstadoPedido estado) {
}
