export interface Reservation {
  id: number;
  matchId: number;
  joueurMatricule: string;
  dateReservation: string;
  statut: 'OUVERTE' | 'EN_COURS' | 'ANNULEE' | 'PAYEE';
  montant: number;
}