/**
 * RFC Utility Functions
 * Helper functions for RFC status, permissions, and actions
 */

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

// Константы статусов подтверждения
export const CONFIRMATION_STATUS = {
  PENDING: 'PENDING',
  CONFIRMED: 'CONFIRMED',
  REJECTED: 'REJECTED'
};

// Константы статусов выполнения
export const EXECUTION_STATUS = {
  PENDING: 'PENDING',
  IN_PROGRESS: 'IN_PROGRESS',
  DONE: 'DONE'
};

// Роли пользователей
export const USER_ROLE = {
  USER: 'USER',
  RFC_APPROVER: 'RFC_APPROVER',
  CAB_MANAGER: 'CAB_MANAGER',
  ADMIN: 'ADMIN'
};

// Константы действий
export const RFC_ACTION = {
  UPDATE: 'UPDATE',
  DELETE: 'DELETE',
  APPROVE: 'APPROVE',
  UNAPPROVE: 'UNAPPROVE', // Добавляем UNAPPROVE
  CONFIRM: 'CONFIRM',
  UPDATE_EXECUTION: 'UPDATE_EXECUTION'
};

/**
 * Форматирует дату в читаемый формат
 */
export const formatDate = (dateString) => {
  if (!dateString) return '';

  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (error) {
    console.error('Error formatting date:', error);
    return dateString;
  }
};

/**
 * Форматирует дату только в формате даты (без времени)
 */
export const formatDateOnly = (dateString) => {
  if (!dateString) return '';

  try {
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  } catch (error) {
    console.error('Error formatting date:', error);
    return dateString;
  }
};

/**
 * Форматирует дату относительно текущего времени
 */
export const formatRelativeTime = (dateString) => {
  if (!dateString) return '';

  try {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMs = now - date;
    const diffInHours = diffInMs / (1000 * 60 * 60);
    const diffInDays = diffInHours / 24;

    if (diffInHours < 1) {
      return 'только что';
    } else if (diffInHours < 24) {
      const hours = Math.floor(diffInHours);
      return `${hours} ${getRussianNoun(hours, 'час', 'часа', 'часов')} назад`;
    } else if (diffInDays < 7) {
      const days = Math.floor(diffInDays);
      return `${days} ${getRussianNoun(days, 'день', 'дня', 'дней')} назад`;
    } else {
      return formatDate(dateString);
    }
  } catch (error) {
    console.error('Error formatting relative time:', error);
    return formatDate(dateString);
  }
};

/**
 * Вспомогательная функция для склонения русских существительных
 */
const getRussianNoun = (number, one, two, five) => {
  let n = Math.abs(number);
  n %= 100;
  if (n >= 5 && n <= 20) {
    return five;
  }
  n %= 10;
  if (n === 1) {
    return one;
  }
  if (n >= 2 && n <= 4) {
    return two;
  }
  return five;
};

/**
 * Проверяет, может ли пользователь согласовывать RFC
 */
export const canApproveRfc = (user, rfc) => {
  if (!user || !rfc) return false;

  console.log('canApproveRfc check:', {
    userRole: user.role,
    rfcStatus: rfc.status,
    requesterId: rfc.requesterId,
    userId: user.id
  });

  // Проверка по ролям
  const hasApprovalRole = [USER_ROLE.ADMIN, USER_ROLE.CAB_MANAGER, USER_ROLE.RFC_APPROVER].includes(user.role);

  // Проверяем, что RFC в статусе, когда можно согласовывать
  const canBeApproved = [RFC_STATUS.NEW, RFC_STATUS.UNDER_REVIEW].includes(rfc.status);

  // Проверяем, что пользователь не является создателем RFC
  const isNotRequester = user.id !== rfc.requesterId;

  const result = hasApprovalRole && canBeApproved && isNotRequester;
  console.log('canApproveRfc result:', result);
  return result;
};

/**
 * Проверяет, может ли пользователь отменить согласование RFC
 */
export const canUnapproveRfc = (user, rfc) => {
  if (!user || !rfc) return false;

  console.log('canUnapproveRfc check:', {
    userRole: user.role,
    rfcStatus: rfc.status,
    requesterId: rfc.requesterId,
    userId: user.id
  });

  // Проверка по ролям (те же роли, что и для согласования)
  const hasApprovalRole = [USER_ROLE.ADMIN, USER_ROLE.CAB_MANAGER, USER_ROLE.RFC_APPROVER].includes(user.role);

  // Проверяем, что RFC в статусе, когда можно отменить согласование
  const canBeUnapproved = [RFC_STATUS.APPROVED, RFC_STATUS.UNDER_REVIEW].includes(rfc.status);

  // Проверяем, что пользователь не является создателем RFC
  const isNotRequester = user.id !== rfc.requesterId;

  // Дополнительно проверяем, что пользователь ранее согласовывал этот RFC
  // (это можно реализовать, если бекенд предоставляет информацию о согласованиях)
  const hasApproved = true; // Временно всегда true, можно доработать когда будут данные о согласованиях

  const result = hasApprovalRole && canBeUnapproved && isNotRequester && hasApproved;
  console.log('canUnapproveRfc result:', result);
  return result;
};

/**
 * Проверяет, может ли пользователь подтверждать подсистемы
 */
export const canConfirmSubsystems = (user, rfc) => {
  if (!user || !rfc) return false;

  console.log('canConfirmSubsystems check:', {
    userId: user.id,
    hasAffectedSystems: !!rfc.affectedSystems
  });

  // Проверка по данным подсистем
  if (!rfc.affectedSystems || !Array.isArray(rfc.affectedSystems)) {
    return false;
  }

  const isExecutor = rfc.affectedSystems.some(system => {
    if (!system || !system.affectedSubsystems || !Array.isArray(system.affectedSubsystems)) {
      return false;
    }

    return system.affectedSubsystems.some(subsystem =>
      subsystem &&
      subsystem.executorId === user.id &&
      subsystem.confirmationStatus === CONFIRMATION_STATUS.PENDING
    );
  });

  console.log('canConfirmSubsystems result:', isExecutor);
  return isExecutor;
};

/**
 * Проверяет, может ли пользователь обновлять статус выполнения
 */
export const canUpdateExecution = (user, rfc) => {
  if (!user || !rfc) return false;

  console.log('canUpdateExecution check:', {
    userId: user.id,
    hasAffectedSystems: !!rfc.affectedSystems
  });

  // Проверка по данным подсистем
  if (!rfc.affectedSystems || !Array.isArray(rfc.affectedSystems)) {
    return false;
  }

  const isExecutor = rfc.affectedSystems.some(system => {
    if (!system || !system.affectedSubsystems || !Array.isArray(system.affectedSubsystems)) {
      return false;
    }

    return system.affectedSubsystems.some(subsystem =>
      subsystem &&
      subsystem.executorId === user.id &&
      subsystem.confirmationStatus === CONFIRMATION_STATUS.CONFIRMED &&
      subsystem.executionStatus !== EXECUTION_STATUS.DONE
    );
  });

  console.log('canUpdateExecution result:', isExecutor);
  return isExecutor;
};

/**
 * Проверяет, может ли пользователь удалить RFC
 */
export const canDeleteRfc = (user, rfc) => {
  if (!user || !rfc) return false;

  // Проверяем, что бекенд разрешает действие DELETE
  const hasDeleteAction = rfc.actions?.includes('DELETE');

  console.log('canDeleteRfc check:', {
    userId: user.id,
    requesterId: rfc.requesterId,
    hasDeleteAction,
    actions: rfc.actions
  });

  return hasDeleteAction;
};

/**
 * Проверяет, может ли пользователь редактировать RFC
 */
export const canUpdateRfc = (user, rfc) => {
  if (!user || !rfc) return false;

  console.log('canUpdateRfc check:', {
    userId: user.id,
    requesterId: rfc.requesterId,
    rfcStatus: rfc.status,
    hasUpdateAction: rfc.actions?.includes('UPDATE')
  });

  // Проверяем, что бекенд разрешает действие UPDATE
  const hasUpdateAction = rfc.actions?.includes('UPDATE');

  // Дополнительно проверяем статус RFC - запрещаем редактирование для IMPLEMENTED
  const cannotEditStatuses = [RFC_STATUS.IMPLEMENTED];
  const isEditableStatus = !cannotEditStatuses.includes(rfc.status);

  return hasUpdateAction && isEditableStatus;
};

/**
 * Получает подсистемы, которые пользователь может подтвердить
 */
export const getConfirmableSubsystems = (user, rfc) => {
  if (!user || !rfc) return [];

  if (!rfc.affectedSystems || !Array.isArray(rfc.affectedSystems)) {
    return [];
  }

  const confirmableSubsystems = [];

  rfc.affectedSystems.forEach(system => {
    if (!system || !system.affectedSubsystems || !Array.isArray(system.affectedSubsystems)) {
      return;
    }

    system.affectedSubsystems.forEach(subsystem => {
      if (subsystem &&
          subsystem.executorId === user.id &&
          subsystem.confirmationStatus === CONFIRMATION_STATUS.PENDING) {
        confirmableSubsystems.push({
          ...subsystem,
          systemName: system.systemName || 'Неизвестная система',
          systemId: system.systemId,
          // Используем affectedSubsystemId для API вызовов
          affectedSubsystemId: subsystem.id // В данном случае subsystem.id - это ID связи в affected_subsystems
        });
      }
    });
  });

  console.log('getConfirmableSubsystems result:', confirmableSubsystems);
  return confirmableSubsystems;
};

/**
 * Получает подсистемы, для которых пользователь может обновлять статус выполнения
 */
export const getExecutableSubsystems = (user, rfc) => {
  if (!user || !rfc) return [];

  if (!rfc.affectedSystems || !Array.isArray(rfc.affectedSystems)) {
    return [];
  }

  const executableSubsystems = [];

  rfc.affectedSystems.forEach(system => {
    if (!system || !system.affectedSubsystems || !Array.isArray(system.affectedSubsystems)) {
      return;
    }

    system.affectedSubsystems.forEach(subsystem => {
      if (subsystem &&
          subsystem.executorId === user.id &&
          subsystem.confirmationStatus === CONFIRMATION_STATUS.CONFIRMED &&
          subsystem.executionStatus !== EXECUTION_STATUS.DONE) {
        executableSubsystems.push({
          ...subsystem,
          systemName: system.systemName || 'Неизвестная система',
          systemId: system.systemId,
          // Используем affectedSubsystemId для API вызовов
          affectedSubsystemId: subsystem.id // В данном случае subsystem.id - это ID связи в affected_subsystems
        });
      }
    });
  });

  console.log('getExecutableSubsystems result:', executableSubsystems);
  return executableSubsystems;
};

/**
 * Получает русскоязычное название статуса
 */
export const getStatusLabel = (status) => {
  const statusLabels = {
    [RFC_STATUS.NEW]: 'Новый',
    [RFC_STATUS.UNDER_REVIEW]: 'На рассмотрении',
    [RFC_STATUS.APPROVED]: 'Согласован',
    [RFC_STATUS.IMPLEMENTED]: 'Внедрен',
    [RFC_STATUS.REJECTED]: 'Отклонен',
    [CONFIRMATION_STATUS.PENDING]: 'Ожидает подтверждения',
    [CONFIRMATION_STATUS.CONFIRMED]: 'Подтверждено',
    [CONFIRMATION_STATUS.REJECTED]: 'Отклонено',
    [EXECUTION_STATUS.PENDING]: 'Ожидает выполнения',
    [EXECUTION_STATUS.IN_PROGRESS]: 'В процессе',
    [EXECUTION_STATUS.DONE]: 'Выполнено'
  };
  return statusLabels[status] || status;
};

/**
 * Получает CSS класс для статуса
 */
export const getStatusClass = (status) => {
  const statusClasses = {
    [RFC_STATUS.NEW]: 'status-new',
    [RFC_STATUS.UNDER_REVIEW]: 'status-under-review',
    [RFC_STATUS.APPROVED]: 'status-approved',
    [RFC_STATUS.IMPLEMENTED]: 'status-implemented',
    [RFC_STATUS.REJECTED]: 'status-rejected',
    [CONFIRMATION_STATUS.PENDING]: 'status-pending',
    [CONFIRMATION_STATUS.CONFIRMED]: 'status-confirmed',
    [CONFIRMATION_STATUS.REJECTED]: 'status-rejected',
    [EXECUTION_STATUS.PENDING]: 'status-pending',
    [EXECUTION_STATUS.IN_PROGRESS]: 'status-in-progress',
    [EXECUTION_STATUS.DONE]: 'status-done'
  };
  return statusClasses[status] || 'status-unknown';
};

/**
 * Получает русскоязычное название срочности
 */
export const getUrgencyLabel = (urgency) => {
  const urgencyLabels = {
    [URGENCY.EMERGENCY]: 'Критическая',
    [URGENCY.URGENT]: 'Срочная',
    [URGENCY.PLANNED]: 'Плановая'
  };
  return urgencyLabels[urgency] || urgency;
};

/**
 * Получает CSS класс для срочности
 */
export const getUrgencyClass = (urgency) => {
  const urgencyClasses = {
    [URGENCY.EMERGENCY]: 'urgency-emergency',
    [URGENCY.URGENT]: 'urgency-urgent',
    [URGENCY.PLANNED]: 'urgency-planned'
  };
  return urgencyClasses[urgency] || 'urgency-unknown';
};

/**
 * Проверяет, может ли пользователь выполнить действие над RFC
 */
export const canPerformAction = (user, rfc, action) => {
  if (!user || !rfc) return false;

  switch (action) {
    case RFC_ACTION.UPDATE:
      return canUpdateRfc(user, rfc);
    case RFC_ACTION.APPROVE:
      return canApproveRfc(user, rfc);
    case RFC_ACTION.UNAPPROVE:
      return canUnapproveRfc(user, rfc);
    case RFC_ACTION.CONFIRM:
      return canConfirmSubsystems(user, rfc);
    case RFC_ACTION.UPDATE_EXECUTION:
      return canUpdateExecution(user, rfc);
    case RFC_ACTION.DELETE:
      return canDeleteRfc(user, rfc);
    default:
      return false;
  }
};

/**
 * Получает доступные действия для пользователя и RFC
 */
export const getAvailableActions = (user, rfc) => {
  if (!user || !rfc) return [];

  const actions = [];

  if (canUpdateRfc(user, rfc)) {
    actions.push(RFC_ACTION.UPDATE);
  }

  if (canApproveRfc(user, rfc)) {
    actions.push(RFC_ACTION.APPROVE);
  }

  if (canUnapproveRfc(user, rfc)) {
    actions.push(RFC_ACTION.UNAPPROVE);
  }

  if (canConfirmSubsystems(user, rfc)) {
    actions.push(RFC_ACTION.CONFIRM);
  }

  if (canUpdateExecution(user, rfc)) {
    actions.push(RFC_ACTION.UPDATE_EXECUTION);
  }

  // Для удаления используем проверку через canDeleteRfc, которая основана на actions от бекенда
  if (canDeleteRfc(user, rfc)) {
    actions.push(RFC_ACTION.DELETE);
  }

  return actions;
};