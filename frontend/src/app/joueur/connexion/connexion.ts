import { Component, inject, signal } from '@angular/core';
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

  matricule: string = '';
  nom: string = '';
  prenom: string = '';
  telephone: string = '';
  email: string = '';

  erreur = signal<string>('');
  matriculeGenere = signal<string>('');

  changerOnglet(onglet: string) {
    this.ongletActif = onglet;
    this.erreur.set('');
    this.matriculeGenere.set('');
  }

  seConnecter() {
    if (!this.matricule) {
      this.erreur.set('Veuillez entrer votre matricule.');
      return;
    }
    const regex = /^[LSG]\d{4}$/;
    if (!regex.test(this.matricule)) {
      this.erreur.set('Format invalide. Exemple : L1234, S1234 ou G1234');
      return;
    }
    this.erreur.set('');
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
        this.erreur.set('Matricule introuvable. Vérifiez votre matricule ou créez un compte.');
      }
    });
  }

  sInscrire() {
    if (!this.nom || !this.prenom || !this.email) {
      this.erreur.set('Veuillez remplir tous les champs obligatoires.');
      return;
    }
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(this.email)) {
      this.erreur.set('Veuillez entrer un email valide.');
      return;
    }
    this.erreur.set('');
    this.joueurService.inscrire(this.nom, this.prenom, this.telephone, this.email)
      .subscribe({
        next: (joueur) => {
          this.matriculeGenere.set(joueur.matricule);
          this.nom = '';
          this.prenom = '';
          this.telephone = '';
          this.email = '';
        },
        error: (err: any) => {
          this.erreur.set(err?.error?.message || 'Erreur lors de l\'inscription. Veuillez réessayer.');
        }
      });
  }
}