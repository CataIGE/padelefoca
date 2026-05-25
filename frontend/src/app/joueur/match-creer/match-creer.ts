import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatchService } from '../../services/match';
import { SlicePipe } from '@angular/common';

@Component({
  selector: 'app-match-creer',
  imports: [FormsModule, SlicePipe],
  templateUrl: './match-creer.html',
  styleUrl: './match-creer.css'
})
export class MatchCreer {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private matchService = inject(MatchService);

  siteId: number = parseInt(this.route.snapshot.params['siteId']);
  dateHeure: string = this.route.snapshot.queryParams['dateHeure'] || '';

  typeMatch = signal<'PUBLIC' | 'PRIVE'>('PUBLIC');
  joueur2: string = '';
  joueur3: string = '';
  joueur4: string = '';
  erreur = signal<string>('');
  chargement: boolean = false;

  choisirType(type: 'PUBLIC' | 'PRIVE') {
    this.typeMatch.set(type);
    this.erreur.set('');
  }

  validerMatricule(matricule: string): boolean {
    const regex = /^[LSG]\d{4}$/;
    return regex.test(matricule);
  }

  creerMatch() {
    if (this.typeMatch() === 'PRIVE') {
      if (!this.joueur2 || !this.joueur3 || !this.joueur4) {
        this.erreur.set('Veuillez entrer les matricules des 3 autres joueurs.');
        return;
      }
      if (!this.validerMatricule(this.joueur2) || !this.validerMatricule(this.joueur3) || !this.validerMatricule(this.joueur4)) {
        this.erreur.set('Un ou plusieurs matricules sont invalides. Format : L1234, S1234 ou G1234');
        return;
      }
    }

    this.erreur.set('');
    this.chargement = true;

    this.matchService.creerMatch(this.siteId, this.dateHeure, this.typeMatch())
      .subscribe({
        next: (match) => {
          if (this.typeMatch() === 'PRIVE') {
            const matricules = [this.joueur2, this.joueur3, this.joueur4];
            this.matchService.ajouterJoueurs(match.id, matricules).subscribe({
              next: () => this.router.navigate(['/joueur/profil']),
              error: (err: any) => {
                this.chargement = false;
                this.erreur.set(err?.error?.message || 'Match créé mais erreur lors de l\'ajout des joueurs.');
              }
            });
          } else {
            this.router.navigate(['/joueur/profil']);
          }
        },
        
      });
  }

  retourCalendrier() {
    this.router.navigate(['/joueur/calendrier', this.siteId]);
  }
}