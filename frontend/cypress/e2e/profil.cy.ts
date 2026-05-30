// Test du profil joueur.
//
// Particularités du composant Profil :
//  - les données sont chargées au démarrage via toSignal :
//      GET /api/joueurs/profil        -> les infos du joueur
//      GET /api/joueurs/reservations  -> la liste des réservations
//    => il faut donc poser les cy.intercept AVANT cy.visit.
//  - "Payer" appelle le service paiement puis fait window.location.reload().
//  - "Annuler" fait DELETE /api/reservations/:id puis window.location.reload().
//  - la page est protégée par le joueurGuard (matricule en sessionStorage).

describe('Profil joueur', () => {

  // Un joueur d'exemple réutilisé dans plusieurs tests.
  const joueurFictif = {
    matricule: 'L1234',
    nom: 'Verhoeven',
    prenom: 'Lina',
    age: 28,
    telephone: '+32470000000',
    email: 'lina@email.com',
    typeMembre: 'SITE',
    penaliteActive: false,
    soldeDu: 12,
    soldeCredit: 5,
  };

  // Fonction utilitaire : prépare les interceptions puis visite la page.
  // On passe la liste de réservations voulue selon le test.
  function ouvrirProfil(reservations: any[]) {
    cy.intercept('GET', '**/api/joueurs/profil', {
      statusCode: 200,
      body: joueurFictif,
    }).as('profil');

    cy.intercept('GET', '**/api/joueurs/reservations', {
      statusCode: 200,
      body: reservations,
    }).as('reservations');

    cy.visit('/joueur/profil', {
      onBeforeLoad(win) {
        win.sessionStorage.setItem('matricule', 'L1234');
        win.sessionStorage.setItem('typeMembre', 'SITE');
      },
    });

    cy.wait('@profil');
    cy.wait('@reservations');
  }

  it("redirige vers la connexion si le joueur n'est pas connecté", () => {
    cy.clearAllSessionStorage();
    cy.visit('/joueur/profil');
    cy.url().should('include', '/joueur/connexion');
  });

  it('affiche les informations du joueur', () => {
    ouvrirProfil([]);

    cy.contains('Lina Verhoeven').should('be.visible');
    cy.contains('L1234').should('be.visible');
    cy.contains('lina@email.com').should('be.visible');
    cy.contains('Membre du site').should('be.visible');
    cy.contains('Aucune pénalité active').should('be.visible');
  });

  it('affiche un message quand il n\'y a aucune réservation', () => {
    ouvrirProfil([]);
    cy.contains('Aucune réservation').should('be.visible');
  });

  it('affiche la liste des réservations', () => {
    ouvrirProfil([
      {
        id: 1,
        dateHeureMatch: '2026-06-15T09:00:00',
        siteNom: 'Bruxelles',
        terrainNom: 'Terrain 1',
        montantDu: 20,
        statutReservation: 'EN_ATTENTE',
      },
    ]);

    cy.contains('.reservation-item', 'Bruxelles').should('be.visible');
    cy.contains('EN_ATTENTE').should('be.visible');
    // Une réservation EN_ATTENTE doit proposer les boutons Payer et Annuler.
    cy.contains('button', 'Payer').should('be.visible');
    cy.contains('button', 'Annuler').should('be.visible');
  });

  it('le bouton Payer déclenche l\'appel de paiement', () => {
    cy.intercept('POST', '**/api/paiements', {
      statusCode: 200,
      body: { nouveauTypeMembre: 'SITE' },
    }).as('paiement');

    ouvrirProfil([
      {
        id: 1,
        dateHeureMatch: '2026-06-15T09:00:00',
        siteNom: 'Bruxelles',
        terrainNom: 'Terrain 1',
        montantDu: 20,
        statutReservation: 'EN_ATTENTE',
      },
    ]);

    cy.contains('button', 'Payer').click();
    cy.wait('@paiement'); // on vérifie que l'appel de paiement est bien parti
  });

  it('le bouton Annuler déclenche la suppression de la réservation', () => {
    cy.intercept('DELETE', '**/api/reservations/1', { statusCode: 200 }).as('annuler');

    ouvrirProfil([
      {
        id: 1,
        dateHeureMatch: '2026-06-15T09:00:00',
        siteNom: 'Bruxelles',
        terrainNom: 'Terrain 1',
        montantDu: 20,
        statutReservation: 'CONFIRMEE',
      },
    ]);

    cy.contains('button', 'Annuler').click();
    cy.wait('@annuler'); // l'appel DELETE doit partir
  });

  it('le bouton Retour ramène à la liste des sites', () => {
    ouvrirProfil([]);
    cy.contains('button', 'Retour').click();
    cy.url().should('include', '/joueur/sites');
  });
});
