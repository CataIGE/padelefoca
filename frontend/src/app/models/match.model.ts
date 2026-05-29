export interface Match {
  id: number;
  siteId: number;
  siteNom: string;
  terrainId: number;
  terrainNom: string;
  dateHeure: string;
  typeMatch: 'PUBLIC' | 'PRIVE';
  typeMatch: 'PUBLIC' | 'PRIVE';
  organisateurMatricule: string;
  organisateurNom: string;
  joueurs: string[];
  nombreJoueurs: number;
  placesDisponibles: number;
  statutMatch: 'PLANIFIE' | 'COMPLET' | 'ANNULE';
  joueursMatricules: string[];
  organisateurReservationId: number | null;
}