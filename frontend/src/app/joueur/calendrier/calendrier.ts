import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SiteService } from '../../services/site';
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

  siteId: number = parseInt(this.route.snapshot.params['siteId']);
  dateSelectionnee = signal<string>(this.getDateAujourdhui());
  creneaux = signal<Creneau[]>([]);
  matricule = sessionStorage.getItem('matricule') || '';

  constructor() {
    this.chargerCreneaux();
  }

  getDateAujourdhui(): string {
    return new Date().toISOString().split('T')[0];
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

  estOrganisateur(creneau: Creneau): boolean {
    return false;
  }

  clicCreneau(creneau: Creneau) {
    if (creneau.statut === 'LIBRE') {
      this.router.navigate(['/joueur/match/creer', this.siteId, creneau.id]);
    }
  }

  retourSites() {
    this.router.navigate(['/joueur/sites']);
  }

  getCreneauxMock(): Creneau[] {
    return [
      { id: 1, siteId: this.siteId, terrainId: 1, dateHeure: '2024-05-15T09:00:00', statut: 'LIBRE' },
      { id: 2, siteId: this.siteId, terrainId: 1, dateHeure: '2024-05-15T10:30:00', statut: 'MATCH_PUBLIC' },
      { id: 3, siteId: this.siteId, terrainId: 2, dateHeure: '2024-05-15T12:00:00', statut: 'COMPLET' },
      { id: 4, siteId: this.siteId, terrainId: 2, dateHeure: '2024-05-15T14:00:00', statut: 'LIBRE' },
      { id: 5, siteId: this.siteId, terrainId: 3, dateHeure: '2024-05-15T15:30:00', statut: 'MATCH_PRIVE' },
      { id: 6, siteId: this.siteId, terrainId: 3, dateHeure: '2024-05-15T17:00:00', statut: 'LIBRE' },
    ];
  }
}