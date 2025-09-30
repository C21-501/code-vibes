import axios from 'axios';
import keycloak from '../keycloak';

// Create axios instance
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
});

// Request interceptor to add Authorization header
apiClient.interceptors.request.use(
  async (config) => {
    // Only add token for API requests (routes starting with /api)
    if (config.url?.startsWith('/api') && keycloak.token) {
      // Check if token needs refresh
      if (keycloak.isTokenExpired(30)) {
        try {
          const refreshed = await keycloak.updateToken(30);
          if (refreshed) {
            console.log('Token refreshed in interceptor');
          }
        } catch (error) {
          console.error('Failed to refresh token in interceptor:', error);
          keycloak.login();
          return Promise.reject(error);
        }
      }
      
      config.headers.Authorization = `Bearer ${keycloak.token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle authentication errors
apiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    // If we get 401 and haven't already tried to refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      try {
        const refreshed = await keycloak.updateToken(30);
        if (refreshed && keycloak.token) {
          // Update the authorization header and retry the request
          originalRequest.headers.Authorization = `Bearer ${keycloak.token}`;
          return apiClient(originalRequest);
        } else {
          // Token refresh failed, redirect to login
          keycloak.login();
        }
      } catch (refreshError) {
        console.error('Token refresh failed:', refreshError);
        keycloak.login();
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

export default apiClient;
