# Eco360 · EA1 FullStack Cloud Native en AWS

Plataforma de logística y gestión de retiro de material reciclado para un
emprendimiento de productos reciclados. Es el mismo proyecto `pedidos360-dev-max`
original, con el nombre/marca actualizados a **Eco360**; por ahora el modelo
de datos interno (clases Java, campos `producto`/`estado`) se mantiene igual
y solo cambió lo visible (título, encabezado, estilos). Si más adelante
quieres que el modelo hable de "material reciclado", "peso" o "punto de
retiro" en vez de "producto"/"estado", es un cambio aparte que podemos hacer
cuando quieras.

Sigue la Guía Práctica EA1 (DSY1107). Incluye:

- `pedidos360-backend/` — Spring Boot (ya traído de tu zip, con el
  `SecurityConfig` completado hasta el paso 21).
- `pedidos360-front/` — Angular + AWS Amplify (pasos 13 al 19), escrito a
  mano siguiendo exactamente la estructura que genera `ng new`.

## Qué se hizo en el backend

- Se corrigió un error de sintaxis en `pom.xml` (una `s` suelta que
  rompía el build).
- `SecurityConfig.java` ahora exige el custom scope
  (`.hasAuthority(CUSTOM_SCOPE_AUTHORITY)`, paso 21) en vez de quedarse en
  `.authenticated()`. Si tu scope real en Cognito no es
  `rs-api-pedidos/pedidos-read`, actualiza la constante
  `CUSTOM_SCOPE_AUTHORITY` en ese archivo.
- `application.properties` ya tenía tu `issuer-uri` de Cognito cargado.

## Qué se hizo en el frontend (nuevo)

Siguiendo los pasos 13 a 19 de la guía:

| Paso | Archivo |
|---|---|
| 13-14 Proyecto + Amplify | `src/main.ts`, `src/app/app.env.ts` |
| 15 Login/logout/sesión | `src/app/app.ts`, `src/app/app.html` |
| 16-17 Service con HttpClient | `src/app/pedidos.ts` |
| 18 Interceptor JWT | `src/app/auth.interceptor.ts`, registrado en `src/app/app.config.ts` |
| 19 Guards | No se agregó (la guía indica agregarlo solo cuando existan rutas privadas; este laboratorio tiene una sola vista) |

Todos los valores de Cognito/API Gateway están centralizados en
**`pedidos360-front/src/app/app.env.ts`** — es el único archivo que
necesitas editar con tus datos reales.

## Importante: build no verificado en este entorno

Este entorno de trabajo no tiene salida a `registry.npmjs.org`, así que no
pude correr `npm install` ni `ng build` para compilar y verificar el
frontend. El código sigue al pie de la letra los snippets de la guía y la
estructura estándar de Angular CLI, pero **debes verificarlo tú al bajarlo**:

```bash
cd pedidos360-front
npm install
npm start      # http://localhost:4200
```

Si `npm install` marca algún conflicto de versiones de Angular/Amplify,
ajusta las versiones en `package.json` a las que tengas instaladas
localmente (`ng version`).

## Checklist de valores AWS que debes completar

Estos son 100% los pasos de AWS que la guía pide y que no se pueden hacer
desde código (según acordamos, quedan fuera de este armado):

1. **Backend en EC2** (sección 4): subir el JAR a S3, instancia con IAM
   Role, Java 17, ejecutar en el puerto 8888.
2. **API Gateway** (sección 5): crear la REST API, recurso `/api/pedidos`,
   integración HTTP hacia tu EC2, deploy a stage `test`.
3. **CORS** (sección 6): habilitar en `/api/pedidos` con
   `Allowed Origin: http://localhost:4200`.
4. **Cognito** (secciones 7 y 9): User Pool, App Client público, dominio,
   Callback/Sign-out URL = `http://localhost:4200`, Resource Server +
   custom scope.
5. **Cognito Authorizer en API Gateway** (sección 11): crear el authorizer
   tipo Cognito y asociarlo al método GET con el scope exacto.

Con esos 5 puntos resueltos en la consola de AWS, copia los valores en
`pedidos360-front/src/app/app.env.ts`:

```ts
userPoolId: '<tu User Pool ID>',
userPoolClientId: '<tu App Client ID>',
cognitoDomain: '<tu dominio, sin https://>',
customScope: '<resource-server-id>/<scope-name>',
apiGatewayBaseUrl: 'https://<API_ID>.execute-api.<REGION>.amazonaws.com/test',
```

Y en `pedidos360-backend/src/main/resources/application.properties` ya
está tu `issuer-uri`; solo confirma que el `USER_POOL_ID` sea el correcto.

## Prueba E2E (sección 29 de la guía)

Una vez completados los 5 puntos de AWS, el orden recomendado para probar
es: backend local → backend EC2 → API Gateway sin seguridad → Cognito
emite token → API Gateway protegido rechaza sin token → Postman con token
responde 200 → Angular (login → sesión → consultar pedidos).
