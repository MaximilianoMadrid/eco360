package cl.duoc.eco360.pedidos.model;

// Ciclo de vida de un pedido de material reciclado.
// Avanza en orden y puede cancelarse mientras no haya sido despachado.
public enum EstadoPedido {
    EN_CLASIFICACION,
    EN_PROCESO,
    LISTO_PARA_DESPACHO,
    DESPACHADO,
    CANCELADO;

    public boolean puedePasarA(EstadoPedido destino) {
        return switch (this) {
            case EN_CLASIFICACION -> destino == EN_PROCESO || destino == CANCELADO;
            case EN_PROCESO -> destino == LISTO_PARA_DESPACHO || destino == CANCELADO;
            case LISTO_PARA_DESPACHO -> destino == DESPACHADO || destino == CANCELADO;
            case DESPACHADO, CANCELADO -> false;
        };
    }
}
