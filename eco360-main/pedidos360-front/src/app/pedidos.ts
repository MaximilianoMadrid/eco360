import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { appEnv } from './app.env';

export interface Pedido {
  id: number;
  producto: string;
  estado: string;
}

// Paso 17 de la guia (ng generate service pedidos): la responsabilidad de
// conocer la URL y hacer la llamada HTTP sale del componente y queda aca.
// El Authorization: Bearer se agrega solo via el interceptor (paso 18),
// este servicio no necesita saber nada de tokens.
@Injectable({ providedIn: 'root' })
export class PedidosService {
  private readonly url = appEnv.pedidosEndpoint;

  constructor(private http: HttpClient) {}

  obtenerPedidos(): Observable<Pedido[]> {
    return this.http.get<Pedido[]>(this.url);
  }
}
