import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-connexion-admin',
  imports: [FormsModule],
  templateUrl: './connexion.html',
  styleUrl: './connexion.css'
})
export class ConnexionAdmin {

  email: string = '';
  motDePasse: string = '';
  erreur: string = '';
  motDePasseVisible: boolean = false;

  constructor(private router: Router) {}

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
    console.log('Connexion admin avec :', this.email);
  }

  toggleMotDePasse() {
    this.motDePasseVisible = !this.motDePasseVisible;
  }
}