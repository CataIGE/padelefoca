// Test end-to-end du parcours de connexion administrateur.
//
// describe = un groupe de tests (la "suite")
// it       = un test individuel (un scénario)
//
// L'idée : on simule un vrai utilisateur qui arrive sur la page de connexion,
// remplit le formulaire et clique. On vérifie ensuite ce qui s'affiche à l'écran
// et où l'app nous emmène.

describe('Connexion administrateur', () => {

  // Avant CHAQUE test, on repart d'une page de connexion propre.
  beforeEach(() => {
    cy.visit('/admin/connexion');
  });

  it('affiche une erreur si les champs sont vides', () => {
    // On clique sur "Continuer" sans rien remplir.
    cy.contains('button', 'Continuer').click();

    // Le message d'erreur du composant doit apparaître.
    cy.get('.erreur-msg').should('contain', 'Veuillez remplir tous les champs.');
  });

  it("affiche une erreur si l'email est invalide", () => {
    cy.get('input[type="email"]').type('pas-un-email');
    cy.get('.champ-input-wrapper input').type('motdepasse123');
    cy.contains('button', 'Continuer').click();

    cy.get('.erreur-msg').should('contain', 'Veuillez entrer un email valide.');
  });

  it("connecte l'administrateur et le redirige vers l'accueil", () => {
    // cy.intercept = on "intercepte" l'appel au backend et on renvoie une fausse
    // réponse. Comme ça le test ne dépend pas d'un vrai serveur qui tourne.
    // Le '**' au début veut dire "n'importe quel domaine/port".
    cy.intercept('POST', '**/api/auth/admin/connexion', {
      statusCode: 200,
      fixture: 'admin.json', // contenu défini dans cypress/fixtures/admin.json
    }).as('connexion'); // on donne un alias pour pouvoir l'attendre ensuite

    cy.get('input[type="email"]').type('admin@padelefoca.be');
    cy.get('.champ-input-wrapper input').type('monMotDePasse');
    cy.contains('button', 'Continuer').click();

    // On attend que la requête interceptée soit bien partie.
    cy.wait('@connexion');

    // On vérifie la redirection vers la page d'accueil admin.
    cy.url().should('include', '/admin/accueil');

    // Et que le token a bien été stocké en session.
    cy.window().then((win) => {
      expect(win.sessionStorage.getItem('token')).to.eq('faux-token-pour-les-tests-123');
    });
  });

  it('affiche une erreur si les identifiants sont incorrects', () => {
    // Cette fois on simule une réponse 401 (refus du backend).
    cy.intercept('POST', '**/api/auth/admin/connexion', {
      statusCode: 401,
      body: { message: 'Unauthorized' },
    }).as('connexionEchouee');

    cy.get('input[type="email"]').type('admin@padelefoca.be');
    cy.get('.champ-input-wrapper input').type('mauvaisMotDePasse');
    cy.contains('button', 'Continuer').click();

    cy.wait('@connexionEchouee');

    // Le message d'erreur d'identifiants doit apparaître...
    cy.get('.erreur-msg').should('contain', 'Email ou mot de passe incorrect.');
    // ...et on reste sur la page de connexion (pas de redirection).
    cy.url().should('include', '/admin/connexion');
  });

  it('exemple avec la commande personnalisée', () => {
    cy.intercept('POST', '**/api/auth/admin/connexion', {
      statusCode: 200,
      fixture: 'admin.json',
    }).as('connexion');

    // Grâce à la commande définie dans cypress/support/commands.ts,
    // tout le parcours de connexion tient en une ligne.
    cy.connexionAdmin('admin@padelefoca.be', 'monMotDePasse');

    cy.wait('@connexion');
    cy.url().should('include', '/admin/accueil');
  });
});
