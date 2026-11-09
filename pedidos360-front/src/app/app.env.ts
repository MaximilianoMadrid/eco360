
export const appEnv = {
  userPoolId: 'us-east-1_IxZrBeOoF',

  userPoolClientId: '2n3j0lvgbeqfbclq12hn5bogff',

  cognitoDomain: 'us-east-1ixzrbeoof.auth.us-east-1.amazoncognito.com',

  // Permisos OAuth de los dos microservicios.
  customScopes: [
    'rs-api-pedidos/pedidos-read',
    'rs-api-pedidos/pedidos-write',
    'rs-api-inventario/inventario-read',
    'rs-api-inventario/inventario-write',
  ],

  apiGatewayBaseUrl: 'https://cw08s1ilad.execute-api.us-east-1.amazonaws.com/test',

  get pedidosEndpoint(): string {
    return `${this.apiGatewayBaseUrl}/api/pedidos`;
  },

  get inventarioEndpoint(): string {
    return `${this.apiGatewayBaseUrl}/api/inventario`;
  },

  redirectUrl: 'http://localhost:4200',
};
