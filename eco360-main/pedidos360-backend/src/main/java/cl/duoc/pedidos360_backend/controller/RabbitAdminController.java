package cl.duoc.pedidos360_backend.controller;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rabbit")
public class RabbitAdminController {

    private final AmqpAdmin amqpAdmin;

    public RabbitAdminController(AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }

    // --- CREACIÓN ---

    @PostMapping("/queues")
    public ResponseEntity<Object> crearCola(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre de la cola es obligatorio");
        }

        Boolean durableObj = (Boolean) request.get("durable");
        boolean durable = (durableObj != null) ? durableObj : true;

        amqpAdmin.declareQueue(new Queue(name, durable));

        Map<String, Object> response = new HashMap<>();
        response.put("name", name);
        response.put("mensaje", "Cola creada");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/exchanges")
    public ResponseEntity<Object> crearExchange(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del exchange es obligatorio");
        }

        String type = request.containsKey("type") ? (String) request.get("type") : "topic";
        Boolean durableObj = (Boolean) request.get("durable");
        boolean durable = (durableObj != null) ? durableObj : true;

        Exchange exchange;
        if ("direct".equalsIgnoreCase(type)) {
            exchange = new DirectExchange(name, durable, false);
        } else {
            exchange = new TopicExchange(name, durable, false);
        }
        amqpAdmin.declareExchange(exchange);

        Map<String, Object> response = new HashMap<>();
        response.put("name", name);
        response.put("mensaje", "Exchange creado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/bindings")
    public ResponseEntity<Object> crearBinding(@RequestBody Map<String, String> request) {
        String queueName = request.get("queue");
        String exchangeName = request.get("exchange");
        String routingKey = request.get("routingKey");

        Binding binding = new Binding(
                queueName,
                Binding.DestinationType.QUEUE,
                exchangeName,
                routingKey,
                null
        );
        amqpAdmin.declareBinding(binding);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Binding creado");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- ELIMINACIÓN ---

    @DeleteMapping("/bindings")
    public ResponseEntity<Object> eliminarBinding(@RequestBody Map<String, String> request) {
        String queueName = request.get("queue");
        String exchangeName = request.get("exchange");
        String routingKey = request.get("routingKey");

        Binding binding = BindingBuilder
                .bind(new Queue(queueName))
                .to(new TopicExchange(exchangeName))
                .with(routingKey);

        amqpAdmin.removeBinding(binding);

        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Binding eliminado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/exchanges/{name}")
    public ResponseEntity<Object> eliminarExchange(@PathVariable String name) {
        amqpAdmin.deleteExchange(name);

        Map<String, Object> response = new HashMap<>();
        response.put("name", name);
        response.put("mensaje", "Exchange eliminado");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/queues/{name}")
    public ResponseEntity<Object> eliminarCola(@PathVariable String name) {
        amqpAdmin.deleteQueue(name);

        Map<String, Object> response = new HashMap<>();
        response.put("name", name);
        response.put("mensaje", "Cola eliminada");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}