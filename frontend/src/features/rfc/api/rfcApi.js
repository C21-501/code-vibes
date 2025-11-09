import { api } from '../../../shared/api/config';

/**
 * RFC API Service based on OpenAPI specification
 */

export const rfcApi = {
  // GET /rfc - Get paginated RFC list with filters
  getRfcs: (params = {}) =>
    api.get('/rfc', {
      params: {
        page: params.page || 0,
        size: params.size || 20,
        status: params.status,
        urgency: params.urgency,
        requesterId: params.requesterId
      }
    }).then(response => response.data),

  // GET /rfc/{id} - Get RFC by ID
  getRfcById: (id) =>
    api.get(`/rfc/${id}`).then(response => response.data),

  // POST /rfc - Create new RFC
  createRfc: (rfcData) =>
    api.post('/rfc', rfcData).then(response => response.data),

  // PUT /rfc/{id} - Update RFC
  updateRfc: (id, rfcData) =>
    api.put(`/rfc/${id}`, rfcData).then(response => response.data),

  // DELETE /rfc/{id} - Delete RFC
  deleteRfc: (id) =>
    api.delete(`/rfc/${id}`).then(response => response.data),

  // PATCH /rfc/{id}/status - Update RFC status
  updateRfcStatus: (id, statusData) =>
    api.patch(`/rfc/${id}/status`, statusData).then(response => response.data),

  // POST /rfc/{id}/approve - Approve RFC
  approveRfc: (id, approveData = {}) =>
    api.post(`/rfc/${id}/approve`, approveData).then(response => response.data),

  // POST /rfc/{id}/unapprove - Unapprove RFC
  unapproveRfc: (id, unapproveData = {}) =>
    api.post(`/rfc/${id}/unapprove`, unapproveData).then(response => response.data),

  // GET /rfc/{id}/approvals - Get RFC approvals
  getRfcApprovals: (id) =>
    api.get(`/rfc/${id}/approvals`).then(response => response.data),

  // PATCH /rfc/{rfcId}/subsystem/{subsystemId}/confirmation - Update confirmation status
  updateSubsystemConfirmation: (rfcId, subsystemId, confirmationData) =>
    api.patch(`/rfc/${rfcId}/subsystem/${subsystemId}/confirmation`, confirmationData)
      .then(response => response.data),

  // PATCH /rfc/{rfcId}/subsystem/{subsystemId}/execution - Update execution status
  updateSubsystemExecution: (rfcId, subsystemId, executionData) =>
    api.patch(`/rfc/${rfcId}/subsystem/${subsystemId}/execution`, executionData)
      .then(response => response.data)
};