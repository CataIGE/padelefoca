import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-connexion-admin',
  imports: [FormsModule],
  templateUrl: './connexion.html',
  styleUrl: './connexion.css'
})
export class ConnexionAdmin {

  private router = inject(Router);
  private authService = inject(Auth);

  email: string = '';
  motDePasse: string = '';
  erreur: string = '';
  motDePasseVisible: boolean = false;

  seConnecter() {
    if (!this.email || !this.motDePasse) {
      this.erreur = 'Veuillez remplir tous les champs.';
      return;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.erreur = 'Veuillez entrer un email valide.';
      return;
    }
    this.erreur = '';
    this.authService.connexionAdmin(this.email, this.motDePasse).subscribe({
      next: (response) => {
        sessionStorage.setItem('token', response.token);
        sessionStorage.setItem('role', response.typeAdmin);
        sessionStorage.setItem('email', this.email);
        if (response.siteId) {
          sessionStorage.setItem('siteId', response.siteId.toString());
        } 
        this.router.navigate(['/admin/accueil']);
      },
      error: () => {
        this.erreur = 'Email ou mot de passe incorrect.';
      }
    });
  }

  toggleMotDePasse() {
    this.motDePasseVisible = !this.motDePasseVisible;
  }
}