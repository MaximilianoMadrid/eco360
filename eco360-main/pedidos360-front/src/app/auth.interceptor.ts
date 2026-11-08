import { HttpInterceptorFn } from '@angular/common/http';
import { fetchAuthSession } from 'aws-amplify/auth';
import { from, switchMap } from 'rxjs';

// Paso 18.1 de la guia: agrega el Access Token como Bearer automaticamente
// a cada peticion HTTP, sin que cada componente/servicio tenga que
// preocuparse de leerlo manualmente (eso es lo que se hacia "a mano" en el
// paso 16, antes de automatizarlo aqui).
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  return from(fetchAuthSession()).pipe(
    switchMap((session) => {
      const token = session.tokens?.accessToken?.toString();
      if (!token) {
        // Sin sesion activa: se deja pasar la peticion sin Authorization.
        // El backend/API Gateway respondera 401 si el recurso lo exige.
        return next(req);
      }
      const requestConToken = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
      return next(requestConToken);
    })
  );
};
