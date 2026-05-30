// Test de la page d'accueil (Home).
// Pas besoin de backend ici : on teste juste la navigation des deux boutons.

describe("Page d'accueil", () => {

  beforeEach(() => {
    cy.visit('/'); // la route '' charge le composant Home
  });

  it('affiche le titre principal', () => {
    cy.contains('Réserve un terrain.').should('be.visible');
  });

  it('le bouton "Je suis joueur" mène à la connexion joueur', () => {
    cy.contains('button', 'Je suis joueur').click();
    cy.url().should('include', '/joueur/connexion');
  });

  it('le bouton "Je suis administrateur" mène à la connexion admin', () => {
    cy.contains('button', 'Je suis administrateur').click();
    cy.url().should('include', '/admin/connexion');
  });
});
