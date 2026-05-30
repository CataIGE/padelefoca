// Test du parcours de connexion joueur (par matricule) et de l'inscription.
// À noter : ce composant utilise des signals (erreur = signal('')),
// donc les messages d'erreur asynchrones s'affichent correctement,
// contrairement au bug qu'on a corrigé côté admin.

describe('Connexion joueur', () => {

  beforeEach(() => {
    cy.visit('/joueur/connexion');
  });

  it('affiche une erreur si le matricule est vide', () => {
    cy.contains('button', 'Continuer').click();
    cy.get('.erreur-msg').should('contain', 'Veuillez entrer votre matricule.');
  });

  it('affiche une erreur si le format du matricule est invalide', () => {
    cy.get('input[placeholder="L1234"]').type('1234'); // format incorrect
    cy.contains('button', 'Continuer').click();
    cy.get('.erreur-msg').should('contain', 'Format invalide');
  });

  it('connecte le joueur et le redirige vers les sites', () => {
    cy.intercept('POST', '**/api/auth/joueur/connexion', {
      statusCode: 200,
      body: { matricule: 'L1234', typeMembre: 'STANDARD', siteId: 1 },
    }).as('connexionJoueur');

    cy.get('input[placeholder="L1234"]').type('L1234');
    cy.contains('button', 'Continuer').click();

    cy.wait('@connexionJoueur');
    cy.url().should('include', '/joueur/sites');
  });

  it('affiche une erreur si le matricule est introuvable', () => {
    cy.intercept('POST', '**/api/auth/joueur/connexion', {
      statusCode: 404,
      body: { message: 'Not found' },
    }).as('connexionEchouee');

    cy.get('input[placeholder="L1234"]').type('G9999');
    cy.contains('button', 'Continuer').click();

    cy.wait('@connexionEchouee');
    cy.get('.erreur-msg').should('contain', 'Matricule introuvable');
  });

  it("bascule sur l'onglet inscription et génère un matricule", () => {
    cy.intercept('POST', '**/api/joueurs/inscription', {
      statusCode: 200,
      body: { matricule: 'L5678' },
    }).as('inscription');

    // On clique sur l'onglet "Créer un compte"
    cy.contains('button', 'Créer un compte').click();

    cy.get('input[placeholder="Lina"]').type('Lina');
    cy.get('input[placeholder="Verhoeven"]').type('Verhoeven');
    cy.get('input[placeholder="vous@email.com"]').type('lina@email.com');
    cy.contains('button', 'Générer mon matricule').click();

    cy.wait('@inscription');
    cy.get('.succes-msg').should('contain', 'L5678');
  });
});
