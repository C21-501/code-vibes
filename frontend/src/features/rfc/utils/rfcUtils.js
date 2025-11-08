// Константы статусов RFC
export const RFC_STATUS = {
  NEW: 'NEW',
  UNDER_REVIEW: 'UNDER_REVIEW',
  APPROVED: 'APPROVED',
  IMPLEMENTED: 'IMPLEMENTED',
  REJECTED: 'REJECTED'
};

// Константы срочности
export const URGENCY = {
  EMERGENCY: 'EMERGENCY',
  URGENT: 'URGENT',
  PLANNED: 'PLANNED'
};

// Функция для получения текстового представления статуса
export const getStatusLabel = (status) => {
  switch (status) {
    case RFC_STATUS.NEW:
      return 'Новый';
    case RFC_STATUS.UNDER_REVIEW:
      return 'На рассмотрении';
    case RFC_STATUS.APPROVED:
      return 'Одобрен';
    case RFC_STATUS.IMPLEMENTED:
      return 'Внедрен';
    case RFC_STATUS.REJECTED:
      return 'Отклонен';
    default:
      return 'Неизвестно';
  }
};

// Функция для получения текстового представления срочности
export const getUrgencyLabel = (urgency) => {
  switch (urgency) {
    case URGENCY.EMERGENCY:
      return 'Безотлагательное';
    case URGENCY.URGENT:
      return 'Срочное';
    case URGENCY.PLANNED:
      return 'Плановое';
    default:
      return 'Неизвестно';
  }
};

// Функция для получения CSS класса статуса
export const getStatusClass = (status) => {
  switch (status) {
    case RFC_STATUS.NEW:
      return 'status-new';
    case RFC_STATUS.UNDER_REVIEW:
      return 'status-under-review';
    case RFC_STATUS.APPROVED:
      return 'status-approved';
    case RFC_STATUS.IMPLEMENTED:
      return 'status-implemented';
    case RFC_STATUS.REJECTED:
      return 'status-rejected';
    default:
      return 'status-unknown';
  }
};

// Функция для получения CSS класса срочности
export const getUrgencyClass = (urgency) => {
  switch (urgency) {
    case URGENCY.EMERGENCY:
      return 'urgency-emergency';
    case URGENCY.URGENT:
      return 'urgency-urgent';
    case URGENCY.PLANNED:
      return 'urgency-planned';
    default:
      return 'urgency-unknown';
  }
};

// Функция для форматирования даты
export const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('ru-RU');
};

// Функция проверки доступных действий
export const canPerformAction = (rfc, action, user) => {
  if (!rfc.actions || !Array.isArray(rfc.actions)) {
    return false;
  }

  // Проверяем, есть ли действие в массиве доступных действий
  return rfc.actions.includes(action);
};

// Функция для получения доступных действий
export const getAvailableActions = (rfc, user) => {
  if (!rfc.actions || !Array.isArray(rfc.actions)) {
    return [];
  }

  return rfc.actions.filter(action => {
    // Дополнительная логика проверки прав пользователя
    switch (action) {
      case 'UPDATE':
        return user && (user.id === rfc.requesterId || user.role === 'ADMIN');
      case 'DELETE':
        return user && (user.id === rfc.requesterId || user.role === 'ADMIN');
      case 'APPROVE':
        return user && (user.role === 'RFC_APPROVER' || user.role === 'CAB_MANAGER' || user.role === 'ADMIN');
      default:
        return true;
    }
  });
};