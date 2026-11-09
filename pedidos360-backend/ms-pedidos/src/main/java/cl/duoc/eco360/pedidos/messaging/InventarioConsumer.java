package cl.duoc.eco360.pedidos.messaging;

import cl.duoc.eco360.pedidos.config.RabbitNames;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class InventarioConsumer {

    @RabbitListener(queues = RabbitNames.QUEUE_INVENTARIO)
    public void consumir(Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        String contenido = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            if (contenido.contains("ERROR_TEST")) {
                throw new IllegalArgumentException("Error de prueba en inventario");
            }

            // Solo registra el evento: el stock ya fue reservado por PedidoService.
            System.out.println("[INVENTARIO] Evento registrado: " + contenido);
            channel.basicAck(tag, false);
            System.out.println("[INVENTARIO] ACK enviado");

        } catch (Exception e) {
            System.err.println("[INVENTARIO] ERROR: " + e.getMessage());
            channel.basicNack(tag, false, false);
            System.err.println("[INVENTARIO] NACK enviado - mensaje dirigido a DLQ");
        }
    }
}
