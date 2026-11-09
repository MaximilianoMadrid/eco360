package cl.duoc.eco360.pedidos.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record QueueRequest(
    @NotBlank
    @Pattern(regexp = "demo\\.[a-zA-Z0-9._-]+",
             message = "La cola debe comenzar con demo.")
    String name
) {}
