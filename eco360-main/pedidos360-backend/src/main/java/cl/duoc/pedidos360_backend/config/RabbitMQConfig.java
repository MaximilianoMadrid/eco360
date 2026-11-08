package cl.duoc.pedidos360_backend.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- EXCHANGES ---
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(RabbitNames.EXCHANGE_DIRECT, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(RabbitNames.DLX_EXCHANGE, true, false);
    }

    // --- COLA 1: NOTIFICACIONES + DLQ ---
    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(RabbitNames.QUEUE_NOTIFICACIONES)
                .deadLetterExchange(RabbitNames.DLX_EXCHANGE)
                .deadLetterRoutingKey(RabbitNames.QUEUE_NOTIFICACIONES)
                .build();
    }

    @Bean
    public Queue notificacionesDlq() {
        return QueueBuilder.durable(RabbitNames.DLQ_NOTIFICACIONES).build();
    }

    @Bean
    public Binding bindNotificaciones() {
        return BindingBuilder.bind(notificacionesQueue())
                .to(directExchange())
                .with(RabbitNames.QUEUE_NOTIFICACIONES);
    }

    @Bean
    public Binding bindNotificacionesDlq() {
        return BindingBuilder.bind(notificacionesDlq())
                .to(deadLetterExchange())
                .with(RabbitNames.QUEUE_NOTIFICACIONES);
    }

    // --- COLA 2: TICKETS + DLQ ---
    @Bean
    public Queue ticketsQueue() {
        return QueueBuilder.durable(RabbitNames.QUEUE_TICKETS)
                .deadLetterExchange(RabbitNames.DLX_EXCHANGE)
                .deadLetterRoutingKey(RabbitNames.QUEUE_TICKETS)
                .build();
    }

    @Bean
    public Queue ticketsDlq() {
        return QueueBuilder.durable(RabbitNames.DLQ_TICKETS).build();
    }

    @Bean
    public Binding bindTickets() {
        return BindingBuilder.bind(ticketsQueue())
                .to(directExchange())
                .with(RabbitNames.QUEUE_TICKETS);
    }

    @Bean
    public Binding bindTicketsDlq() {
        return BindingBuilder.bind(ticketsDlq())
                .to(deadLetterExchange())
                .with(RabbitNames.QUEUE_TICKETS);
    }

    // --- COLA 3: INVENTARIO + DLQ ---
    @Bean
    public Queue inventarioQueue() {
        return QueueBuilder.durable(RabbitNames.QUEUE_INVENTARIO)
                .deadLetterExchange(RabbitNames.DLX_EXCHANGE)
                .deadLetterRoutingKey(RabbitNames.QUEUE_INVENTARIO)
                .build();
    }

    @Bean
    public Queue inventarioDlq() {
        return QueueBuilder.durable(RabbitNames.DLQ_INVENTARIO).build();
    }

    @Bean
    public Binding bindInventario() {
        return BindingBuilder.bind(inventarioQueue())
                .to(directExchange())
                .with(RabbitNames.QUEUE_INVENTARIO);
    }

    @Bean
    public Binding bindInventarioDlq() {
        return BindingBuilder.bind(inventarioDlq())
                .to(deadLetterExchange())
                .with(RabbitNames.QUEUE_INVENTARIO);
    }
}