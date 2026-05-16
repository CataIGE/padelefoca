import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-accueil',
  imports: [],
  templateUrl: './accueil.html',
  styleUrl: './accueil.css'
})
export class Accueil {

  private router = inject(Router);
  private authService = inject(Auth);

  role = sessionStorage.getItem('role') || '';
  email = sessionStorage.getItem('email') || 'Administrateur';

  estAdminGlobal(): boolean {
    return this.role === 'GLOBAL';
  }

  getImageAdmin(): string {
  if (this.estAdminGlobal()) return 'AdminGlobal.png';
  const siteId = sessionStorage.getItem('siteId');
  if (siteId === '1') return 'AdminBruxelles.png';
  if (siteId === '2') return 'AdminGand.png';
  if (siteId === '3') return 'AdminNamur.png';
  return 'ImageAdmin.png';
  }

  allerStatistiques() {
    this.router.navigate(['/admin/statistiques']);
  }

  allerGestion() {
    this.router.navigate(['/admin/gestion']);
  }

  seDeconnecter() {
    this.authService.deconnexionAdmin();
  }

  
}