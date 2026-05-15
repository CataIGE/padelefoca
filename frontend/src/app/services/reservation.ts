import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reservation } from '../models/reservation.model';

@Injectable({
  providedIn: 'root'
})
export class ReservationService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  getReservation(reservationId: number): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.apiUrl}/reservations/${reservationId}`);
  }

  rejoindreMatch(matchId: number): Observable<Reservation> {
    return this.http.post<Reservation>(`${this.apiUrl}/reservations/${matchId}`, {});
  }
}