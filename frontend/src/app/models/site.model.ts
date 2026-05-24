export interface Site {
  id: number;
  nom: string;
  adresse: string;
  heureOuverture: string;
  heureFermeture: string;
  joursRepos: string[];
}

// export interface Creneau {
//   id: number;
//   siteId: number;
//   terrainId: number;
//   dateHeure: string;
//   statut: 'LIBRE' | 'MATCH_PUBLIC' | 'MATCH_PRIVE' | 'COMPLET';
//   matchId?: number;
// }



export interface Creneau {
  heure: string;
  statut: 'LIBRE' | 'MATCH_PUBLIC' | 'MATCH_PRIVE' | 'COMPLET';
  matchId?: number;
  placesDisponibles: number;
}