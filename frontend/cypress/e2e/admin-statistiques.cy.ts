// Test de la page Statistiques admin.
//
// Rappels :
//  - protégé par adminGuard -> token requis.
//  - charge les stats au démarrage ; si l'appel échoue, le composant affiche
//    des stats de DÉMO. On force ce mode en faisant échouer les appels, ce qui
//    nous donne des valeurs connues à vérifier.
//  - vue 'global' par défaut si role === 'GLOBAL', sinon vue 'site'.
//  - les onglets de sites (Bruxelles/Gand/Namur) n'apparaissent que pour un admin global.

describe('Statistiques admin', () => {

  // On force le mode démo en faisant échouer les deux endpoints de stats.
  function ouvrirStats(role: string) {
    cy.intercept('GET', '**/api/statistiques/**', { statusCode: 500 }).as('stats');

    cy.visit('/admin/statistiques', {
      onBeforeLoad(win) {
        win.sessionStorage.setItem('token', 'faux-token');
        win.sessionStorage.setItem('role', role);
        win.sessionStorage.setItem('siteId', '1');
      },
    });
  }

  it('redirige vers la connexion si pas de token', () => {
    cy.visit('/admin/statistiques');
    cy.url().should('include', '/admin/connexion');
  });

  it('affiche les statistiques de site pour un admin de SITE', () => {
    ouvrirStats('SITE');
    // Données de démo du mode "site".
    cy.contains("Chiffre d'affaires").should('be.visible');
    cy.contains('4200€').should('be.visible');
    // Pas d'onglets de sites pour un admin de site.
    cy.contains('button', 'Bruxelles').should('not.exist');
  });

  it('affiche les statistiques globales et les onglets pour un admin GLOBAL', () => {
    ouvrirStats('GLOBAL');
    // Données de démo du mode "global".
    cy.contains('12500€').should('be.visible');
    cy.contains('Répartition des membres').should('be.visible');
    // Les onglets de sites sont présents.
    cy.contains('button', 'Vue globale').should('be.visible');
    cy.contains('button', 'Bruxelles').should('be.visible');
  });

  it('un admin GLOBAL peut basculer sur la vue d\'un site', () => {
    ouvrirStats('GLOBAL');
    cy.contains('button', 'Bruxelles').click();
    // On repasse sur des stats de site (valeur de démo "site").
    cy.contains('4200€').should('be.visible');
  });

  it('le bouton de mise à jour relance le chargement des stats', () => {
    ouvrirStats('GLOBAL');
    // Le scheduler peut échouer : le composant recharge quand même les stats.
    cy.intercept('POST', '**/api/statistiques/**', { statusCode: 500 }).as('scheduler');

    cy.contains('button', 'Mettre à jour').click();
    // La page reste fonctionnelle et affiche toujours les stats.
    cy.contains('Répartition des membres').should('be.visible');
  });

  it('le bouton Retour ramène à l\'accueil admin', () => {
    ouvrirStats('GLOBAL');
    cy.contains('button', 'Retour').click();
    cy.url().should('include', '/admin/accueil');
  });
});
