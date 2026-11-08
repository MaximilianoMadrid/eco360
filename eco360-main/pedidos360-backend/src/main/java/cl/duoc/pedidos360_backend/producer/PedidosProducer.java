package cl.duoc.pedidos360_backend.producer;

import cl.duoc.pedidos360_backend.config.RabbitNames;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PedidosProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void enviarEventoPedido(String colaDestino, String mensaje) {
        System.out.println("📤 [PedidosProducer] Publicando evento a la cola [" + colaDestino + "]: " + mensaje);
        rabbitTemplate.convertAndSend(RabbitNames.EXCHANGE_DIRECT, colaDestino, mensaje);
    }
}
