package cl.duoc.eco360.pedidos.messaging;

import cl.duoc.eco360.pedidos.config.RabbitNames;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class TicketsConsumer {

    @RabbitListener(queues = RabbitNames.QUEUE_TICKETS)
    public void consumir(Message message, Channel channel) throws Exception {
        long tag = message.getMessageProperties().getDeliveryTag();
        String contenido = new String(message.getBody(), StandardCharsets.UTF_8);

        try {
            if (contenido.contains("ERROR_TEST")) {
                throw new IllegalArgumentException("Error de prueba en tickets");
            }

            System.out.println("[TICKETS] Comprobante procesado: " + contenido);
            channel.basicAck(tag, false);
            System.out.println("[TICKETS] ACK enviado");

        } catch (Exception e) {
            System.err.println("[TICKETS] ERROR: " + e.getMessage());
            channel.basicNack(tag, false, false);
            System.err.println("[TICKETS] NACK enviado - mensaje dirigido a DLQ");
        }
    }
}
