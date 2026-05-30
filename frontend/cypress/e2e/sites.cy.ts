// Test de la liste des sites (côté joueur).
//
// Rappels sur le composant Sites :
//  - protégé par le joueurGuard -> matricule requis en sessionStorage.
//  - charge les sites au démarrage via toSignal : GET /api/sites
//    (-> on pose l'intercept AVANT cy.visit).
//  - disponibilité selon le typeMembre :
//      LIBRE / GLOBAL -> tous les sites accessibles
//      SITE           -> seulement le site dont l'id == siteId en session
//  - clic sur un site disponible -> /joueur/calendrier/:id
//  - clic sur un site non disponible -> rien ne se passe.
//  - le disclaimer dépend du typeMembre.

describe('Liste des sites', () => {

  // 3 sites de test renvoyés par l'API simulée.
  const sites = [
    { id: 1, nom: 'Bruxelles', adresse: 'Rue 1', heureOuverture: '08:00:00', heureFermeture: '22:00:00', joursRepos: ['MONDAY'] },
    { id: 2, nom: 'Gand', adresse: 'Rue 2', heureOuverture: '08:00:00', heureFermeture: '22:00:00', joursRepos: ['TUESDAY'] },
    { id: 3, nom: 'Namur', adresse: 'Rue 3', heureOuverture: '08:00:00', heureFermeture: '22:00:00', joursRepos: ['SUNDAY'] },
  ];

  // Ouvre la page avec un typeMembre donné (et un siteId pour les membres SITE).
  function ouvrirSites(typeMembre: string, siteId?: string) {
    cy.intercept('GET', '**/api/sites', { statusCode: 200, body: sites }).as('sites');

    cy.visit('/joueur/sites', {
      onBeforeLoad(win) {
        win.sessionStorage.setItem('matricule', 'L1234');
        win.sessionStorage.setItem('typeMembre', typeMembre);
        if (siteId) win.sessionStorage.setItem('siteId', siteId);
      },
    });

    cy.wait('@sites');
  }

  it("redirige vers la connexion si le joueur n'est pas connecté", () => {
    cy.clearAllSessionStorage();
    cy.visit('/joueur/sites');
    cy.url().should('include', '/joueur/connexion');
  });

  it('affiche les trois sites', () => {
    ouvrirSites('LIBRE');
    cy.contains('.site-card', 'Bruxelles').should('be.visible');
    cy.contains('.site-card', 'Gand').should('be.visible');
    cy.contains('.site-card', 'Namur').should('be.visible');
  });

  it('affiche le bon disclaimer pour un membre libre', () => {
    ouvrirSites('LIBRE');
    cy.contains('membre libre').should('be.visible');
  });

  it('un membre LIBRE peut accéder à tous les sites', () => {
    ouvrirSites('LIBRE');
    // Tous les sites doivent être marqués comme disponibles.
    cy.get('.site-disponible').should('have.length', 3);
    cy.get('.site-grise').should('not.exist');
  });

  it('un membre SITE ne peut accéder qu\'à son propre site', () => {
    // Membre du site 2 (Gand).
    ouvrirSites('SITE', '2');
    // Un seul site disponible, les deux autres grisés.
    cy.get('.site-disponible').should('have.length', 1);
    cy.get('.site-grise').should('have.length', 2);
    // Le site disponible est bien Gand.
    cy.contains('.site-card', 'Gand').should('have.class', 'site-disponible');
  });

  it('un clic sur un site disponible mène au calendrier', () => {
    ouvrirSites('LIBRE');
    cy.contains('.site-card', 'Bruxelles').click();
    cy.url().should('include', '/joueur/calendrier/1');
  });

  it('un clic sur un site non disponible ne navigue pas', () => {
    // Membre du site 1 : Gand (site 2) est non disponible.
    ouvrirSites('SITE', '1');
    cy.contains('.site-card', 'Gand').click();
    // On reste sur la page des sites.
    cy.url().should('include', '/joueur/sites');
  });

  it('le bouton Profil mène au profil', () => {
    ouvrirSites('LIBRE');
    cy.contains('button', 'Profil').click();
    cy.url().should('include', '/joueur/profil');
  });
});
