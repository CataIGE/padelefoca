export interface Admin {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  typeAdmin: 'GLOBAL' | 'SITE';
  siteId?: number;
}