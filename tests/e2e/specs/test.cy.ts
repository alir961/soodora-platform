describe('App Root', () => {
  it('Visits the app root url and finds Soodora', () => {
    cy.visit('/');
    cy.get('#app').should('exist');
    cy.contains('Soodora');
  });
});
