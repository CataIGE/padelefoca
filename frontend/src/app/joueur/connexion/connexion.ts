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

  ongletActif: string = 'connexion';

  // Champs connexion
  matricule: string = '';

  // Champs inscription
  nom: string = '';
  prenom: string = '';
  age: number | null = null;
  telephone: string = '';
  email: string = '';

  // Messages
  erreur: string = '';
  matriculeGenere: string = '';

  constructor(private router: Router) {}

  changerOnglet(onglet: string) {
    this.ongletActif = onglet;
    this.erreur = '';
    this.matriculeGenere = '';
  }

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

  sInscrire() {
    if (!this.nom || !this.prenom || !this.age || !this.email) {
      this.erreur = 'Veuillez remplir tous les champs.';
      return;
    }
    if (this.age < 16 || this.age > 99) {
      this.erreur = 'L\'âge doit être compris entre 16 et 99 ans.';
      return;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.erreur = 'Veuillez entrer un email valide.';
      return;
    }
    this.erreur = '';
    this.matriculeGenere = 'L' + Math.floor(1000 + Math.random() * 9000);
    console.log('Inscription réussie, matricule :', this.matriculeGenere);
  }
}