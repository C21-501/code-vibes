import { api } from './config';

export const attachmentApi = {
  // POST /attachment - Upload attachment
  uploadAttachment: (file) => {
    const formData = new FormData();
    formData.append('file', file);

    return api.post('/attachment', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }).then(response => response.data);
  },

  // GET /attachment/{id} - Download attachment
  downloadAttachment: (id) =>
    api.get(`/attachment/${id}`, {
      responseType: 'blob'
    }).then(response => response.data)
};