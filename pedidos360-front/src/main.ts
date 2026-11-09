import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { Amplify } from 'aws-amplify';
import { appEnv } from './app/app.env';

// Paso 14 de la guia: Amplify se configura ANTES de bootstrapApplication.
// Los valores salen de app.env.ts (el unico archivo que se deberia editar
// con los identificadores reales de Cognito).
Amplify.configure({
  Auth: {
    Cognito: {
      userPoolId: appEnv.userPoolId,
      userPoolClientId: appEnv.userPoolClientId,
      loginWith: {
        oauth: { 
          domain: appEnv.cognitoDomain,
          scopes: ['email', 'openid', 'profile', ...appEnv.customScopes],
          redirectSignIn: [appEnv.redirectUrl],
          redirectSignOut: [appEnv.redirectUrl],
          responseType: 'code',
        },
      },
    },
  },
});

bootstrapApplication(App, appConfig).catch((err) => console.error(err));
