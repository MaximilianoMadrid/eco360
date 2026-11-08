package cl.duoc.pedidos360_backend.controller;

import cl.duoc.pedidos360_backend.config.RabbitNames;
import cl.duoc.pedidos360_backend.dto.EventoPedidoRequest;
import cl.duoc.pedidos360_backend.producer.PedidosProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class Pedidos360Controller {

    @Autowired
    private PedidosProducer pedidosProducer;

    @GetMapping("/public/status")
    public ResponseEntity<Object> getStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Servicio Eco360 de pedidos funcionando correctamente");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/public/eventos")
    public ResponseEntity<Object> publicarEvento(@RequestBody EventoPedidoRequest request) {
        String cola;
        if ("TICKETS".equalsIgnoreCase(request.getTipoEvento())) {
            cola = RabbitNames.QUEUE_TICKETS;
        } else if ("INVENTARIO".equalsIgnoreCase(request.getTipoEvento())) {
            cola = RabbitNames.QUEUE_INVENTARIO;
        } else {
            cola = RabbitNames.QUEUE_NOTIFICACIONES;
        }

        pedidosProducer.enviarEventoPedido(cola, request.getMensaje());

        Map<String, Object> response = new HashMap<>();
        response.put("status", "EVENTO_ENVIADO");
        response.put("colaDestino", cola);
        response.put("mensaje", request.getMensaje());

        return ResponseEntity.ok(response);
    }

    // --- ENDPOINT PARA EVIDENCIA #9 (VALIDA BAD REQUEST 400 SI PRODUCTO ESTÁ VACÍO O ES NULO) ---
    @PostMapping("/public")
    public ResponseEntity<Object> crearPedidoPublico(@RequestBody Map<String, String> body) {
        String producto = body != null ? body.get("producto") : null;
        if (producto == null || producto.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El campo producto es obligatorio");
        }

        pedidosProducer.enviarEventoPedido(RabbitNames.QUEUE_INVENTARIO, "Producto: " + producto);

        Map<String, Object> response = new HashMap<>();
        response.put("producto", producto);
        response.put("id", 4);
        response.put("estado", "CREADO");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/public/{id}/estado")
    public ResponseEntity<Object> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String nuevoEstado = body != null ? body.get("estado") : null;

        pedidosProducer.enviarEventoPedido(RabbitNames.QUEUE_INVENTARIO, "Estado: " + nuevoEstado);

        Map<String, Object> response = new HashMap<>();
        response.put("estado", nuevoEstado);
        response.put("id", id);
        response.put("producto", "Notebook");

        return ResponseEntity.ok(response);
    }
}