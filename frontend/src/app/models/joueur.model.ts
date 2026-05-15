export interface Joueur {
  matricule: string;
  nom: string;
  prenom: string;
  age: number;
  telephone?: string;
  email: string;
  typeMembre: 'LIBRE' | 'SITE' | 'GLOBAL';
  siteId?: number;
  penaliteActive: boolean;
  soldeDu: number;
  soldeCredit: number;
}