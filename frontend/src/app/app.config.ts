import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpInterceptorFn } from '@angular/common/http';
import { routes } from './app.routes';

const authInterceptor: HttpInterceptorFn = (req, next) => {
  const matricule = sessionStorage.getItem('matricule');
  const token = sessionStorage.getItem('token');

  if (token) {
    const reqClone = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(reqClone);
  }

  if (matricule) {
    const reqClone = req.clone({
      setHeaders: { 'X-Matricule': matricule }
    });
    return next(reqClone);
  }

  return next(req);
};

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor]))
  ]
};