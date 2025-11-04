/**
 * JWT Token Utilities
 * Functions for decoding and extracting user data from JWT tokens
 */

/**
 * Decode JWT token payload (base64 decode)
 * @param {string} token - JWT token
 * @returns {Object|null} Decoded payload or null if invalid
 */
export function decodeJWT(token) {
  try {
    if (!token) return null;
    
    // JWT format: header.payload.signature
    const parts = token.split('.');
    if (parts.length !== 3) return null;
    
    // Decode payload (second part)
    const payload = parts[1];
    
    // Base64 decode (handle URL-safe base64)
    const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    
    return JSON.parse(jsonPayload);
  } catch (error) {
    console.error('Error decoding JWT:', error);
    return null;
  }
}

/**
 * Extract user data from JWT token
 * @param {string} token - JWT token
 * @returns {Object|null} User data or null if invalid
 */
export function getUserFromToken(token) {
  const payload = decodeJWT(token);
  if (!payload) return null;
  
  // Extract roles from realm_access
  let roles = [];
  if (payload.realm_access && Array.isArray(payload.realm_access.roles)) {
    roles = payload.realm_access.roles;
  }
  
  // Get primary role (first role or REQUESTER as default)
  // Priority: ADMIN > CAB_MANAGER > EXECUTOR > REQUESTER
  const rolePriority = ['ADMIN', 'CAB_MANAGER', 'EXECUTOR', 'REQUESTER'];
  let primaryRole = 'REQUESTER';
  
  for (const role of rolePriority) {
    if (roles.includes(role)) {
      primaryRole = role;
      break;
    }
  }
  
  return {
    id: payload.sub || null,                      // User ID from JWT subject
    username: payload.preferred_username || '',   // Username
    firstName: payload.given_name || '',          // First name
    lastName: payload.family_name || '',          // Last name
    email: payload.email || '',                   // Email
    role: primaryRole,                            // Primary role
    roles: roles,                                 // All roles
    emailVerified: payload.email_verified || false,
    name: payload.name || ''                      // Full name
  };
}

/**
 * Get current user from localStorage token
 * @returns {Object|null} User data or null if no valid token
 */
export function getCurrentUser() {
  const token = localStorage.getItem('accessToken');
  if (!token) return null;
  
  return getUserFromToken(token);
}

/**
 * Check if token is expired
 * @param {string} token - JWT token
 * @returns {boolean} True if expired
 */
export function isTokenExpired(token) {
  const payload = decodeJWT(token);
  if (!payload || !payload.exp) return true;
  
  // exp is in seconds, Date.now() is in milliseconds
  const expirationTime = payload.exp * 1000;
  return Date.now() >= expirationTime;
}

/**
 * Check if current token is expired
 * @returns {boolean} True if expired
 */
export function isCurrentTokenExpired() {
  const token = localStorage.getItem('accessToken');
  if (!token) return true;
  
  return isTokenExpired(token);
}

/**
 * Get token expiration time
 * @param {string} token - JWT token
 * @returns {Date|null} Expiration date or null
 */
export function getTokenExpiration(token) {
  const payload = decodeJWT(token);
  if (!payload || !payload.exp) return null;
  
  return new Date(payload.exp * 1000);
}

/**
 * Check if current user has ADMIN role
 * @returns {boolean} True if user is admin
 */
export function isAdmin() {
  const user = getCurrentUser();
  if (!user) return false;
  
  // Check if user has ADMIN role in their roles array
  return user.roles && user.roles.includes('ADMIN');
}

