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

// Действия по изменению статуса RFC
export const RFC_ACTIONS = {
  SUBMIT: 'SUBMIT',           // NEW → UNDER_REVIEW
  APPROVE: 'APPROVE',         // UNDER_REVIEW → APPROVED
  REJECT: 'REJECT',           // UNDER_REVIEW → REJECTED
  IMPLEMENT: 'IMPLEMENT',     // APPROVED → IMPLEMENTED
  REOPEN: 'REOPEN'            // REJECTED → UNDER_REVIEW
};

// Workflow configuration
export const STATUS_WORKFLOW = {
  [RFC_STATUS.NEW]: {
    next: [RFC_STATUS.UNDER_REVIEW],
    actions: [RFC_ACTIONS.SUBMIT],
    allowedRoles: ['REQUESTER', 'ADMIN'],
    label: 'Новый',
    description: 'RFC создан, но еще не отправлен на рассмотрение'
  },
  [RFC_STATUS.UNDER_REVIEW]: {
    next: [RFC_STATUS.APPROVED, RFC_STATUS.REJECTED],
    actions: [RFC_ACTIONS.APPROVE, RFC_ACTIONS.REJECT],
    allowedRoles: ['CAB_MANAGER', 'ADMIN'],
    label: 'На рассмотрении',
    description: 'RFC находится на рассмотрении CAB менеджера'
  },
  [RFC_STATUS.APPROVED]: {
    next: [RFC_STATUS.IMPLEMENTED],
    actions: [RFC_ACTIONS.IMPLEMENT],
    allowedRoles: ['EXECUTOR', 'ADMIN'],
    label: 'Одобрен',
    description: 'RFC одобрен и готов к внедрению'
  },
  [RFC_STATUS.IMPLEMENTED]: {
    next: [],
    actions: [],
    allowedRoles: [],
    label: 'Внедрен',
    description: 'RFC успешно внедрен'
  },
  [RFC_STATUS.REJECTED]: {
    next: [RFC_STATUS.UNDER_REVIEW],
    actions: [RFC_ACTIONS.REOPEN],
    allowedRoles: ['REQUESTER', 'ADMIN'],
    label: 'Отклонен',
    description: 'RFC отклонен CAB менеджером'
  }
};

// Labels for actions
export const ACTION_LABELS = {
  [RFC_ACTIONS.SUBMIT]: 'Отправить на рассмотрение',
  [RFC_ACTIONS.APPROVE]: 'Одобрить RFC',
  [RFC_ACTIONS.REJECT]: 'Отклонить RFC',
  [RFC_ACTIONS.IMPLEMENT]: 'Отметить как внедренный',
  [RFC_ACTIONS.REOPEN]: 'Переоткрыть RFC'
};

// Descriptions for actions
export const ACTION_DESCRIPTIONS = {
  [RFC_ACTIONS.SUBMIT]: 'Переводит RFC в статус "На рассмотрении" для оценки CAB менеджера',
  [RFC_ACTIONS.APPROVE]: 'Одобряет RFC для дальнейшего внедрения исполнителями',
  [RFC_ACTIONS.REJECT]: 'Отклоняет RFC с указанием причины',
  [RFC_ACTIONS.IMPLEMENT]: 'Отмечает RFC как успешно внедренный',
  [RFC_ACTIONS.REOPEN]: 'Возвращает отклоненный RFC на повторное рассмотрение'
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

// Get next status based on action
export const getNextStatus = (currentStatus, action) => {
  const statusMap = {
    [RFC_ACTIONS.SUBMIT]: RFC_STATUS.UNDER_REVIEW,
    [RFC_ACTIONS.APPROVE]: RFC_STATUS.APPROVED,
    [RFC_ACTIONS.REJECT]: RFC_STATUS.REJECTED,
    [RFC_ACTIONS.IMPLEMENT]: RFC_STATUS.IMPLEMENTED,
    [RFC_ACTIONS.REOPEN]: RFC_STATUS.UNDER_REVIEW
  };
  return statusMap[action] || currentStatus;
};

// Get available actions for RFC and user
export const getAvailableActions = (rfc, user) => {
  const currentStatus = rfc.status;
  const workflowConfig = STATUS_WORKFLOW[currentStatus];

  if (!workflowConfig) return [];

  return workflowConfig.actions.filter(action => {
    // Check user role
    if (!workflowConfig.allowedRoles.includes(user?.role)) return false;

    // Additional business logic
    switch (action) {
      case RFC_ACTIONS.SUBMIT:
      case RFC_ACTIONS.REOPEN:
        return user?.id === rfc.requesterId || user?.role === 'ADMIN';
      default:
        return true;
    }
  }).map(action => ({
    value: action,
    label: ACTION_LABELS[action],
    description: ACTION_DESCRIPTIONS[action],
    nextStatus: getNextStatus(currentStatus, action)
  }));
};

// Функция проверки доступных действий
export const canPerformAction = (rfc, action, user) => {
  if (!rfc.actions || !Array.isArray(rfc.actions)) {
    return false;
  }

  // Проверяем, есть ли действие в массиве доступных действий
  return rfc.actions.includes(action);
};