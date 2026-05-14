import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-connexion',
  imports: [FormsModule],
  templateUrl: './connexion.html',
  styleUrl: './connexion.css'
})
export class Connexion {

  matricule: string = '';
  erreur: string = '';

  constructor(private router: Router) {}

  seConnecter() {
    if (!this.matricule) {
      this.erreur = 'Veuillez entrer votre matricule.';
      return;
    }
    const regex = /^L\d{4}$/;
    if (!regex.test(this.matricule)) {
      this.erreur = 'Format invalide. Exemple : L1234';
      return;
    }
    this.erreur = '';
    console.log('Connexion avec le matricule :', this.matricule);
  }

  allerInscription() {
    this.router.navigate(['/joueur/inscription']);
  }
}