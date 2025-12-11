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
        requesterId: params.requesterId,
        title: params.title // Добавляем параметр поиска по названию
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

  // POST /rfc/{id}/approve - Approve RFC
    approveRfc: (id, approveData = {}) => {
      console.log('API: Approving RFC', { id, approveData });
      return api.post(`/rfc/${id}/approve`, approveData)
        .then(response => {
          console.log('API: Approve RFC success', response.data);
          return response.data;
        })
        .catch(error => {
          console.error('API: Approve RFC error', error);
          throw error;
        });
    },

    // POST /rfc/{id}/unapprove - Unapprove RFC
    unapproveRfc: (id, unapproveData = {}) => {
      console.log('API: Unapproving RFC', { id, unapproveData });
      return api.post(`/rfc/${id}/unapprove`, unapproveData)
        .then(response => {
          console.log('API: Unapprove RFC success', response.data);
          return response.data;
        })
        .catch(error => {
          console.error('API: Unapprove RFC error', error);
          throw error;
        });
    },

  // GET /rfc/{id}/approvals - Get RFC approvals
  getRfcApprovals: (id) =>
    api.get(`/rfc/${id}/approvals`).then(response => response.data),

  // GET /rfc/{id}/history - Get RFC history with pagination
  getRfcHistory: (id, params = {}) =>
    api.get(`/rfc/${id}/history`, {
      params: {
        page: params.page || 0,
        size: params.size || 20
      }
    }).then(response => response.data),

  // PATCH /rfc/{rfcId}/subsystem/{subsystemId}/confirmation - Update confirmation status
  updateSubsystemConfirmation: (rfcId, subsystemId, confirmationData) =>
    api.patch(`/rfc/${rfcId}/subsystem/${subsystemId}/confirmation`, confirmationData)
      .then(response => response.data),

  // PATCH /rfc/{rfcId}/subsystem/{subsystemId}/execution - Update execution status
  updateSubsystemExecution: (rfcId, subsystemId, executionData) =>
    api.patch(`/rfc/${rfcId}/subsystem/${subsystemId}/execution`, executionData)
      .then(response => response.data)
};