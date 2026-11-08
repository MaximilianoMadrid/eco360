import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  signInWithRedirect,
  signOut,
  fetchAuthSession,
  getCurrentUser,
} from 'aws-amplify/auth';
import { PedidosService, Pedido } from './pedidos';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  // --- Sesion (paso 15) ---
  usuario = '';
  token = '';
  autenticado = false;
  verificandoSesion = true;

  // --- Pedidos (pasos 16-18) ---
  pedidos: Pedido[] = [];
  cargandoPedidos = false;
  errorPedidos = '';

  constructor(private pedidosService: PedidosService) {}

  ngOnInit(): void {
    // Al cargar la app, revisamos si ya existe una sesion activa (por
    // ejemplo, volvemos del redirect de Cognito tras hacer login).
    this.verSesion();
  }

  async login() {
    await signInWithRedirect();
  }

  async logout() {
    await signOut();
    this.usuario = '';
    this.token = '';
    this.autenticado = false;
    this.pedidos = [];
  }

  async verSesion() {
    this.verificandoSesion = true;
    try {
      const user = await getCurrentUser();
      const session = await fetchAuthSession();
      this.usuario = user.username;
      this.token = session.tokens?.accessToken?.toString() ?? '';
      this.autenticado = true;
      console.log('Usuario:', user);
      console.log('Access Token:', this.token);
    } catch (error) {
      console.log('No existe sesion', error);
      this.autenticado = false;
    } finally {
      this.verificandoSesion = false;
    }
  }

  // Paso 18.3: el componente ya no necesita tocar el token manualmente,
  // el interceptor lo agrega solo. Usa el PedidosService (paso 17), que
  // internamente llama a API Gateway.
  consultarPedidos() {
    this.cargandoPedidos = true;
    this.errorPedidos = '';
    this.pedidosService.obtenerPedidos().subscribe({
      next: (data) => {
        this.pedidos = data;
        this.cargandoPedidos = false;
      },
      error: (error) => {
        console.error(error);
        this.errorPedidos = 'No fue posible consultar pedidos';
        this.cargandoPedidos = false;
      },
    });
  }
}
