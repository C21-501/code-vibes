/**
 * Auth Context Utilities
 * Manages authentication state, return URLs, and request queue
 */

/**
 * Save the current URL to return to after login
 * Uses sessionStorage to keep it session-specific
 * @param {string} url - URL to save (pathname + search + hash)
 */
export function saveReturnUrl(url) {
  if (url && url !== '/login') {
    sessionStorage.setItem('returnUrl', url);
  }
}

/**
 * Get the saved return URL and clear it
 * @returns {string|null} Saved URL or null
 */
export function getAndClearReturnUrl() {
  const url = sessionStorage.getItem('returnUrl');
  sessionStorage.removeItem('returnUrl');
  return url;
}

/**
 * Clear the saved return URL
 */
export function clearReturnUrl() {
  sessionStorage.removeItem('returnUrl');
}

/**
 * Get the saved return URL without clearing it
 * @returns {string|null} Saved URL or null
 */
export function getReturnUrl() {
  return sessionStorage.getItem('returnUrl');
}

/**
 * Clear authentication tokens from localStorage
 * Does NOT clear pending requests queue (for re-auth scenarios)
 */
export function clearAuthTokens() {
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  localStorage.removeItem('tokenType');
  localStorage.removeItem('expiresIn');
  localStorage.removeItem('tokenExpiration');
  localStorage.removeItem('refreshTokenExpiration');
}

/**
 * Clear all authentication data from storage
 * Use this for logout - clears everything including pending requests
 */
export function clearAuthData() {
  // Clear localStorage auth data
  clearAuthTokens();
  
  // Clear sessionStorage
  clearReturnUrl();
  
  // Clear pending requests queue (only on logout)
  sessionStorage.removeItem('pendingApiRequests');
}

/**
 * Check if user is currently on login page
 * @returns {boolean} True if on login page
 */
export function isOnLoginPage() {
  return window.location.pathname === '/login';
}

