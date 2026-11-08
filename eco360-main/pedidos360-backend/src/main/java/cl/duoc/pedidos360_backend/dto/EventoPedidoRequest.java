package cl.duoc.pedidos360_backend.dto;

public class EventoPedidoRequest {
    private String tipoEvento;
    private String mensaje;

    public EventoPedidoRequest() {}

    public EventoPedidoRequest(String tipoEvento, String mensaje) {
        this.tipoEvento = tipoEvento;
        this.mensaje = mensaje;
    }

    public String getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
