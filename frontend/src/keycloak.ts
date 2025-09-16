import Keycloak from 'keycloak-js';

// Keycloak configuration
const keycloakConfig = {
  url: process.env.VITE_KEYCLOAK_URL || 'http://localhost:8080',
  realm: process.env.VITE_KEYCLOAK_REALM || 'cab-realm',
  clientId: process.env.VITE_KEYCLOAK_CLIENT_ID || 'cab-frontend',
};

// Initialize Keycloak instance
const keycloak = new Keycloak(keycloakConfig);

export default keycloak;
