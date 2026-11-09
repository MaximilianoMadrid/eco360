
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  signInWithRedirect,
  signOut,
  fetchAuthSession,
  getCurrentUser,
} from 'aws-amplify/auth';

import { PedidosPanel } from './pedidos-panel';
import { InventarioPanel } from './inventario-panel';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    PedidosPanel,
    InventarioPanel,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  usuario = '';
  autenticado = false;
  verificandoSesion = true;

  seccionActiva: 'pedidos' | 'inventario' = 'pedidos';

  ngOnInit(): void {
    this.verSesion();
  }

  async login(): Promise<void> {
    await signInWithRedirect();
  }

  async logout(): Promise<void> {
    await signOut();

    this.usuario = '';
    this.autenticado = false;
    this.seccionActiva = 'pedidos';
  }

  async verSesion(): Promise<void> {
    this.verificandoSesion = true;

    try {
      const user = await getCurrentUser();
      const session = await fetchAuthSession();

      if (!session.tokens?.accessToken) {
        throw new Error('No existe un token de acceso');
      }

      this.usuario = user.username;
      this.autenticado = true;

      console.log('Sesión autenticada correctamente');
    } catch (error) {
      console.log('No existe una sesión activa');

      this.usuario = '';
      this.autenticado = false;
    } finally {
      this.verificandoSesion = false;
    }
  }

  cambiarSeccion(
    seccion: 'pedidos' | 'inventario'
  ): void {
    this.seccionActiva = seccion;
  }
}
