import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Site, Creneau } from '../models/site.model';

@Injectable({
  providedIn: 'root'
})
export class SiteService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  getSites(): Observable<Site[]> {
    return this.http.get<Site[]>(`${this.apiUrl}/sites`);
  }

  getSite(siteId: number): Observable<Site> {
    return this.http.get<Site>(`${this.apiUrl}/sites/${siteId}`);
  }

  getCreneaux(siteId: number, date: string): Observable<Creneau[]> {
    return this.http.get<Creneau[]>(`${this.apiUrl}/sites/${siteId}/creneaux?date=${date}`);
  }
}