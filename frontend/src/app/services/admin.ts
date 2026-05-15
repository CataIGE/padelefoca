import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Admin } from '../models/admin.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api';

  getAdmins(): Observable<Admin[]> {
    return this.http.get<Admin[]>(`${this.apiUrl}/admins`);
  }

  creerAdmin(nom: string, prenom: string, email: string, motDePasse: string, typeAdmin: 'GLOBAL' | 'SITE', siteId?: number): Observable<Admin> {
    return this.http.post<Admin>(`${this.apiUrl}/admins`, { nom, prenom, email, motDePasse, typeAdmin, siteId });
  }

  modifierAdmin(adminId: number, nom: string, prenom: string, email: string, typeAdmin: 'GLOBAL' | 'SITE', siteId?: number): Observable<Admin> {
    return this.http.put<Admin>(`${this.apiUrl}/admins/${adminId}`, { nom, prenom, email, typeAdmin, siteId });
  }

  supprimerAdmin(adminId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/admins/${adminId}`);
  }
}