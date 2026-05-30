// Test de l'accueil admin (tableau de bord).
//
// Rappels :
//  - protégé par adminGuard -> il faut un 'token' en sessionStorage.
//  - lit 'role' et 'email' du sessionStorage.
//  - la carte "Gestion des admins" n'apparaît QUE si role === 'GLOBAL'.
//  - bouton "Déconnexion" -> vide le sessionStorage et redirige vers /admin/connexion.

describe('Accueil admin', () => {

  // Petite aide : connecte un admin avec le rôle voulu avant de visiter la page.
  function ouvrirAccueil(role: string) {
    cy.visit('/admin/accueil', {
      onBeforeLoad(win) {
        win.sessionStorage.setItem('token', 'faux-token');
        win.sessionStorage.setItem('role', role);
        win.sessionStorage.setItem('email', 'admin@padelefoca.be');
      },
    });
  }

  it('redirige vers la connexion si pas de token', () => {
    cy.visit('/admin/accueil');
    cy.url().should('include', '/admin/connexion');
  });

  it('affiche le tableau de bord et l\'email de l\'admin', () => {
    ouvrirAccueil('SITE');
    cy.contains('Tableau de').should('be.visible');
    cy.contains('admin@padelefoca.be').should('be.visible');
  });

  it('un admin de SITE ne voit PAS la carte de gestion', () => {
    ouvrirAccueil('SITE');
    cy.contains('Gestion des admins').should('not.exist');
    cy.contains('Administrateur de site').should('be.visible');
  });

  it('un admin GLOBAL voit la carte de gestion', () => {
    ouvrirAccueil('GLOBAL');
    cy.contains('Gestion des admins').should('be.visible');
    cy.contains('Administrateur global').should('be.visible');
  });

  it('la carte Statistiques mène à la page des stats', () => {
    ouvrirAccueil('SITE');
    cy.contains('.nav-card', 'Statistiques').click();
    cy.url().should('include', '/admin/statistiques');
  });

  it('la déconnexion vide la session et redirige vers la connexion', () => {
    ouvrirAccueil('GLOBAL');
    cy.contains('button', 'Déconnexion').click();

    cy.url().should('include', '/admin/connexion');
    // Le token doit avoir été retiré du sessionStorage.
    cy.window().then((win) => {
      expect(win.sessionStorage.getItem('token')).to.be.null;
    });
  });
});
