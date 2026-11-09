
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InventarioService, Producto } from './inventario';

@Component({
  selector: 'app-inventario-panel',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './inventario-panel.html',
})
export class InventarioPanel implements OnInit {
  protected readonly inventario = inject(InventarioService);

  protected readonly error = signal('');
  protected readonly ocupado = signal(false);

  // Formulario para agregar materiales
  protected readonly nuevoMaterial = signal('');
  protected readonly nuevoStock = signal<number | null>(null);
  protected readonly nuevaUnidad = signal('kg');

  // Formulario para editar materiales
  protected readonly editandoId = signal<number | null>(null);
  protected readonly editStock = signal<number | null>(null);
  protected readonly editUnidad = signal('');

  ngOnInit(): void {
    this.inventario.cargar();
  }

  actualizar(): void {
    this.inventario.cargar();
  }

  agregar(): void {
    const producto = this.nuevoMaterial().trim();
    const stock = this.nuevoStock();
    const unidad = this.nuevaUnidad().trim();

    if (
      this.ocupado() ||
      !producto ||
      !unidad ||
      stock === null ||
      !Number.isInteger(stock) ||
      stock < 0
    ) {
      return;
    }

    this.ocupado.set(true);
    this.error.set('');

    this.inventario.crearProducto({
      producto,
      stock,
      unidad,
    }).subscribe({
      next: () => {
        this.nuevoMaterial.set('');
        this.nuevoStock.set(null);
        this.ocupado.set(false);
        this.inventario.cargar();
      },
      error: (e) => {
        console.error(e);
        this.error.set('No fue posible agregar el material');
        this.ocupado.set(false);
      },
    });
  }

  editar(item: Producto): void {
    this.error.set('');
    this.editandoId.set(item.id);
    this.editStock.set(item.stock);
    this.editUnidad.set(item.unidad);
  }

  cancelarEdicion(): void {
    this.editandoId.set(null);
  }

  guardar(item: Producto): void {
    const stock = this.editStock();
    const unidad = this.editUnidad().trim();

    if (
      this.ocupado() ||
      stock === null ||
      !Number.isInteger(stock) ||
      stock < 0 ||
      !unidad
    ) {
      return;
    }

    this.ocupado.set(true);
    this.error.set('');

    this.inventario.actualizarProducto(item.id, {
      producto: item.producto,
      stock,
      unidad,
    }).subscribe({
      next: () => {
        this.editandoId.set(null);
        this.ocupado.set(false);
        this.inventario.cargar();
      },
      error: (e) => {
        console.error(e);
        this.error.set('No fue posible actualizar el material');
        this.ocupado.set(false);
      },
    });
  }

  eliminar(item: Producto): void {
    const confirmado = confirm(
      `¿Eliminar ${item.producto} del inventario? ` +
      'Los pedidos activos que utilicen este material ' +
      'podrían verse afectados.'
    );

    if (!confirmado || this.ocupado()) {
      return;
    }

    this.ocupado.set(true);
    this.error.set('');

    this.inventario.eliminarProducto(item.id).subscribe({
      next: () => {
        this.ocupado.set(false);
        this.inventario.cargar();
      },
      error: (e) => {
        console.error(e);
        this.error.set('No fue posible eliminar el material');
        this.ocupado.set(false);
      },
    });
  }
}
