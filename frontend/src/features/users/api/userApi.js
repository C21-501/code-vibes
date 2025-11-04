/**
 * User API Service
 * Handles all API calls related to user management
 * Based on OpenAPI specification: /user and /user/{id} endpoints
 */

import apiClient from '../../../utils/apiClient';

const API_BASE_URL = '/api';

/**
 * Get paginated list of users with optional search
 * GET /user
 * 
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number (0-indexed, default: 0)
 * @param {number} params.size - Page size (default: 20, min: 1, max: 100)
 * @param {string} params.searchString - Search string (optional, minLength: 1)
 * @returns {Promise<Object>} UserPageResponse with content, totalElements, totalPages, etc.
 */
export async function getUsers({ page = 0, size = 20, searchString = '' } = {}) {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString()
  });
  
  // Only add searchString if not empty (per OpenAPI spec minLength: 1)
  if (searchString && searchString.trim()) {
    params.append('searchString', searchString.trim());
  }
  
  const response = await apiClient.request(`${API_BASE_URL}/user?${params.toString()}`, {
    method: 'GET'
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to fetch users');
  }
  
  return response.json();
}

/**
 * Get user by ID
 * GET /user/{id}
 * 
 * @param {number} id - User ID
 * @returns {Promise<Object>} UserResponse
 */
export async function getUserById(id) {
  const response = await apiClient.request(`${API_BASE_URL}/user/${id}`, {
    method: 'GET'
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to fetch user');
  }
  
  return response.json();
}

/**
 * Create new user
 * POST /user
 * 
 * @param {Object} userData - UserRequest object
 * @param {string} userData.username - Username (minLength: 3, maxLength: 50, pattern: ^[a-zA-Z0-9_-]+$)
 * @param {string} userData.firstName - First name (minLength: 1, maxLength: 100)
 * @param {string} userData.lastName - Last name (minLength: 1, maxLength: 100)
 * @param {string} userData.role - User role (REQUESTER, EXECUTOR, CAB_MANAGER, ADMIN)
 * @param {string} userData.password - Password (minLength: 8, maxLength: 100)
 * @returns {Promise<Object>} UserResponse (201 Created)
 */
export async function createUser(userData) {
  const response = await apiClient.request(`${API_BASE_URL}/user`, {
    method: 'POST',
    body: JSON.stringify(userData)
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to create user');
  }
  
  return response.json();
}

/**
 * Update existing user
 * PUT /user/{id}
 * 
 * @param {number} id - User ID
 * @param {Object} userData - UpdateUserRequest object
 * @param {string} userData.username - Username (minLength: 3, maxLength: 50, pattern: ^[a-zA-Z0-9_-]+$)
 * @param {string} userData.firstName - First name (minLength: 1, maxLength: 100)
 * @param {string} userData.lastName - Last name (minLength: 1, maxLength: 100)
 * @param {string} userData.role - User role (REQUESTER, EXECUTOR, CAB_MANAGER, ADMIN)
 * @returns {Promise<Object>} UserResponse (200 OK)
 */
export async function updateUser(id, userData) {
  const response = await apiClient.request(`${API_BASE_URL}/user/${id}`, {
    method: 'PUT',
    body: JSON.stringify(userData)
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to update user');
  }
  
  return response.json();
}

/**
 * Delete user by ID
 * DELETE /user/{id}
 * 
 * @param {number} id - User ID
 * @returns {Promise<void>} No content (204 No Content)
 */
export async function deleteUser(id) {
  const response = await apiClient.request(`${API_BASE_URL}/user/${id}`, {
    method: 'DELETE'
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to delete user');
  }
  
  // 204 No Content - no response body
  return;
}

