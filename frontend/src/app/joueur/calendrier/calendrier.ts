import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SiteService } from '../../services/site';
import { MatchService } from '../../services/match';
import { Creneau } from '../../models/site.model';
import { SlicePipe } from '@angular/common';

@Component({
  selector: 'app-calendrier',
  imports: [SlicePipe],
  templateUrl: './calendrier.html',
  styleUrl: './calendrier.css'
})
export class Calendrier {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private siteService = inject(SiteService);
  private matchService = inject(MatchService);

  siteId: number = parseInt(this.route.snapshot.params['siteId']);
  dateSelectionnee = signal<string>(this.getDateAujourdhui());
  creneaux = signal<Creneau[]>([]);
  matricule = sessionStorage.getItem('matricule') || '';
  erreur = signal<string>('');

  constructor() {
    this.chargerCreneaux();
  }

  getDateAujourdhui(): string {
    return new Date().toISOString().split('T')[0];
  }

  getDateMax(): string {
    const typeMembre = sessionStorage.getItem('typeMembre') || 'LIBRE';
    const jours = typeMembre === 'GLOBAL' ? 21 : typeMembre === 'SITE' ? 14 : 5;
    const date = new Date();
    date.setDate(date.getDate() + jours);
    return date.toISOString().split('T')[0];
  }

  chargerCreneaux() {
    this.siteService.getCreneaux(this.siteId, this.dateSelectionnee())
      .subscribe({
        next: (creneaux) => this.creneaux.set(creneaux),
        error: () => this.creneaux.set(this.getCreneauxMock())
      });
  }

  changerDate(event: Event) {
    const input = event.target as HTMLInputElement;
    this.dateSelectionnee.set(input.value);
    this.chargerCreneaux();
  }

  getClasseCreneau(creneau: Creneau): string {
    switch (creneau.statut) {
      case 'LIBRE': return 'creneau-libre';
      case 'MATCH_PUBLIC': return 'creneau-public';
      case 'MATCH_PRIVE': return 'creneau-prive';
      case 'COMPLET': return 'creneau-complet';
      default: return '';
    }
  }

  clicCreneau(creneau: Creneau) {
    if (creneau.statut === 'LIBRE') {
      const dateHeure = `${this.dateSelectionnee()}T${creneau.heure}`;
      this.router.navigate(['/joueur/match/creer', this.siteId], {
        queryParams: { dateHeure }
      });
    } else if (creneau.statut === 'MATCH_PUBLIC' && creneau.matchId) {
        this.matchService.rejoindreMatch(creneau.matchId!).subscribe({
        next: () => this.router.navigate(['/joueur/profil']),
        error: (err: any) => this.erreur.set(err?.error?.message || 'Erreur lors de la réservation.')
      });
    }
  }

  retourSites() {
    this.router.navigate(['/joueur/sites']);
  }

  getCreneauxMock(): Creneau[] {
    return [
      { heure: '09:00:00', statut: 'LIBRE', placesDisponibles: 4 },
      { heure: '10:45:00', statut: 'MATCH_PUBLIC', matchId: 1, placesDisponibles: 2 },
      { heure: '12:30:00', statut: 'COMPLET', matchId: 2, placesDisponibles: 0 },
      { heure: '14:15:00', statut: 'LIBRE', placesDisponibles: 4 },
      { heure: '16:00:00', statut: 'MATCH_PRIVE', matchId: 3, placesDisponibles: 1 },
    ];
  }
}