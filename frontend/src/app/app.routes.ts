import { Routes } from '@angular/router';
import { joueurGuard } from './guards/joueur-guard';
import { adminGuard } from './guards/admin-guard';
import { adminGlobalGuard } from './guards/admin-global-guard';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./shared/home/home').then(m => m.Home) },
  { path: 'joueur/connexion', loadComponent: () => import('./joueur/connexion/connexion').then(m => m.Connexion) },
  { path: 'joueur/sites', loadComponent: () => import('./joueur/sites/sites').then(m => m.Sites) },
  { path: 'joueur/calendrier/:siteId', loadComponent: () => import('./joueur/calendrier/calendrier').then(m => m.Calendrier) },
  { path: 'joueur/match/creer/:siteId/:creneau', loadComponent: () => import('./joueur/match-creer/match-creer').then(m => m.MatchCreer) },
  { path: 'joueur/profil', loadComponent: () => import('./joueur/profil/profil').then(m => m.Profil) },
  { path: 'admin/connexion', loadComponent: () => import('./admin/connexion/connexion').then(m => m.ConnexionAdmin) },
  // { path: 'admin/accueil', loadComponent: () => import('./admin/accueil/accueil').then(m => m.Accueil) },
  // { path: 'admin/statistiques', loadComponent: () => import('./admin/statistiques/statistiques').then(m => m.Statistiques) },
  // { path: 'admin/gestion', loadComponent: () => import('./admin/gestion/gestion').then(m => m.Gestion) },
  { path: '**', redirectTo: '' }
];