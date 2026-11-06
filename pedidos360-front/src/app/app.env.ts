// ============================================================================
// Valores del proyecto AWS (paso 2 "ATENCION - Valores del proyecto" de la
// guia). Este es el UNICO archivo que deberias necesitar editar para dejar
// el frontend apuntando a tu laboratorio real. No lo subas a un repo publico
// una vez tenga tus identificadores reales (son datos de laboratorio, no
// credenciales secretas, pero igual conviene mantenerlos fuera de Git si el
// proyecto es publico).
// ============================================================================

export const appEnv = {
  // Cognito -> User pools -> tu user pool -> "User pool ID"
  userPoolId: 'us-east-1_IxZrBeOoF',

  // Cognito -> App clients -> tu app client (publico, SIN client secret)
  userPoolClientId: '2n3j0lvgbeqfbclq12hn5bogff',

  // Cognito -> Branding -> Domain. Solo el host, sin "https://".
  // Ej: 'pedidos360-dev.auth.us-east-1.amazoncognito.com'
  cognitoDomain: 'us-east-1ixzrbeoof.auth.us-east-1.amazoncognito.com',

  // Resource server + scope creados en el paso 9 de la guia.
  // Formato: '<RESOURCE_SERVER_IDENTIFIER>/<SCOPE_NAME>'
  // Ej: 'pedidos360-api/pedidos-api-read'
  customScope: 'rs-api-pedidos/pedidos-read',

  // URL del stage de API Gateway (paso 5), sin slash final.
  // Ej: 'https://abc123xyz.execute-api.us-east-1.amazonaws.com/test'
  apiGatewayBaseUrl: 'https://cw08s1ilad.execute-api.us-east-1.amazonaws.com/test',

  // Ruta completa que consume el PedidosService (apiGatewayBaseUrl + recurso).
  get pedidosEndpoint(): string {
    return `${this.apiGatewayBaseUrl}/api/pedidos`;
  },

  // Debe coincidir EXACTO con la Callback URL / Sign-out URL configuradas
  // en el App Client de Cognito (paso 7.3).
  redirectUrl: 'http://localhost:4200',
};
