import { Component, inject } from '@angular/core';
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

  sites = toSignal(this.siteService.getSites(), {
    initialValue: [] as Site[]
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