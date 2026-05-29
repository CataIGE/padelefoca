import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { StatistiqueService, StatistiquesSite, StatistiquesGlobales } from '../../services/statistique';

@Component({
  selector: 'app-statistiques',
  imports: [],
  templateUrl: './statistiques.html',
  styleUrl: './statistiques.css'
})
export class Statistiques {

  private router = inject(Router);
  private statistiqueService = inject(StatistiqueService);

  role = sessionStorage.getItem('role') || 'SITE';
  siteId = sessionStorage.getItem('siteId') ? parseInt(sessionStorage.getItem('siteId')!) : 1;

  vueSelectionnee = signal<'site' | 'global'>(
  sessionStorage.getItem('role') === 'GLOBAL' ? 'global' : 'site');
  siteSelectionne = signal<number>(1);
  statsSite = signal<StatistiquesSite | null>(null);
  statsGlobal = signal<StatistiquesGlobales | null>(null);

  constructor() {
    this.chargerStats();
  }

  estAdminGlobal(): boolean {
    return this.role === 'GLOBAL';
  }

  chargerStats() {
    if (this.vueSelectionnee() === 'global' && this.estAdminGlobal()) {
      this.statistiqueService.getStatistiquesGlobales().subscribe({
        next: (stats) => this.statsGlobal.set(stats),
        error: () => this.statsGlobal.set({
          chiffreAffairesGlobal: 12500,
          tauxRemplissageGlobal: 72,
          nombreMatchesGlobal: 847,
          nombreJoueursGlobal: 520,
          nombreReservationsAnneeGlobal: 3388,
          repartitionMembres: { libres: 245, site: 180, globaux: 95 }
        })
      });
    } else {
      this.statistiqueService.getStatistiquesSite(this.siteSelectionne()).subscribe({
        next: (stats) => this.statsSite.set(stats),
        error: () => this.statsSite.set({
          siteId: this.siteSelectionne(),
          nomSite: 'Bruxelles',
          chiffreAffaires: 4200,
          tauxRemplissage: 68,
          nombreMatches: 282,
          nombreJoueurs: 180,
          nombreReservationsAnnee: 1128
        })
      });
    }
  }

  changerVue(vue: 'site' | 'global', siteId?: number) {
    this.vueSelectionnee.set(vue);
    if (siteId) this.siteSelectionne.set(siteId);
    this.chargerStats();
  }

  retourAccueil() {
    this.router.navigate(['/admin/accueil']);
  }

  getImageAdmin(): string {
    if (this.estAdminGlobal()) return 'AdminGlobal.png';
    const siteId = sessionStorage.getItem('siteId');
    if (siteId === '1') return 'AdminBruxelles.png';
    if (siteId === '2') return 'AdminGand.png';
    if (siteId === '3') return 'AdminNamur.png';
    return 'ImageAdmin.png';
  }

  mettreAJourStats() {
    this.statistiqueService.runScheduler().subscribe({
      next: () => this.chargerStats(),
      error: () => this.chargerStats()
    });
  }
}