export interface Paiement {
  id: number;
  reservationId: number;
  joueurMatricule: string;
  montant: number;
  datePaiement: string;
  statut: 'EN_ATTENTE' | 'PAYE' | 'REMBOURSE';
  nouveauTypeMembre: string | null;
}