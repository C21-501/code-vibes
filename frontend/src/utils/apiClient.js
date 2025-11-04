/**
 * Centralized API Client
 * Provides request/response interceptors for graceful JWT token handling
 * Handles token validation, 401 errors, request queueing, and automatic retry
 */

import { isTokenExpired } from './jwtUtils';
import { saveReturnUrl, isOnLoginPage, clearAuthTokens } from './authContext';

const PENDING_REQUESTS_KEY = 'pendingApiRequests';

class ApiClient {
  constructor() {
    // Flag to prevent multiple simultaneous redirects
    this.isRedirectingToLogin = false;
    
    // Callbacks waiting for auth refresh (in-memory, for same session)
    this.refreshSubscribers = [];
  }

  /**
   * Save pending request to sessionStorage to survive page reload
   * @param {string} url - Request URL
   * @param {Object} options - Fetch options
   */
  savePendingRequest(url, options) {
    try {
      const pendingRequests = this.getPendingRequests();
      
      // Create request object
      const request = {
        url,
        method: options.method || 'GET',
        body: options.body,
        timestamp: Date.now()
      };
      
      // Check for duplicates (same URL and method within last 5 seconds)
      const isDuplicate = pendingRequests.some(req => 
        req.url === url && 
        req.method === request.method &&
        (Date.now() - req.timestamp) < 5000
      );
      
      if (isDuplicate) {
        console.log('Skipping duplicate request:', url);
        return;
      }
      
      // Check size before saving (sessionStorage limit is ~5-10MB)
      const currentSize = JSON.stringify(pendingRequests).length;
      const newRequestSize = JSON.stringify(request).length;
      const MAX_STORAGE_SIZE = 1024 * 1024; // 1MB safety limit
      
      if (currentSize + newRequestSize > MAX_STORAGE_SIZE) {
        console.warn('Pending requests queue too large, skipping:', url);
        return;
      }
      
      // Add to queue
      pendingRequests.push(request);
      
      sessionStorage.setItem(PENDING_REQUESTS_KEY, JSON.stringify(pendingRequests));
    } catch (error) {
      console.error('Failed to save pending request:', error);
    }
  }

  /**
   * Get pending requests from sessionStorage
   * Filters out requests older than 10 minutes
   * @returns {Array} Array of pending requests
   */
  getPendingRequests() {
    try {
      const stored = sessionStorage.getItem(PENDING_REQUESTS_KEY);
      if (!stored) return [];
      
      const requests = JSON.parse(stored);
      const MAX_AGE = 10 * 60 * 1000; // 10 minutes
      
      // Filter out old requests
      const freshRequests = requests.filter(req => {
        const age = Date.now() - req.timestamp;
        return age < MAX_AGE;
      });
      
      // Update storage if we filtered anything out
      if (freshRequests.length !== requests.length) {
        sessionStorage.setItem(PENDING_REQUESTS_KEY, JSON.stringify(freshRequests));
      }
      
      return freshRequests;
    } catch (error) {
      console.error('Failed to get pending requests:', error);
      return [];
    }
  }

  /**
   * Clear pending requests from sessionStorage
   */
  clearPendingRequests() {
    sessionStorage.removeItem(PENDING_REQUESTS_KEY);
  }

  /**
   * Add a callback to be executed after successful login (in-memory only)
   * @param {Function} callback - Function to call after login
   */
  subscribeTokenRefresh(callback) {
    this.refreshSubscribers.push(callback);
  }

  /**
   * Execute all queued callbacks after successful login
   * This handles in-memory subscribers (for refresh without redirect)
   */
  processInMemorySubscribers() {
    const token = localStorage.getItem('accessToken');
    this.refreshSubscribers.forEach(callback => callback(token));
    this.refreshSubscribers = [];
    this.isRedirectingToLogin = false;
  }

  /**
   * Replay all pending requests from sessionStorage after login
   * This handles requests that were queued before page redirect
   * @returns {Promise<void>}
   */
  async replayPendingRequests() {
    const pendingRequests = this.getPendingRequests();
    
    if (pendingRequests.length === 0) {
      return;
    }
    
    console.log(`Replaying ${pendingRequests.length} pending request(s)...`);
    
    // Clear the queue before replaying to avoid duplicates
    this.clearPendingRequests();
    
    // Replay each request (fire and forget - components will refetch if needed)
    const replayPromises = pendingRequests.map(request => {
      // Don't await - let them run in parallel
      return this.request(request.url, {
        method: request.method,
        body: request.body
      }).catch(error => {
        // Log but don't fail - one failed request shouldn't block others
        console.error('Failed to replay request:', request.url, error);
        return null;
      });
    });
    
    // Wait for all replays to complete (or fail)
    await Promise.allSettled(replayPromises);
  }

  /**
   * Clear the request queue and subscribers (e.g., on logout)
   */
  clearQueue() {
    this.refreshSubscribers = [];
    this.isRedirectingToLogin = false;
    this.clearPendingRequests();
  }

  /**
   * Redirect to login page with return URL saved
   */
  redirectToLogin() {
    if (this.isRedirectingToLogin || isOnLoginPage()) {
      return;
    }
    
    this.isRedirectingToLogin = true;
    
    // Save current location for return after login
    const currentUrl = window.location.pathname + window.location.search + window.location.hash;
    saveReturnUrl(currentUrl);
    
    // Clear invalid/expired tokens from localStorage
    // Note: We keep the pending requests queue in sessionStorage for replay after login
    clearAuthTokens();
    
    // Redirect to login
    window.location.href = '/login';
  }

  /**
   * Check if request needs authentication
   * @param {string} url - Request URL
   * @returns {boolean} True if auth is needed
   */
  needsAuth(url) {
    // Login endpoint doesn't need auth
    return !url.includes('/user/login');
  }

  /**
   * Get authentication headers
   * @returns {Object} Headers with Authorization if token exists
   */
  getAuthHeaders() {
    const token = localStorage.getItem('accessToken');
    const tokenType = localStorage.getItem('tokenType') || 'Bearer';
    
    const headers = {
      'Content-Type': 'application/json'
    };
    
    if (token) {
      headers['Authorization'] = `${tokenType} ${token}`;
    }
    
    return headers;
  }

  /**
   * Main request method with interceptors
   * @param {string} url - Request URL
   * @param {Object} options - Fetch options
   * @returns {Promise<Response>} Fetch response
   */
  async request(url, options = {}) {
    // Merge headers
    const headers = {
      ...this.getAuthHeaders(),
      ...options.headers
    };

    // Pre-request interceptor: Check token validity for authenticated requests
    if (this.needsAuth(url)) {
      const token = localStorage.getItem('accessToken');
      
      // No token - save request and redirect to login
      if (!token) {
        this.savePendingRequest(url, options);
        this.redirectToLogin();
        // Return a rejected promise
        return Promise.reject(new Error('Authentication required - redirecting to login'));
      }
      
      // Token expired - save request and redirect to login
      if (isTokenExpired(token)) {
        this.savePendingRequest(url, options);
        this.redirectToLogin();
        // Return a rejected promise
        return Promise.reject(new Error('Token expired - redirecting to login'));
      }
    }

    // Execute the request
    try {
      const response = await fetch(url, {
        ...options,
        headers
      });

      // Post-response interceptor: Handle 401 Unauthorized
      if (response.status === 401 && this.needsAuth(url)) {
        // Token is invalid on server side - save request and redirect
        this.savePendingRequest(url, options);
        this.redirectToLogin();
        
        // Return a rejected promise
        return Promise.reject(new Error('Token invalid - redirecting to login'));
      }

      return response;
    } catch (error) {
      // Network errors or other fetch errors
      throw error;
    }
  }

  /**
   * Notify that login was successful and process queued requests
   * Should be called after successful login
   * @returns {Promise<void>}
   */
  async onLoginSuccess() {
    // Process in-memory subscribers first (if any)
    this.processInMemorySubscribers();
    
    // Then replay pending requests from sessionStorage
    await this.replayPendingRequests();
  }

  /**
   * Clear queue on logout
   */
  onLogout() {
    this.clearQueue();
  }
}

// Create singleton instance
const apiClient = new ApiClient();

export default apiClient;

/**
 * Convenience method for making API requests
 * @param {string} url - Request URL
 * @param {Object} options - Fetch options
 * @returns {Promise<Response>} Fetch response
 */
export function apiRequest(url, options) {
  return apiClient.request(url, options);
}

