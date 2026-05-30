// Test du parcours calendrier / réservation (côté joueur).
//
// Rappels sur le composant Calendrier :
//  - la page est protégée par le joueurGuard -> il faut un 'matricule' en sessionStorage,
//    sinon on est redirigé vers /joueur/connexion.
//  - si l'appel getCreneaux échoue, le composant affiche des créneaux de DÉMO
//    (getCreneauxMock). On s'en sert pour avoir des créneaux fiables dans les tests.
//  - clic sur un créneau LIBRE       -> redirige vers /joueur/match/creer/:siteId
//  - clic sur un créneau MATCH_PUBLIC -> POST /api/matches/:id/rejoindre puis /joueur/profil

describe('Calendrier et réservation', () => {

  // Avant chaque test, on simule un joueur déjà connecté (token + matricule),
  // et on force les créneaux de démo en faisant échouer l'appel réel.
  beforeEach(() => {
    // On renvoie une erreur sur getCreneaux -> le composant bascule sur les créneaux mock.
    cy.intercept('GET', '**/api/sites/**/creneaux**', { statusCode: 500 }).as('creneaux');

    cy.visit('/joueur/calendrier/1', {
      onBeforeLoad(win) {
        win.sessionStorage.setItem('matricule', 'L1234');
        win.sessionStorage.setItem('typeMembre', 'SITE');
      },
    });
  });

  it('redirige vers la connexion si le joueur n\'est pas connecté', () => {
    // Cas particulier : ici on NE met PAS de matricule, donc le guard doit rediriger.
    cy.clearAllSessionStorage();
    cy.visit('/joueur/calendrier/1');
    cy.url().should('include', '/joueur/connexion');
  });

  it('affiche la liste des créneaux', () => {
    // Les créneaux de démo contiennent une heure "09:00".
    cy.contains('.creneau-card', '09:00').should('be.visible');
    // Et au moins un créneau "Libre".
    cy.contains('Libre').should('be.visible');
  });

  it('un clic sur un créneau LIBRE mène à la création de match', () => {
    // Dans les données de démo, 09:00 est un créneau LIBRE.
    cy.contains('.creneau-card', '09:00').click();
    cy.url().should('include', '/joueur/match/creer/1');
    // Le créneau choisi est passé en paramètre d'URL.
    cy.url().should('include', 'dateHeure');
  });

  it('rejoindre un match public redirige vers le profil', () => {
    // Dans les données de démo, 10:45 est un MATCH_PUBLIC (matchId = 1) avec des places.
    // On intercepte l'appel "rejoindre" pour simuler un succès.
    cy.intercept('POST', '**/api/matches/1/rejoindre', {
      statusCode: 200,
      body: { reservationId: 99 },
    }).as('rejoindre');

    cy.contains('.creneau-card', '10:45').click();

    cy.wait('@rejoindre');
    cy.url().should('include', '/joueur/profil');
  });

  it('affiche une erreur si rejoindre le match échoue', () => {
    cy.intercept('POST', '**/api/matches/1/rejoindre', {
      statusCode: 400,
      body: { message: 'Match déjà complet.' },
    }).as('rejoindreEchoue');

    cy.contains('.creneau-card', '10:45').click();

    cy.wait('@rejoindreEchoue');
    cy.get('.erreur-msg').should('contain', 'Match déjà complet.');
  });

  it('le bouton Retour ramène à la liste des sites', () => {
    cy.contains('button', 'Retour').click();
    cy.url().should('include', '/joueur/sites');
  });
});
