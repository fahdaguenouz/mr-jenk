import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { HTTP_INTERCEPTORS, provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { JwtInterceptor } from '../core/interceptors/auth.interceptor';
import { ErrorInterceptor } from '../core/interceptors/error.interceptor'; 

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    // Order matters: Attach Token first, then catch Errors
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true }, 
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimations(),
    importProvidersFrom(MatSnackBarModule)
  ]
};