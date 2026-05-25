// export interface Reservation {
//   id: number;
//   matchId: number;
//   joueurMatricule: string;
//   dateReservation: string;
//   statut: 'OUVERTE' | 'EN_COURS' | 'ANNULEE' | 'PAYEE';
//   montant: number;
// }

export interface Reservation {
  id: number;
  joueurMatricule: string;
  joueurNom: string;
  matchId: number;
  siteNom: string;
  terrainNom: string;
  dateHeureMatch: string;
  statutReservation: 'EN_ATTENTE' | 'PAYEE' | 'ANNULEE';
  montantDu: number;
}