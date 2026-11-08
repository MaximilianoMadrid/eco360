package cl.duoc.pedidos360_backend.controller;

import cl.duoc.pedidos360_backend.config.RabbitNames;
import cl.duoc.pedidos360_backend.dto.EventoPedidoRequest;
import cl.duoc.pedidos360_backend.producer.PedidosProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class Pedidos360Controller {

    @Autowired
    private PedidosProducer pedidosProducer;

    @GetMapping("/public/status")
    public ResponseEntity getStatus() {
        Map response = new HashMap();
        response.put("status", "OK");
        response.put("message", "Servicio Eco360 de pedidos funcionando correctamente");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/public/eventos")
    public ResponseEntity publicarEvento(@RequestBody EventoPedidoRequest request) {
        String cola;
        if ("TICKETS".equalsIgnoreCase(request.getTipoEvento())) {
            cola = RabbitNames.QUEUE_TICKETS;
        } else if ("INVENTARIO".equalsIgnoreCase(request.getTipoEvento())) {
            cola = RabbitNames.QUEUE_INVENTARIO;
        } else {
            cola = RabbitNames.QUEUE_NOTIFICACIONES;
        }

        pedidosProducer.enviarEventoPedido(cola, request.getMensaje());

        Map response = new HashMap();
        response.put("status", "EVENTO_ENVIADO");
        response.put("colaDestino", cola);
        response.put("mensaje", request.getMensaje());

        return ResponseEntity.ok(response);
    }
}