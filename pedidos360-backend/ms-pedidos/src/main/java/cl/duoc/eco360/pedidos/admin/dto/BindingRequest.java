package cl.duoc.eco360.pedidos.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record BindingRequest(
    @NotBlank
    @Pattern(regexp = "demo\\.[a-zA-Z0-9._-]+")
    String exchange,

    @NotBlank
    @Pattern(regexp = "demo\\.[a-zA-Z0-9._-]+")
    String queue,

    @NotBlank
    String routingKey
) {}
