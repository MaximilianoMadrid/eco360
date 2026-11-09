
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { appEnv } from './app.env';

export interface Pedido {
  id: number;
  producto: string;
  cantidad: number;
  estado: string;
  fechaCreacion: string;
}

export interface CrearPedido {
  producto: string;
  cantidad: number;
}

@Injectable({ providedIn: 'root' })
export class PedidosService {
  private readonly url = appEnv.pedidosEndpoint;

  constructor(private http: HttpClient) {}

  // Consultar todos los pedidos
  obtenerPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(this.url);
  }

  // Consultar un pedido por ID
  obtenerPedido(id: number): Observable<Pedido> {
    return this.http.get<Pedido>(`${this.url}/${id}`);
  }

  // Crear un pedido
  crearPedido(datos: CrearPedido): Observable<Pedido> {
    return this.http.post<Pedido>(this.url, datos);
  }

  // Cambiar el estado de un pedido
  actualizarEstado(
    id: number,
    estado: string
  ): Observable<Pedido> {
    return this.http.put<Pedido>(
      `${this.url}/${id}/estado`,
      { estado }
    );
  }

  // Eliminar un pedido
  eliminarPedido(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
