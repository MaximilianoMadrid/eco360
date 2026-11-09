
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { PedidosService, Pedido } from './pedidos';

type EstadoPedido =
  | 'EN_CLASIFICACION'
  | 'EN_PROCESO'
  | 'LISTO_PARA_DESPACHO'
  | 'DESPACHADO'
  | 'CANCELADO';

@Component({
  selector: 'app-pedidos-panel',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './pedidos-panel.html',
})
export class PedidosPanel implements OnInit {
  private readonly servicio = inject(PedidosService);

  readonly pedidos = signal<Pedido[]>([]);
  readonly cargando = signal(false);
  readonly ocupado = signal(false);
  readonly error = signal('');
  readonly mensaje = signal('');

  readonly producto = signal('');
  readonly cantidad = signal<number | null>(null);

  ngOnInit(): void {
    this.consultar();
  }

  consultar(): void {
    this.cargando.set(true);
    this.error.set('');

    this.servicio.obtenerPedidos().subscribe({
      next: (datos) => {
        this.pedidos.set(datos);
        this.cargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.error.set('No fue posible consultar los pedidos');
        this.cargando.set(false);
      },
    });
  }

  crear(): void {
    const producto = this.producto().trim();
    const cantidad = this.cantidad();

    if (
      this.ocupado() ||
      !producto ||
      cantidad === null ||
      !Number.isInteger(cantidad) ||
      cantidad <= 0
    ) {
      return;
    }

    this.ocupado.set(true);
    this.error.set('');
    this.mensaje.set('');

    this.servicio.crearPedido({ producto, cantidad }).subscribe({
      next: () => {
        this.producto.set('');
        this.cantidad.set(null);
        this.ocupado.set(false);
        this.mensaje.set('Pedido creado correctamente');
        this.consultar();
      },
      error: (err) => {
        console.error(err);
        this.error.set('No fue posible crear el pedido');
        this.ocupado.set(false);
      },
    });
  }

  estadosPermitidos(estado: string): EstadoPedido[] {
    switch (estado) {
      case 'EN_CLASIFICACION':
        return ['EN_PROCESO', 'CANCELADO'];
      case 'EN_PROCESO':
        return ['LISTO_PARA_DESPACHO', 'CANCELADO'];
      case 'LISTO_PARA_DESPACHO':
        return ['DESPACHADO', 'CANCELADO'];
      default:
        return [];
    }
  }

  cambiarEstado(pedido: Pedido, estado: string): void {
    if (
      this.ocupado() ||
      !this.estadosPermitidos(pedido.estado).includes(
        estado as EstadoPedido
      )
    ) {
      return;
    }

    this.ocupado.set(true);
    this.error.set('');
    this.mensaje.set('');

    this.servicio.actualizarEstado(pedido.id, estado).subscribe({
      next: () => {
        this.ocupado.set(false);
        this.mensaje.set('Estado actualizado correctamente');
        this.consultar();
      },
      error: (err) => {
        console.error(err);
        this.error.set('No fue posible cambiar el estado');
        this.ocupado.set(false);
      },
    });
  }

  puedeEliminar(pedido: Pedido): boolean {
    return (
      pedido.estado === 'CANCELADO' ||
      pedido.estado === 'DESPACHADO'
    );
  }

  eliminar(pedido: Pedido): void {
    if (!this.puedeEliminar(pedido) || this.ocupado()) {
      return;
    }

    if (!confirm(`¿Eliminar el pedido ${pedido.id}?`)) {
      return;
    }

    this.ocupado.set(true);
    this.error.set('');
    this.mensaje.set('');

    this.servicio.eliminarPedido(pedido.id).subscribe({
      next: () => {
        this.ocupado.set(false);
        this.mensaje.set('Pedido eliminado correctamente');
        this.consultar();
      },
      error: (err) => {
        console.error(err);
        this.error.set('No fue posible eliminar el pedido');
        this.ocupado.set(false);
      },
    });
  }
}
