/// <reference types="cypress" />

// --- Commandes personnalisées ---
// Une commande personnalisée te permet de réutiliser une suite d'actions
// dans plusieurs tests. Ici, on factorise la connexion admin.
//
// Utilisation dans un test :  cy.connexionAdmin('admin@padelefoca.be', 'monMotDePasse');

Cypress.Commands.add('connexionAdmin', (email: string, motDePasse: string) => {
  cy.visit('/admin/connexion');
  cy.get('input[type="email"]').type(email);
  cy.get('.champ-input-wrapper input').type(motDePasse);
  cy.contains('button', 'Continuer').click();
});

// On déclare le type de la commande pour que TypeScript la reconnaisse.
declare global {
  namespace Cypress {
    interface Chainable {
      connexionAdmin(email: string, motDePasse: string): Chainable<void>;
    }
  }
}

export {};
