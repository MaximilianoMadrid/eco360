package cl.duoc.eco360.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductoRequest(
        @NotBlank(message = "El nombre del producto es obligatorio") String producto,
        @NotNull(message = "El stock es obligatorio")
        @PositiveOrZero(message = "El stock no puede ser negativo") Integer stock,
        @NotBlank(message = "La unidad es obligatoria") String unidad) {
}
