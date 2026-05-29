import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { JoueurService } from '../../services/joueur';
import { PaiementService } from '../../services/paiement';
import { Joueur } from '../../models/joueur.model';
import { Reservation } from '../../models/reservation.model';
import { SlicePipe } from '@angular/common';

@Component({
  selector: 'app-profil',
  imports: [SlicePipe],
  templateUrl: './profil.html',
  styleUrl: './profil.css'
})
export class Profil {

  private router = inject(Router);
  private joueurService = inject(JoueurService);
  private paiementService = inject(PaiementService);

  joueur = toSignal(this.joueurService.getProfil(), {
    initialValue: {
      matricule: sessionStorage.getItem('matricule') || '',
      nom: 'Nom',
      prenom: 'Prénom',
      age: 0,
      telephone: '',
      email: 'email@exemple.com',
      typeMembre: 'LIBRE' as 'LIBRE' | 'SITE' | 'GLOBAL',
      penaliteActive: false,
      soldeDu: 0,
      soldeCredit: 0
    } as Joueur
  });

  reservations = toSignal(this.joueurService.getReservations(), {
    initialValue: [] as Reservation[]
  });

  getLibelleTypeMembre(): string {
    switch (this.joueur().typeMembre) {
      case 'LIBRE': return 'Membre libre';
      case 'SITE': return 'Membre du site';
      case 'GLOBAL': return 'Membre global';
    }
  }

  getDisclaimerTypeMembre(): string {
    switch (this.joueur().typeMembre) {
      case 'LIBRE': return 'Réservation jusqu\'à 5 jours avant le match sur tous les sites.';
      case 'SITE': return 'Réservation jusqu\'à 2 semaines avant le match sur votre site uniquement.';
      case 'GLOBAL': return 'Réservation jusqu\'à 3 semaines avant le match sur tous les sites.';
    }
  }

  getClasseStatut(statut: string): string {
    switch (statut) {
      case 'EN_ATTENTE': return 'statut-ouverte';
      case 'CONFIRMEE': return 'statut-payee';
      case 'ANNULEE': return 'statut-annulee';
      default: return '';
    }
  }

  promotionMessage: string | null = null;
  
  constructor() {
    const msg = sessionStorage.getItem('promotionMessage');
    if (msg) {
      this.promotionMessage = msg;
    }
  }

  payer(reservationId: number) {
    this.paiementService.payerPlace(reservationId).subscribe({
      next: (response: any) => {
        const ancienMatricule = sessionStorage.getItem('matricule') || '';
        const nouveauTypeMembre = response.nouveauTypeMembre;
        const ancienTypeMembre = sessionStorage.getItem('typeMembre') || 'LIBRE';

        if (nouveauTypeMembre && nouveauTypeMembre !== ancienTypeMembre) {
          const prefixe = nouveauTypeMembre === 'SITE' ? 'S' : 'G';
          const nouveauMatricule = prefixe + ancienMatricule.substring(1);
          
          sessionStorage.setItem('matricule', nouveauMatricule);
          sessionStorage.setItem('typeMembre', nouveauTypeMembre);
          
          if (nouveauTypeMembre === 'GLOBAL') {
            sessionStorage.removeItem('siteId');
            sessionStorage.setItem('promotionMessage', '🎉 Félicitations ! Vous êtes désormais Membre Global. Vous pouvez réserver jusqu\'à 3 semaines à l\'avance sur tous les sites !');
          } else if (nouveauTypeMembre === 'SITE') {
            if (response.siteId) {
              sessionStorage.setItem('siteId', response.siteId.toString());
            }
            sessionStorage.setItem('promotionMessage', '🎉 Félicitations ! Vous êtes désormais Membre du Site. Vous pouvez réserver jusqu\'à 2 semaines à l\'avance sur votre site !');
          }
        }
        window.location.reload();
      },
      error: (err: any) => {
        console.log('Erreur paiement:', err);
      }
    });
  }

  fermerPopup() {
    this.promotionMessage = null;
    sessionStorage.removeItem('promotionMessage');
  }

  retourSites() {
    this.router.navigate(['/joueur/sites']);
  }

  annuler(reservationId: number) {
    this.joueurService.annulerReservation(reservationId).subscribe({
      next: () => window.location.reload(),
      error: (err: any) => console.log('Erreur annulation:', err)
    });
  }
}