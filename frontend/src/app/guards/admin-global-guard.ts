import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const adminGlobalGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const token = sessionStorage.getItem('token');
  const role = sessionStorage.getItem('role');

  if (token && role === 'GLOBAL') {
    return true;
  }

  if (token && role !== 'GLOBAL') {
    return router.createUrlTree(['/admin/accueil']);
  }

  return router.createUrlTree(['/admin/connexion']);
};