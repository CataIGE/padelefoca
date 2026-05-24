import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Match } from '../models/match.model';

@Injectable({
  providedIn: 'root'
})
export class MatchService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  getMatches(): Observable<Match[]> {
    return this.http.get<Match[]>(`${this.apiUrl}/matches`);
  }

  getMatch(matchId: number): Observable<Match> {
    return this.http.get<Match>(`${this.apiUrl}/matches/${matchId}`);
  }

  getMatchesSite(siteId: number): Observable<Match[]> {
    return this.http.get<Match[]>(`${this.apiUrl}/matches/site/${siteId}`);
  }

  creerMatch(siteId: number, dateHeure: string, typeMatch: 'PUBLIC' | 'PRIVE'): Observable<Match> {
    return this.http.post<Match>(`${this.apiUrl}/matches`, { siteId, dateHeure, typeMatch });
  }

  ajouterJoueurs(matchId: number, matricules: string[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/matches/${matchId}/joueurs`, { matricules });
  }

  annulerMatch(matchId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/matches/${matchId}`);
  }
}