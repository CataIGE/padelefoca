// Test du parcours de création de match (côté joueur).
//
// Rappels sur le composant MatchCreer :
//  - protégé par le joueurGuard -> il faut un 'matricule' en sessionStorage.
//  - siteId vient de l'URL, dateHeure vient des query params.
//  - type PUBLIC par défaut. En PUBLIC : POST /api/matches puis redirection /joueur/profil.
//  - en PRIVE : 3 champs de matricules (validation), puis POST /api/matches
//    et POST /api/matches/:id/joueurs, puis redirection /joueur/profil.

describe('Création de match', () => {

  // On arrive sur la page comme si on venait du calendrier :
  // un siteId dans l'URL + un dateHeure en query param, et un joueur connecté.
  beforeEach(() => {
    cy.visit('/joueur/match/creer/1?dateHeure=2026-06-15T09:00:00', {
      onBeforeLoad(win) {
        win.sessionStorage.setItem('matricule', 'L1234');
        win.sessionStorage.setItem('typeMembre', 'SITE');
      },
    });
  });

  it('affiche le créneau sélectionné et le choix du type', () => {
    cy.contains('Créneau sélectionné').should('be.visible');
    cy.contains('Match public').should('be.visible');
    cy.contains('Match privé').should('be.visible');
  });

  it('crée un match public et redirige vers le profil', () => {
    cy.intercept('POST', '**/api/matches', {
      statusCode: 200,
      body: { id: 42 },
    }).as('creerMatch');

    // Le type PUBLIC est sélectionné par défaut : on clique directement sur "Créer".
    cy.contains('button', 'Créer le match').click();

    cy.wait('@creerMatch');
    cy.url().should('include', '/joueur/profil');
  });

  it('affiche les champs de matricules quand on choisit "privé"', () => {
    // Par défaut les champs ne sont pas là.
    cy.contains('JOUEUR 2').should('not.exist');

    cy.contains('Match privé').click();

    // Ils apparaissent après le choix "privé".
    cy.contains('JOUEUR 2').should('be.visible');
    cy.contains('JOUEUR 3').should('be.visible');
    cy.contains('JOUEUR 4').should('be.visible');
  });

  it('refuse un match privé si les matricules sont vides', () => {
    cy.contains('Match privé').click();
    cy.contains('button', 'Créer le match').click();

    cy.get('.erreur-msg').should('contain', 'matricules des 3 autres joueurs');
  });

  it('refuse un match privé si un matricule a un format invalide', () => {
    cy.contains('Match privé').click();

    // On remplit les 3 champs, mais le premier est invalide (pas le bon format).
    cy.get('input[placeholder="L1234"]').eq(0).type('XXXX');
    cy.get('input[placeholder="L1234"]').eq(1).type('S5678');
    cy.get('input[placeholder="L1234"]').eq(2).type('G9012');

    cy.contains('button', 'Créer le match').click();

    cy.get('.erreur-msg').should('contain', 'invalides');
  });

  it('crée un match privé complet et redirige vers le profil', () => {
    cy.intercept('POST', '**/api/matches', {
      statusCode: 200,
      body: { id: 42 },
    }).as('creerMatch');
    // L'ajout des joueurs renvoie 200 sans contenu.
    cy.intercept('POST', '**/api/matches/42/joueurs', { statusCode: 200 }).as('ajoutJoueurs');

    cy.contains('Match privé').click();
    cy.get('input[placeholder="L1234"]').eq(0).type('L1111');
    cy.get('input[placeholder="L1234"]').eq(1).type('S2222');
    cy.get('input[placeholder="L1234"]').eq(2).type('G3333');

    cy.contains('button', 'Créer le match').click();

    cy.wait('@creerMatch');
    cy.wait('@ajoutJoueurs');
    cy.url().should('include', '/joueur/profil');
  });

  it('affiche une erreur si la création échoue côté serveur', () => {
    cy.intercept('POST', '**/api/matches', {
      statusCode: 500,
      body: { message: 'Créneau déjà réservé.' },
    }).as('creerMatchEchoue');

    cy.contains('button', 'Créer le match').click();

    cy.wait('@creerMatchEchoue');
    cy.get('.erreur-msg').should('contain', 'Créneau déjà réservé.');
  });

  it('le bouton Retour ramène au calendrier du site', () => {
    cy.contains('button', 'Retour').click();
    cy.url().should('include', '/joueur/calendrier/1');
  });
});
