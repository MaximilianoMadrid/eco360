package cl.duoc.pedidos360_backend.listener;

import cl.duoc.pedidos360_backend.config.RabbitNames;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class InventarioConsumer {

    @RabbitListener(queues = RabbitNames.QUEUE_INVENTARIO)
    public void recibirInventario(String mensaje, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            if (mensaje.contains("ERROR") || mensaje.contains("FALLA") || mensaje.contains("INVALIDO")) {
                throw new IllegalArgumentException("Error no recuperable, enviando a DLQ: Producto o estado invalido");
            }
            System.out.println("📦 [InventarioConsumer] Inventario actualizado: " + mensaje);
            channel.basicAck(deliveryTag, false);
        } catch (Exception ex) {
            System.err.println("❌ [InventarioConsumer] Rechazando mensaje, movido a DLQ: " + ex.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
