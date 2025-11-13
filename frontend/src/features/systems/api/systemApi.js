/**
 * System API Service
 * Handles all API calls related to system management
 * Based on OpenAPI specification: /system and /system/{id} endpoints
 */

import apiClient from '../../../utils/apiClient';

const API_BASE_URL = '/api';

/**
 * Get paginated list of systems with optional name search
 * GET /system
 * 
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number (0-indexed, default: 0)
 * @param {number} params.size - Page size (default: 20, min: 1, max: 100)
 * @param {string} params.name - Filter by system name (optional, minLength: 1)
 * @returns {Promise<Object>} SystemPageResponse with content, totalElements, totalPages, etc.
 */
export async function getSystems({ page = 0, size = 20, name = '' } = {}) {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString()
  });
  
  // Only add name filter if not empty (per OpenAPI spec minLength: 1)
  if (name && name.trim()) {
    params.append('name', name.trim());
  }
  
  const response = await apiClient.request(`${API_BASE_URL}/system?${params.toString()}`, {
    method: 'GET'
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to fetch systems');
  }
  
  return response.json();
}

/**
 * Get all systems without pagination (for dropdowns)
 * Uses multiple requests if needed to get all systems
 *
 * @returns {Promise<Array>} Array of SystemResponse objects
 */
export async function getAllSystems() {
  let allSystems = [];
  let page = 0;
  const size = 100; // Maximum allowed by backend
  let hasMore = true;

  try {
    while (hasMore) {
      const response = await apiClient.request(`${API_BASE_URL}/system?page=${page}&size=${size}`, {
        method: 'GET'
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.errors?.[0]?.message || 'Failed to fetch systems');
      }

      const data = await response.json();

      if (data.content && data.content.length > 0) {
        allSystems = [...allSystems, ...data.content];
      }

      // Check if there are more pages
      hasMore = !data.last && data.content && data.content.length === size;
      page++;
    }

    return allSystems;
  } catch (error) {
    console.error('Error fetching all systems:', error);
    throw error;
  }
}

/**
 * Get system by ID
 * GET /system/{id}
 *
 * @param {number} id - System ID
 * @returns {Promise<Object>} SystemResponse
 */
export async function getSystemById(id) {
  const response = await apiClient.request(`${API_BASE_URL}/system/${id}`, {
    method: 'GET'
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to fetch system');
  }

  return response.json();
}

/**
 * Create new system
 * POST /system
 *
 * @param {Object} systemData - SystemRequest object
 * @param {string} systemData.name - System name (minLength: 1, maxLength: 255)
 * @param {string} systemData.description - System description (maxLength: 1000, nullable)
 * @returns {Promise<Object>} SystemResponse (201 Created)
 */
export async function createSystem(systemData) {
  const response = await apiClient.request(`${API_BASE_URL}/system`, {
    method: 'POST',
    body: JSON.stringify(systemData)
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to create system');
  }

  return response.json();
}

/**
 * Update existing system
 * PUT /system/{id}
 *
 * @param {number} id - System ID
 * @param {Object} systemData - SystemRequest object
 * @param {string} systemData.name - System name (minLength: 1, maxLength: 255)
 * @param {string} systemData.description - System description (maxLength: 1000, nullable)
 * @returns {Promise<Object>} SystemResponse (200 OK)
 */
export async function updateSystem(id, systemData) {
  const response = await apiClient.request(`${API_BASE_URL}/system/${id}`, {
    method: 'PUT',
    body: JSON.stringify(systemData)
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to update system');
  }

  return response.json();
}

/**
 * Delete system by ID
 * DELETE /system/{id}
 *
 * @param {number} id - System ID
 * @returns {Promise<void>} No content (204 No Content)
 */
export async function deleteSystem(id) {
  const response = await apiClient.request(`${API_BASE_URL}/system/${id}`, {
    method: 'DELETE'
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to delete system');
  }

  // 204 No Content - no response body
  return;
}