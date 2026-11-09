package cl.duoc.eco360.pedidos.admin;

import cl.duoc.eco360.pedidos.admin.dto.BindingRequest;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

@Service
public class RabbitAdminService {

    private final RabbitAdmin rabbitAdmin;

    public RabbitAdminService(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    public void crearCola(String name) {
        rabbitAdmin.declareQueue(new Queue(name, true));
    }

    public void eliminarCola(String name) {
        rabbitAdmin.deleteQueue(name);
    }

    public void crearExchange(String name) {
        rabbitAdmin.declareExchange(
            new DirectExchange(name, true, false)
        );
    }

    public void eliminarExchange(String name) {
        rabbitAdmin.deleteExchange(name);
    }

    public void crearBinding(BindingRequest request) {
        Binding binding = new Binding(
            request.queue(),
            Binding.DestinationType.QUEUE,
            request.exchange(),
            request.routingKey(),
            null
        );

        rabbitAdmin.declareBinding(binding);
    }

    public void eliminarBinding(BindingRequest request) {
        Binding binding = new Binding(
            request.queue(),
            Binding.DestinationType.QUEUE,
            request.exchange(),
            request.routingKey(),
            null
        );

        rabbitAdmin.removeBinding(binding);
    }
}
