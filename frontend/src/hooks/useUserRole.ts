import { useAuth } from '../auth/AuthProvider';
import { UserRole } from '../types/api';

export const useUserRole = () => {
  const { keycloakInstance } = useAuth();

  const hasRole = (role: UserRole): boolean => {
    return keycloakInstance.hasRealmRole(role);
  };

  const hasAnyRole = (roles: UserRole[]): boolean => {
    return roles.some(role => hasRole(role));
  };

  const isAdmin = (): boolean => {
    return hasRole(UserRole.ADMIN);
  };

  const isCabManager = (): boolean => {
    return hasRole(UserRole.CAB_MANAGER);
  };

  const canPerformMassOperations = (): boolean => {
    return hasAnyRole([UserRole.CAB_MANAGER, UserRole.ADMIN]);
  };

  const getUserRoles = (): UserRole[] => {
    const roles: UserRole[] = [];
    Object.values(UserRole).forEach((role) => {
      if (hasRole(role)) {
        roles.push(role);
      }
    });
    return roles;
  };

  return {
    hasRole,
    hasAnyRole,
    isAdmin,
    isCabManager,
    canPerformMassOperations,
    getUserRoles,
  };
};
