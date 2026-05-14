import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {

  maintenant: string = this.formaterDate(new Date());

  constructor(private router: Router) {}

  formaterDate(date: Date): string {
    return date.toLocaleDateString('fr-BE', {
      weekday: 'short',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  allerEspaceJoueur() {
    this.router.navigate(['/joueur/connexion']);
  }

  allerEspaceAdmin() {
    this.router.navigate(['/admin/connexion']);
  }
}