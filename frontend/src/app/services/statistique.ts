import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface StatistiquesSite {
  siteId: number;
  nomSite: string;
  chiffreAffaires: number;
  tauxRemplissage: number;
  nombreMatches: number;
  nombreJoueurs: number;
  nombreReservationsAnnee: number;
}

export interface StatistiquesGlobales {
  chiffreAffairesGlobal: number;
  tauxRemplissageGlobal: number;
  nombreMatchesGlobal: number;
  nombreJoueursGlobal: number;
  nombreReservationsAnneeGlobal: number;
  repartitionMembres: {
    libres: number;
    site: number;
    globaux: number;
  };
}

@Injectable({
  providedIn: 'root'
})
export class StatistiqueService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  getStatistiquesSite(siteId: number): Observable<StatistiquesSite> {
    return this.http.get<StatistiquesSite>(`${this.apiUrl}/admin/statistiques/site/${siteId}`);
  }

  getStatistiquesGlobales(): Observable<StatistiquesGlobales> {
    return this.http.get<StatistiquesGlobales>(`${this.apiUrl}/admin/statistiques/global`);
  }
}