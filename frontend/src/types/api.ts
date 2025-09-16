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
