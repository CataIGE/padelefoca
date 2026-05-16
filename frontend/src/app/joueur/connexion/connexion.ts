import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Auth } from '../../services/auth';
import { JoueurService } from '../../services/joueur';

@Component({
  selector: 'app-connexion',
  imports: [FormsModule],
  templateUrl: './connexion.html',
  styleUrl: './connexion.css'
})
export class Connexion {

  private router = inject(Router);
  private authService = inject(Auth);
  private joueurService = inject(JoueurService);

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
    const regex = /^[LSG]\d{4}$/;
    if (!regex.test(this.matricule)) {
      this.erreur = 'Format invalide. Exemple : L1234, S1234 ou G1234';
      return;
    }
    this.erreur = '';
    this.authService.connexionJoueur(this.matricule).subscribe({
      next: (joueur) => {
        sessionStorage.setItem('matricule', joueur.matricule);
        sessionStorage.setItem('typeMembre', joueur.typeMembre);
        if (joueur.siteId) {
          sessionStorage.setItem('siteId', joueur.siteId.toString());
        }
        this.router.navigate(['/joueur/sites']);
      },
      error: () => {
        this.erreur = 'Matricule introuvable. Vérifiez votre matricule ou créez un compte.';
      }
    });
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
    this.joueurService.inscrire(this.nom, this.prenom, this.age, this.telephone, this.email)
      .subscribe({
        next: (joueur) => {
          this.matriculeGenere = joueur.matricule;
        },
        error: () => {
          this.erreur = 'Erreur lors de l\'inscription. Veuillez réessayer.';
        }
      });
  }
}