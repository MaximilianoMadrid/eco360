package cl.duoc.eco360.pedidos.messaging;

import cl.duoc.eco360.pedidos.config.RabbitNames;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class NotificacionesConsumer {

    private static final int MAX_INTENTOS = 3;

    @RabbitListener(queues = RabbitNames.QUEUE_NOTIFICACIONES)
    public void consumir(Message message, Channel channel) throws Exception {

        long tag = message.getMessageProperties().getDeliveryTag();
        String contenido = new String(message.getBody(), StandardCharsets.UTF_8);

        // Error permanente: no se reintenta
        if (contenido.contains("ERROR_TEST")) {
            System.err.println("[NOTIFICACIONES] ERROR PERMANENTE: " + contenido);
            channel.basicNack(tag, false, false);
            System.err.println("[NOTIFICACIONES] NACK - mensaje enviado a DLQ");
            return;
        }

        for (int intento = 1; intento <= MAX_INTENTOS; intento++) {
            try {
                System.out.println("[NOTIFICACIONES] Intento "
                        + intento + "/" + MAX_INTENTOS
                        + " - " + contenido);

                // Simulacion de error temporal que siempre falla
                if (contenido.contains("ERROR_TEMPORAL")
                        && !contenido.contains("ERROR_TEMPORAL_OK")) {
                    throw new IllegalStateException("Servicio temporalmente no disponible");
                }

                // Simulacion de recuperacion en el segundo intento
                if (contenido.contains("ERROR_TEMPORAL_OK") && intento == 1) {
                    throw new IllegalStateException("Fallo temporal simulado");
                }

                System.out.println("[NOTIFICACIONES] Mensaje procesado: " + contenido);

                channel.basicAck(tag, false);
                System.out.println("[NOTIFICACIONES] ACK enviado");
                return;

            } catch (IllegalStateException e) {
                System.err.println("[NOTIFICACIONES] Error recuperable: "
                        + e.getMessage());

                if (intento == MAX_INTENTOS) {
                    channel.basicNack(tag, false, false);
                    System.err.println("[NOTIFICACIONES] Reintentos agotados - NACK a DLQ");
                    return;
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    channel.basicNack(tag, false, true);
                    System.err.println("[NOTIFICACIONES] Interrupcion - mensaje reencolado");
                    return;
                }
            }
        }
    }
}
