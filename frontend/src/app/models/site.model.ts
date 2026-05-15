export interface Site {
  id: number;
  nom: string;
  adresse: string;
  nombreTerrains: number;
}

export interface Creneau {
  id: number;
  siteId: number;
  terrainId: number;
  dateHeure: string;
  statut: 'LIBRE' | 'MATCH_PUBLIC' | 'MATCH_PRIVE' | 'COMPLET';
  matchId?: number;
}