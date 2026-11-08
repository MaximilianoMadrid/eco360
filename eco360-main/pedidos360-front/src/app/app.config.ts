import { ApplicationConfig,  provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './auth.interceptor';

// Paso 18.2 de la guia: provideHttpClient(...) con el interceptor DEBE
// quedar DENTRO de providers. Si queda fuera (por ejemplo despues de cerrar
// el arreglo), el archivo compila igual pero el interceptor nunca se
// registra y el JWT no se agrega a las peticiones.
export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([authInterceptor])
    ),
  ],
};
