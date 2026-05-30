// Test de la page Gestion des admins.
//
// Rappels :
//  - protégée par adminGlobalGuard :
//      pas de token            -> /admin/connexion
//      token mais role != GLOBAL -> /admin/accueil
//      token + role == GLOBAL  -> accès autorisé
//  - charge la liste des admins ; si l'appel échoue, affiche une liste de DÉMO
//    (Jean Dupont GLOBAL, Sophie Martin SITE Bruxelles, Marc Leroy SITE Gand).
//  - "Nouvel admin" ouvre un formulaire ; validation des champs obligatoires.
//  - "Supprimer" passe par une confirmation native confirm().

describe('Gestion des admins', () => {

  // Connecte un admin GLOBAL et force la liste de démo (appel en échec).
  function ouvrirGestion() {
    cy.intercept('GET', '**/api/admins**', { statusCode: 500 }).as('admins');

    cy.visit('/admin/gestion', {
      onBeforeLoad(win) {
        win.sessionStorage.setItem('token', 'faux-token');
        win.sessionStorage.setItem('role', 'GLOBAL');
      },
    });
  }

  it('redirige vers la connexion si pas de token', () => {
    cy.visit('/admin/gestion');
    cy.url().should('include', '/admin/connexion');
  });

  it('redirige un admin de SITE vers l\'accueil (accès refusé)', () => {
    cy.visit('/admin/gestion', {
      onBeforeLoad(win) {
        win.sessionStorage.setItem('token', 'faux-token');
        win.sessionStorage.setItem('role', 'SITE');
      },
    });
    cy.url().should('include', '/admin/accueil');
  });

  it('affiche la liste des admins pour un admin GLOBAL', () => {
    ouvrirGestion();
    cy.contains('.admin-card', 'Jean Dupont').should('be.visible');
    cy.contains('.admin-card', 'Sophie Martin').should('be.visible');
    cy.contains('⭐ Global').should('be.visible');
  });

  it('ouvre le formulaire de création au clic sur "Nouvel admin"', () => {
    ouvrirGestion();
    // Le formulaire n'est pas affiché au départ.
    cy.contains('Créer un admin').should('not.exist');

    cy.contains('button', 'Nouvel admin').click();
    cy.contains('Créer un admin').should('be.visible');
  });

  it('affiche une erreur si on sauvegarde un formulaire vide', () => {
    ouvrirGestion();
    cy.contains('button', 'Nouvel admin').click();
    cy.contains('button', 'Créer').click();

    cy.get('.erreur-msg').should('contain', 'remplir tous les champs');
  });

  it('crée un nouvel admin global', () => {
    ouvrirGestion();
    cy.intercept('POST', '**/api/admins**', { statusCode: 200 }).as('creerAdmin');

    cy.contains('button', 'Nouvel admin').click();
    cy.get('input[placeholder="Sophie"]').type('Claire');
    cy.get('input[placeholder="Martin"]').type('Dubois');
    cy.get('input[placeholder="admin@padelefoca.be"]').type('claire@padelefoca.be');
    cy.get('input[type="password"]').type('motdepasse123');
    // On choisit "Admin global" pour éviter d'avoir à sélectionner un site.
    cy.get('select').first().select('GLOBAL');

    cy.contains('button', 'Créer').click();
    cy.wait('@creerAdmin');
  });

  it('demande confirmation avant de supprimer un admin', () => {
    ouvrirGestion();
    cy.intercept('DELETE', '**/api/admins/**', { statusCode: 200 }).as('supprimer');

    // On force la fenêtre de confirmation à répondre "Oui".
    cy.on('window:confirm', () => true);

    cy.contains('.admin-card', 'Sophie Martin')
      .contains('button', 'Supprimer')
      .click();

    cy.wait('@supprimer');
  });

  it('le bouton Retour ramène à l\'accueil admin', () => {
    ouvrirGestion();
    cy.contains('button', 'Retour').click();
    cy.url().should('include', '/admin/accueil');
  });
});
