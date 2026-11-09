package cl.duoc.eco360.rabbitadmin.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ExchangeRequest(
    @NotBlank
    @Pattern(regexp = "demo\\.[a-zA-Z0-9._-]+",
             message = "El exchange debe comenzar con demo.")
    String name
) {}
