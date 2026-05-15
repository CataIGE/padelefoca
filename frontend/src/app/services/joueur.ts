import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Joueur } from '../models/joueur.model';
import { Reservation } from '../models/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class JoueurService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  inscrire(nom: string, prenom: string, age: number, telephone: string, email: string): Observable<Joueur> {
    return this.http.post<Joueur>(`${this.apiUrl}/joueurs/inscription`, { nom, prenom, age, telephone, email });
  }

  getProfil(): Observable<Joueur> {
    return this.http.get<Joueur>(`${this.apiUrl}/joueurs/profil`);
  }

  getReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/joueurs/reservations`);
  }
}