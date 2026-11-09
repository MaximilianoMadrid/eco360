
import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { appEnv } from './app.env';

export interface Producto {
  id: number;
  producto: string;
  stock: number;
  unidad: string;
}

export interface DatosProducto {
  producto: string;
  stock: number;
  unidad: string;
}

@Injectable({ providedIn: 'root' })
export class InventarioService {
  private readonly http = inject(HttpClient);
  private readonly url = appEnv.inventarioEndpoint;

  readonly productos = signal<Producto[]>([]);
  readonly cargando = signal(false);
  readonly error = signal('');

  cargar(): void {
    if (this.cargando()) {
      return;
    }

    this.cargando.set(true);
    this.error.set('');

    this.http.get<Producto[]>(this.url).subscribe({
      next: (datos) => {
        this.productos.set(datos);
        this.cargando.set(false);
      },
      error: (error) => {
        console.error('Error al consultar inventario:', error);
        this.error.set('No fue posible consultar el inventario');
        this.cargando.set(false);
      },
    });
  }

  limpiar(): void {
    this.productos.set([]);
    this.error.set('');
  }

  crearProducto(datos: DatosProducto): Observable<Producto> {
    return this.http.post<Producto>(this.url, datos);
  }

  actualizarProducto(
    id: number,
    datos: DatosProducto
  ): Observable<Producto> {
    return this.http.put<Producto>(`${this.url}/${id}`, datos);
  }

  eliminarProducto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
