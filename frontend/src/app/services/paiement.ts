import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Paiement } from '../models/paiement.model';

@Injectable({
  providedIn: 'root'
})
export class PaiementService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  payerPlace(reservationId: number): Observable<Paiement> {
    return this.http.post<Paiement>(`${this.apiUrl}/paiements/${reservationId}`, {});
  }

  getPaiements(): Observable<Paiement[]> {
    return this.http.get<Paiement[]>(`${this.apiUrl}/paiements/joueur`);
  }
}