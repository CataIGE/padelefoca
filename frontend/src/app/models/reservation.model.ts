export interface Reservation {
  id: number;
  joueurMatricule: string;
  joueurNom: string;
  matchId: number;
  siteNom: string;
  terrainNom: string;
  dateHeureMatch: string;
  statutReservation: 'EN_ATTENTE' | 'CONFIRMEE' | 'ANNULEE';
  montantDu: number;
}