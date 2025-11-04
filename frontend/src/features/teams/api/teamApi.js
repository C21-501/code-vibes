/**
 * Team API Service
 * Handles all API calls related to team management
 * Based on OpenAPI specification: /team and /team/{id} endpoints
 */

import apiClient from '../../../utils/apiClient';

const API_BASE_URL = '/api';

/**
 * Get paginated list of teams with optional name search
 * GET /team
 * 
 * @param {Object} params - Query parameters
 * @param {number} params.page - Page number (0-indexed, default: 0)
 * @param {number} params.size - Page size (default: 20, min: 1, max: 100)
 * @param {string} params.name - Filter by team name (optional, minLength: 1)
 * @returns {Promise<Object>} TeamPageResponse with content, totalElements, totalPages, etc.
 */
export async function getTeams({ page = 0, size = 20, name = '' } = {}) {
  const params = new URLSearchParams({
    page: page.toString(),
    size: size.toString()
  });
  
  // Only add name filter if not empty (per OpenAPI spec minLength: 1)
  if (name && name.trim()) {
    params.append('name', name.trim());
  }
  
  const response = await apiClient.request(`${API_BASE_URL}/team?${params.toString()}`, {
    method: 'GET'
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to fetch teams');
  }
  
  return response.json();
}

/**
 * Get team by ID
 * GET /team/{id}
 * 
 * @param {number} id - Team ID
 * @returns {Promise<Object>} TeamResponse
 */
export async function getTeamById(id) {
  const response = await apiClient.request(`${API_BASE_URL}/team/${id}`, {
    method: 'GET'
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to fetch team');
  }
  
  return response.json();
}

/**
 * Create new team
 * POST /team
 * 
 * @param {Object} teamData - TeamRequest object
 * @param {string} teamData.name - Team name (minLength: 1, maxLength: 255)
 * @param {string} teamData.description - Team description (maxLength: 1000, nullable)
 * @param {number[]} teamData.memberIds - Array of member user IDs (minItems: 1)
 * @returns {Promise<Object>} TeamResponse (201 Created)
 */
export async function createTeam(teamData) {
  const response = await apiClient.request(`${API_BASE_URL}/team`, {
    method: 'POST',
    body: JSON.stringify(teamData)
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to create team');
  }
  
  return response.json();
}

/**
 * Update existing team
 * PUT /team/{id}
 * 
 * @param {number} id - Team ID
 * @param {Object} teamData - TeamRequest object
 * @param {string} teamData.name - Team name (minLength: 1, maxLength: 255)
 * @param {string} teamData.description - Team description (maxLength: 1000, nullable)
 * @param {number[]} teamData.memberIds - Array of member user IDs (minItems: 1)
 * @returns {Promise<Object>} TeamResponse (200 OK)
 */
export async function updateTeam(id, teamData) {
  const response = await apiClient.request(`${API_BASE_URL}/team/${id}`, {
    method: 'PUT',
    body: JSON.stringify(teamData)
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to update team');
  }
  
  return response.json();
}

/**
 * Delete team by ID
 * DELETE /team/{id}
 * 
 * @param {number} id - Team ID
 * @returns {Promise<void>} No content (204 No Content)
 */
export async function deleteTeam(id) {
  const response = await apiClient.request(`${API_BASE_URL}/team/${id}`, {
    method: 'DELETE'
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to delete team');
  }
  
  // 204 No Content - no response body
  return;
}

