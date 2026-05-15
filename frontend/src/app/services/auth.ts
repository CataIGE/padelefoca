import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { Joueur } from '../models/joueur.model';
import { Admin } from '../models/admin.model';

@Injectable({
  providedIn: 'root'
})
export class Auth {

  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = 'http://localhost:8080/api';

  // JOUEUR
  connexionJoueur(matricule: string): Observable<Joueur> {
    return this.http.post<Joueur>(`${this.apiUrl}/auth/joueur/connexion`, { matricule })
      .pipe(
        tap(joueur => {
          sessionStorage.setItem('matricule', joueur.matricule);
          sessionStorage.setItem('typeMembre', joueur.typeMembre);
        })
      );
  }

  // ADMIN
  connexionAdmin(email: string, motDePasse: string): Observable<{token: string, admin: Admin}> {
    return this.http.post<{token: string, admin: Admin}>(`${this.apiUrl}/auth/admin/connexion`, { email, motDePasse })
      .pipe(
        tap(response => {
          sessionStorage.setItem('token', response.token);
          sessionStorage.setItem('role', response.admin.typeAdmin);
        })
      );
  }

  deconnexionJoueur() {
    sessionStorage.removeItem('matricule');
    sessionStorage.removeItem('typeMembre');
    this.router.navigate(['/joueur/connexion']);
  }

  deconnexionAdmin() {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('role');
    this.router.navigate(['/admin/connexion']);
  }

  getMatricule(): string | null {
    return sessionStorage.getItem('matricule');
  }

  getToken(): string | null {
    return sessionStorage.getItem('token');
  }

  estConnecteJoueur(): boolean {
    return !!sessionStorage.getItem('matricule');
  }

  estConnecteAdmin(): boolean {
    return !!sessionStorage.getItem('token');
  }
}