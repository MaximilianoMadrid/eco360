package cl.duoc.eco360.pedidos.messaging;

import cl.duoc.eco360.pedidos.config.RabbitNames;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PedidosProducer {

    private final RabbitTemplate rabbitTemplate;

    public PedidosProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarPedidoCreado(Long pedidoId, String producto, Integer cantidad) {

        String mensaje = "PEDIDO_CREADO|"
                + pedidoId + "|"
                + producto + "|"
                + cantidad;

        rabbitTemplate.convertAndSend(
                RabbitNames.EXCHANGE_DIRECT,
                RabbitNames.QUEUE_NOTIFICACIONES,
                mensaje
        );

        rabbitTemplate.convertAndSend(
                RabbitNames.EXCHANGE_DIRECT,
                RabbitNames.QUEUE_TICKETS,
                mensaje
        );

        rabbitTemplate.convertAndSend(
                RabbitNames.EXCHANGE_DIRECT,
                RabbitNames.QUEUE_INVENTARIO,
                mensaje
        );

        System.out.println("[PRODUCER] Evento publicado: " + mensaje);
    }
}
