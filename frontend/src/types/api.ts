// RFC related types
export interface RfcStats {
  total: number;
  draft: number;
  review: number;
  approved: number;
  implemented: number;
  rejected: number;
}

export interface RecentRfc {
  id: string;
  title: string;
  status: RfcStatus;
  createdAt: string;
  author: string;
  priority: Priority;
}

export interface DashboardData {
  stats: RfcStats;
  recentRfcs: RecentRfc[];
  upcomingDeadlines: UpcomingDeadline[];
}

export interface UpcomingDeadline {
  id: string;
  title: string;
  deadline: string;
  status: RfcStatus;
}

// Enums
export const RfcStatus = {
  REQUESTED_NEW: 'REQUESTED_NEW',
  WAITING: 'WAITING', 
  APPROVED: 'APPROVED',
  WAITING_FOR_CAB: 'WAITING_FOR_CAB',
  DECLINED: 'DECLINED',
  DONE: 'DONE',
  CANCELLED: 'CANCELLED'
} as const;

export type RfcStatus = typeof RfcStatus[keyof typeof RfcStatus];

export const Priority = {
  LOW: 'LOW',
  MEDIUM: 'MEDIUM',
  HIGH: 'HIGH',
  CRITICAL: 'CRITICAL'
} as const;

export type Priority = typeof Priority[keyof typeof Priority];

// API Response types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  limit: number;
  hasNext: boolean;
  hasPrev: boolean;
}

// Full RFC entity
export interface Rfc {
  id: string;
  title: string;
  description: string;
  status: RfcStatus;
  priority: Priority;
  createdAt: string;
  updatedAt: string;
  author: User;
  executors: User[];
  reviewers: User[];
  system: System;
}

// User entity
export interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  fullName: string;
}

// System entity
export interface System {
  id: string;
  name: string;
  type?: string;
  description?: string;
  responsibleTeam?: Team;
}

// Team entity
export interface Team {
  id: string;
  name: string;
  description?: string;
  leader?: User;
  members?: User[];
}

// RFC creation request
export interface CreateRfcRequest {
  title: string;
  description: string;
  systemId: string;
  executorIds: string[];
  reviewerIds: string[];
  updatedAt: string;
  priority: Priority;
}

// RFC filters
export interface RfcFilters {
  status?: RfcStatus;
  priority?: Priority;
  systemId?: string;
  dateFrom?: string;
  dateTo?: string;
  my?: boolean;
}

// Sorting options
export interface SortOptions {
  field: 'createdAt' | 'updatedAt' | 'title' | 'status' | 'priority';
  direction: 'asc' | 'desc';
}

// Change status request
export interface ChangeStatusRequest {
  newStatus: RfcStatus;
  comment?: string;
}

// User roles
export const UserRole = {
  REQUESTER: 'REQUESTER',
  EXECUTOR: 'EXECUTOR',
  CAB_MANAGER: 'CAB_MANAGER',
  ADMIN: 'ADMIN'
} as const;

export type UserRole = typeof UserRole[keyof typeof UserRole];

// Enhanced User interface with role
export interface UserWithRole extends User {
  role: UserRole;
  team?: Team;
  keycloakId?: string;
}

// CRUD Request types
export interface CreateSystemRequest {
  name: string;
  type?: string;
  description?: string;
  responsibleTeamId: string;
}

export interface UpdateSystemRequest {
  id: string;
  name: string;
  type?: string;
  description?: string;
  responsibleTeamId: string;
}

export interface CreateTeamRequest {
  name: string;
  description?: string;
  leaderId: string;
}

export interface UpdateTeamRequest {
  id: string;
  name: string;
  description?: string;
  leaderId: string;
}

export interface CreateUserRequest {
  keycloakId: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
}

export interface UpdateUserRequest {
  id: string;
  keycloakId: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
}
