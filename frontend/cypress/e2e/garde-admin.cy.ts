// Test du guard qui protège les pages admin.
// Le adminGuard regarde s'il y a un "token" dans le sessionStorage :
//  - pas de token  -> redirection vers /admin/connexion
//  - token présent -> accès autorisé

describe('Protection des routes admin (guard)', () => {

  it("redirige vers la connexion si on n'est pas connecté", () => {
    // On essaie d'accéder directement à une page protégée, sans token.
    cy.visit('/admin/accueil');
    cy.url().should('include', '/admin/connexion');
  });

  it("laisse passer si un token est présent", () => {
    // onBeforeLoad permet d'écrire dans le sessionStorage AVANT que l'app démarre,
    // pour que le guard voie le token dès le chargement.
    cy.visit('/admin/accueil', {
      onBeforeLoad(win) {
        win.sessionStorage.setItem('token', 'faux-token-pour-les-tests');
      },
    });
    cy.url().should('include', '/admin/accueil');
  });

  it('redirige aussi les statistiques si non connecté', () => {
    cy.visit('/admin/statistiques');
    cy.url().should('include', '/admin/connexion');
  });
});
