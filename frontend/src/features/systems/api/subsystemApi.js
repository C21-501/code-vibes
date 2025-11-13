/**
 * Subsystem API Service
 * Handles all API calls related to subsystem management
 * Based on OpenAPI specification: /system/{id}/subsystem endpoints
 */

import apiClient from '../../../utils/apiClient';

const API_BASE_URL = '/api';

/**
 * Get list of subsystems for a specific system
 * GET /system/{id}/subsystem
 * 
 * @param {number} systemId - System ID
 * @returns {Promise<Array>} Array of SubsystemResponse objects
 */
export async function getSystemSubsystems(systemId) {
  const response = await apiClient.request(`${API_BASE_URL}/system/${systemId}/subsystem`, {
    method: 'GET'
  });
  
  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to fetch subsystems');
  }
  
  return response.json();
}

/**
 * Get all subsystems for a specific system without pagination
 * GET /system/{id}/subsystem
 *
 * @param {number} systemId - System ID
 * @returns {Promise<Array>} Array of SubsystemResponse objects
 */
export async function getAllSystemSubsystems(systemId) {
  const response = await apiClient.request(`${API_BASE_URL}/system/${systemId}/subsystem`, {
    method: 'GET'
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to fetch subsystems');
  }

  return response.json(); // Returns array directly according to OpenAPI spec
}

/**
 * Get subsystem by ID
 * GET /system/{id}/subsystem/{subsystemId}
 *
 * @param {number} systemId - System ID
 * @param {number} subsystemId - Subsystem ID
 * @returns {Promise<Object>} SubsystemResponse
 */
export async function getSubsystemById(systemId, subsystemId) {
  const response = await apiClient.request(`${API_BASE_URL}/system/${systemId}/subsystem/${subsystemId}`, {
    method: 'GET'
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to fetch subsystem');
  }

  return response.json();
}

/**
 * Create new subsystem
 * POST /system/{id}/subsystem
 *
 * @param {number} systemId - System ID
 * @param {Object} subsystemData - SubsystemRequest object
 * @param {string} subsystemData.name - Subsystem name (minLength: 1, maxLength: 255)
 * @param {string} subsystemData.description - Subsystem description (maxLength: 1000, nullable)
 * @param {number} subsystemData.systemId - System ID
 * @param {number} subsystemData.teamId - Team ID
 * @returns {Promise<Object>} SubsystemResponse (201 Created)
 */
export async function createSubsystem(systemId, subsystemData) {
  const response = await apiClient.request(`${API_BASE_URL}/system/${systemId}/subsystem`, {
    method: 'POST',
    body: JSON.stringify(subsystemData)
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to create subsystem');
  }

  return response.json();
}

/**
 * Update existing subsystem
 * PUT /system/{id}/subsystem/{subsystemId}
 *
 * @param {number} systemId - System ID
 * @param {number} subsystemId - Subsystem ID
 * @param {Object} subsystemData - SubsystemRequest object
 * @param {string} subsystemData.name - Subsystem name (minLength: 1, maxLength: 255)
 * @param {string} subsystemData.description - Subsystem description (maxLength: 1000, nullable)
 * @param {number} subsystemData.systemId - System ID
 * @param {number} subsystemData.teamId - Team ID
 * @returns {Promise<Object>} SubsystemResponse (200 OK)
 */
export async function updateSubsystem(systemId, subsystemId, subsystemData) {
  const response = await apiClient.request(`${API_BASE_URL}/system/${systemId}/subsystem/${subsystemId}`, {
    method: 'PUT',
    body: JSON.stringify(subsystemData)
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to update subsystem');
  }

  return response.json();
}

/**
 * Delete subsystem by ID
 * DELETE /system/{id}/subsystem/{subsystemId}
 *
 * @param {number} systemId - System ID
 * @param {number} subsystemId - Subsystem ID
 * @returns {Promise<void>} No content (204 No Content)
 */
export async function deleteSubsystem(systemId, subsystemId) {
  const response = await apiClient.request(`${API_BASE_URL}/system/${systemId}/subsystem/${subsystemId}`, {
    method: 'DELETE'
  });

  if (!response.ok) {
    const errorData = await response.json();
    throw new Error(errorData.errors?.[0]?.message || 'Failed to delete subsystem');
  }

  // 204 No Content - no response body
  return;
}