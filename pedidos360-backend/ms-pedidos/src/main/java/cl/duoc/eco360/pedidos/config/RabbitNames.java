package cl.duoc.eco360.pedidos.config;

public final class RabbitNames {
    private RabbitNames() {}

    // Exchanges
    public static final String EXCHANGE_DIRECT = "eco360.pedidos.direct";
    public static final String DLX_EXCHANGE = "eco360.dlx";

    // Colas principales
    public static final String QUEUE_NOTIFICACIONES = "eco360.cola.notificaciones";
    public static final String QUEUE_TICKETS = "eco360.cola.tickets";
    public static final String QUEUE_INVENTARIO = "eco360.cola.inventario";

    // Colas de mensajes rechazados
    public static final String DLQ_NOTIFICACIONES = "eco360.cola.notificaciones.dlq";
    public static final String DLQ_TICKETS = "eco360.cola.tickets.dlq";
    public static final String DLQ_INVENTARIO = "eco360.cola.inventario.dlq";
}
