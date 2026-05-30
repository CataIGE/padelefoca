// Tests transversaux : éléments présents sur toute l'application.
//
// Couvre trois choses :
//  1. La navbar (présente partout) qui ramène à l'accueil au clic.
//  2. La route "**" : une URL inexistante doit rediriger vers l'accueil.
//  3. L'authInterceptor : quand un token est présent, les requêtes HTTP
//     doivent partir avec le header "Authorization: Bearer ...".
//     Quand un matricule est présent, avec le header "X-Matricule".

describe('Navbar (présente sur toutes les pages)', () => {

  it("ramène à l'accueil au clic, depuis la page de connexion admin", () => {
    cy.visit('/admin/connexion');
    cy.get('.navbar').click();
    cy.url().should('eq', Cypress.config('baseUrl') + '/');
  });

  it('affiche le nom du club', () => {
    cy.visit('/admin/connexion');
    cy.get('.navbar').should('contain', 'Padelefoca');
  });
});

describe('Route inexistante (404)', () => {

  it('redirige une URL inconnue vers la page d\'accueil', () => {
    cy.visit('/cette/page/nexiste/pas');
    // La route '**' redirige vers '' (l'accueil).
    cy.url().should('eq', Cypress.config('baseUrl') + '/');
    cy.contains('Réserve un terrain.').should('be.visible');
  });
});

describe('Intercepteur HTTP (authInterceptor)', () => {

  it('ajoute le header Authorization quand un token est présent', () => {
    // On capture la requête de profil pour inspecter ses headers.
    cy.intercept('GET', '**/api/joueurs/profil', (req) => {
      // Le header doit contenir le token sous forme "Bearer ...".
      expect(req.headers).to.have.property('authorization');
      expect(req.headers['authorization']).to.contain('Bearer faux-token');
      req.reply({ statusCode: 200, body: {} });
    }).as('profil');

    cy.intercept('GET', '**/api/joueurs/reservations', { statusCode: 200, body: [] });

    cy.visit('/joueur/profil', {
      onBeforeLoad(win) {
        win.sessionStorage.setItem('matricule', 'L1234');
        win.sessionStorage.setItem('token', 'faux-token-123');
      },
    });

    cy.wait('@profil');
  });

  it('ajoute le header X-Matricule quand seul le matricule est présent', () => {
    cy.intercept('GET', '**/api/joueurs/profil', (req) => {
      // Pas de token ici : l'intercepteur doit utiliser X-Matricule.
      expect(req.headers).to.have.property('x-matricule', 'L1234');
      req.reply({ statusCode: 200, body: {} });
    }).as('profil');

    cy.intercept('GET', '**/api/joueurs/reservations', { statusCode: 200, body: [] });

    cy.visit('/joueur/profil', {
      onBeforeLoad(win) {
        win.sessionStorage.setItem('matricule', 'L1234');
        // pas de token
      },
    });

    cy.wait('@profil');
  });
});
