package cl.duoc.eco360.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Cuerpo de /api/inventario/reservas y /api/inventario/liberaciones.
public record MovimientoRequest(
        @NotBlank(message = "El producto es obligatorio") String producto,
        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a 0") Integer cantidad) {
}
