package cl.duoc.eco360.rabbitadmin.admin;

import cl.duoc.eco360.rabbitadmin.admin.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rabbit-admin")
@Validated
public class RabbitAdminController {

    private final RabbitAdminService service;

    public RabbitAdminController(RabbitAdminService service) {
        this.service = service;
    }

    @PostMapping("/queues")
    public ResponseEntity<?> crearCola(
            @Valid @RequestBody QueueRequest request) {

        service.crearCola(request.name());
        return ResponseEntity.ok(
            Map.of("mensaje", "Cola creada", "nombre", request.name())
        );
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<?> eliminarCola(
            @PathVariable
            @Pattern(regexp = "demo\\.[a-zA-Z0-9._-]+")
            String name) {

        service.eliminarCola(name);
        return ResponseEntity.ok(
            Map.of("mensaje", "Solicitud de eliminacion procesada")
        );
    }

    @PostMapping("/exchanges")
    public ResponseEntity<?> crearExchange(
            @Valid @RequestBody ExchangeRequest request) {

        service.crearExchange(request.name());
        return ResponseEntity.ok(
            Map.of("mensaje", "Exchange creado", "nombre", request.name())
        );
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<?> eliminarExchange(
            @PathVariable
            @Pattern(regexp = "demo\\.[a-zA-Z0-9._-]+")
            String name) {

        service.eliminarExchange(name);
        return ResponseEntity.ok(
            Map.of("mensaje", "Solicitud de eliminacion procesada")
        );
    }

    @PostMapping("/bindings")
    public ResponseEntity<?> crearBinding(
            @Valid @RequestBody BindingRequest request) {

        service.crearBinding(request);
        return ResponseEntity.ok(
            Map.of("mensaje", "Binding creado")
        );
    }

    @DeleteMapping("/bindings")
    public ResponseEntity<?> eliminarBinding(
            @Valid @RequestBody BindingRequest request) {

        service.eliminarBinding(request);
        return ResponseEntity.ok(
            Map.of("mensaje", "Solicitud de eliminacion procesada")
        );
    }
}
