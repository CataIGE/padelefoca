export interface Match {
  id: number;
  siteId: number;
  terrainId: number;
  dateHeure: string;
  typeMatch: 'PUBLIC' | 'PRIVE';
  organisateurMatricule: string;
  joueurs: string[];
  placesDisponibles: number;
  statut: 'OUVERT' | 'COMPLET' | 'ANNULE';
  prixParJoueur: number;
}