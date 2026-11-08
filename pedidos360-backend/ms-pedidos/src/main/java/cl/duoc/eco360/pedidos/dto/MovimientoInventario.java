package cl.duoc.eco360.pedidos.dto;

// Cuerpo que ms-pedidos envia a ms-inventario (reservas y liberaciones).
public record MovimientoInventario(String producto, int cantidad) {
}
