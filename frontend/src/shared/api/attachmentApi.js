import { api } from './config';

export const attachmentApi = {
  // POST /attachment - Upload attachment
  uploadAttachment: (file) => {
    const formData = new FormData();
    formData.append('file', file);

    return api.post('/attachment', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 30000 // 30 секунд для больших файлов
    }).then(response => response.data);
  },

  // GET /attachment/{id} - Download attachment
  downloadAttachment: (id, filename) => {
    return api.get(`/attachment/${id}`, {
      responseType: 'blob'
    }).then(response => {
      // Получаем имя файла из параметра или заголовков
      let finalFilename = filename;

      // Пытаемся извлечь имя файла из заголовков Content-Disposition
      const contentDisposition = response.headers['content-disposition'];
      if (contentDisposition) {
        const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
        const matches = filenameRegex.exec(contentDisposition);
        if (matches != null && matches[1]) {
          // Удаляем кавычки если есть
          finalFilename = matches[1].replace(/['"]/g, '');
        }
      }

      // Если имя файла не получено, используем переданное или стандартное
      if (!finalFilename) {
        finalFilename = `attachment-${id}`;
      }

      // Получаем тип контента из заголовков
      const contentType = response.headers['content-type'] || 'application/octet-stream';

      // Создаем Blob с правильным типом
      const blob = new Blob([response.data], { type: contentType });

      // Создаем ссылку для скачивания
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = finalFilename;

      // Добавляем в DOM и кликаем
      document.body.appendChild(link);
      link.click();

      // Очищаем
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

      return response.data;
    });
  },

  // DELETE /attachment/{id} - Delete attachment (если API поддерживает)
  deleteAttachment: (id) =>
    api.delete(`/attachment/${id}`).then(response => response.data)
};