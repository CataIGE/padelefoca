import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    // URL de base : c'est l'adresse de ton "ng serve".
    // Grâce à ça, dans les tests tu peux écrire cy.visit('/admin/connexion')
    // au lieu de l'URL complète.
    baseUrl: 'http://localhost:4200',

    // Où Cypress va chercher tes tests.
    specPattern: 'cypress/e2e/**/*.cy.ts',
    supportFile: 'cypress/support/e2e.ts',

    setupNodeEvents(on, config) {
      // Endroit pour brancher des plugins / events Node si besoin plus tard.
      return config;
    },
  },
});
