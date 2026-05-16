import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../services/admin';
import { Admin } from '../../models/admin.model';

@Component({
  selector: 'app-gestion',
  imports: [FormsModule],
  templateUrl: './gestion.html',
  styleUrl: './gestion.css'
})
export class Gestion {

  private router = inject(Router);
  private adminService = inject(AdminService);

  admins = signal<Admin[]>([]);
  afficherFormulaire = signal<boolean>(false);
  adminEnEdition = signal<Admin | null>(null);

  // Champs formulaire
  nom: string = '';
  prenom: string = '';
  email: string = '';
  motDePasse: string = '';
  typeAdmin: 'GLOBAL' | 'SITE' = 'SITE';
  siteId: number | null = null;

  erreur: string = '';
  chargement: boolean = false;

  constructor() {
    this.chargerAdmins();
  }

  chargerAdmins() {
    this.adminService.getAdmins().subscribe({
      next: (admins) => this.admins.set(admins),
      error: () => this.admins.set([
        { id: 1, nom: 'Dupont', prenom: 'Jean', email: 'jean@padelefoca.be', typeAdmin: 'GLOBAL' },
        { id: 2, nom: 'Martin', prenom: 'Sophie', email: 'sophie@padelefoca.be', typeAdmin: 'SITE', siteId: 1 },
        { id: 3, nom: 'Leroy', prenom: 'Marc', email: 'marc@padelefoca.be', typeAdmin: 'SITE', siteId: 2 },
      ])
    });
  }

  ouvrirFormulaire() {
    this.afficherFormulaire.set(true);
    this.adminEnEdition.set(null);
    this.reinitialiserFormulaire();
  }

  editerAdmin(admin: Admin) {
    this.adminEnEdition.set(admin);
    this.afficherFormulaire.set(true);
    this.nom = admin.nom;
    this.prenom = admin.prenom;
    this.email = admin.email;
    this.typeAdmin = admin.typeAdmin;
    this.siteId = admin.siteId || null;
    this.motDePasse = '';
    this.erreur = '';
  }

  fermerFormulaire() {
    this.afficherFormulaire.set(false);
    this.reinitialiserFormulaire();
  }

  reinitialiserFormulaire() {
    this.nom = '';
    this.prenom = '';
    this.email = '';
    this.motDePasse = '';
    this.typeAdmin = 'SITE';
    this.siteId = null;
    this.erreur = '';
  }

  sauvegarder() {
    if (!this.nom || !this.prenom || !this.email) {
      this.erreur = 'Veuillez remplir tous les champs obligatoires.';
      return;
    }
    if (this.typeAdmin === 'SITE' && !this.siteId) {
      this.erreur = 'Veuillez sélectionner un site pour un admin de site.';
      return;
    }

    this.chargement = true;
    this.erreur = '';

    if (this.adminEnEdition()) {
      this.adminService.modifierAdmin(
        this.adminEnEdition()!.id, this.nom, this.prenom, this.email, this.typeAdmin, this.siteId || undefined
      ).subscribe({
        next: () => { this.chargerAdmins(); this.fermerFormulaire(); this.chargement = false; },
        error: () => { this.erreur = 'Erreur lors de la modification.'; this.chargement = false; }
      });
    } else {
      this.adminService.creerAdmin(
        this.nom, this.prenom, this.email, this.motDePasse, this.typeAdmin, this.siteId || undefined
      ).subscribe({
        next: () => { this.chargerAdmins(); this.fermerFormulaire(); this.chargement = false; },
        error: () => { this.erreur = 'Erreur lors de la création.'; this.chargement = false; }
      });
    }
  }

  supprimerAdmin(admin: Admin) {
    if (confirm(`Supprimer ${admin.prenom} ${admin.nom} ?`)) {
      this.adminService.supprimerAdmin(admin.id).subscribe({
        next: () => this.chargerAdmins(),
        error: () => alert('Erreur lors de la suppression.')
      });
    }
  }

  getNomSite(siteId?: number): string {
    switch (siteId) {
      case 1: return 'Bruxelles';
      case 2: return 'Gand';
      case 3: return 'Namur';
      default: return '';
    }
  }

  retourAccueil() {
    this.router.navigate(['/admin/accueil']);
  }
}