import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

export const joueurGuard: CanActivateFn = (route, state) => {
  const router = inject(Router);
  const matricule = sessionStorage.getItem('matricule');

  if (matricule) {
    return true;
  }

  return router.createUrlTree(['/joueur/connexion']);
};