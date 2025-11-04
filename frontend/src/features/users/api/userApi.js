/**
 * User API Service
 * Handles all API calls related to user management
 * Based on OpenAPI specification: /user and /user/{id} endpoints
 */

const API_BASE_URL = '/api';

/**
 * Get authentication headers with token from localStorage
 * @returns {Object} Headers object with Authorization if token exists
 */
function getAuthHeaders() {
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
  
  const response = await fetch(`${API_BASE_URL}/user?${params.toString()}`, {
    method: 'GET',
    headers: getAuthHeaders()
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
  const response = await fetch(`${API_BASE_URL}/user/${id}`, {
    method: 'GET',
    headers: getAuthHeaders()
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
  const response = await fetch(`${API_BASE_URL}/user`, {
    method: 'POST',
    headers: getAuthHeaders(),
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
  const response = await fetch(`${API_BASE_URL}/user/${id}`, {
    method: 'PUT',
    headers: getAuthHeaders(),
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
  const response = await fetch(`${API_BASE_URL}/user/${id}`, {
    method: 'DELETE',
    headers: getAuthHeaders()
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to delete user');
  }
  
  // 204 No Content - no response body
  return;
}

