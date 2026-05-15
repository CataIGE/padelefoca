import { Component, inject, computed } from '@angular/core';
import { Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { SiteService } from '../../services/site';
import { Site } from '../../models/site.model';

@Component({
  selector: 'app-sites',
  imports: [],
  templateUrl: './sites.html',
  styleUrl: './sites.css'
})
export class Sites {

  private router = inject(Router);
  private siteService = inject(SiteService);

  //sites = toSignal(this.siteService.getSites(), { initialValue: [] as Site[] });
  sites = toSignal(this.siteService.getSites(), { 
  initialValue: [
    { id: 1, nom: 'Bruxelles', adresse: 'Avenue du Padel 1, 1000 Bruxelles', nombreTerrains: 3 },
    { id: 2, nom: 'Gand', adresse: 'Padelstraat 2, 9000 Gand', nombreTerrains: 3 },
    { id: 3, nom: 'Namur', adresse: 'Rue du Padel 3, 5000 Namur', nombreTerrains: 3 }
  ] as Site[] 
  });
  typeMembre = sessionStorage.getItem('typeMembre') || 'LIBRE';
  siteIdMembre = sessionStorage.getItem('siteId') ? parseInt(sessionStorage.getItem('siteId')!) : null;

  estSiteDisponible(site: Site): boolean {
    if (this.typeMembre === 'LIBRE' || this.typeMembre === 'GLOBAL') {
      return true;
    }
    return site.id === this.siteIdMembre;
  }

  getDisclaimer(): string {
    if (this.typeMembre === 'LIBRE') {
      return 'En tant que membre libre, vous pouvez réserver sur tous les sites jusqu\'à 5 jours avant le match.';
    }
    if (this.typeMembre === 'SITE') {
      return 'En tant que membre de site, vous pouvez uniquement réserver sur votre site jusqu\'à 2 semaines avant le match.';
    }
    return 'En tant que membre global, vous pouvez réserver sur tous les sites jusqu\'à 3 semaines avant le match.';
  }

  allerCalendrier(site: Site) {
    if (this.estSiteDisponible(site)) {
      this.router.navigate(['/joueur/calendrier', site.id]);
    }
  }

  getImageSite(nomSite: string): string {
  const nom = nomSite.toLowerCase();
  if (nom.includes('bruxelles')) return 'Bruxelles.png';
  if (nom.includes('namur')) return 'Namur.png';
  if (nom.includes('gand')) return 'Gand.png';
  return 'logo.png';
  }
}