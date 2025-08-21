// User Types
export interface User {
  id: number;
  username: string;
  email: string;
  name: string;
  surname: string;
  phone?: string;
  role: 'USER' | 'ADMIN';
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UserResponse {
  content: User[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

// Lead Types
export interface Lead {
  id: number;
  userId: number;
  companyName: string;
  contactName: string;
  email: string;
  phone: string;
  status: 'ACTIVE' | 'CONTACTED' | 'CONVERTED' | 'LOST';
  notes: string;
  createdAt: string;
  updatedAt: string;
}

export interface LeadResponse {
  content: Lead[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

// Email Types
export interface Email {
  id: string;
  from: string;
  to: string[];
  cc?: string[];
  bcc?: string[];
  subject: string;
  body: string;
  contentType: string;
  date: string;
  isRead: boolean;
  labels: string[];
  attachments?: EmailAttachment[];
}

export interface EmailAttachment {
  filename: string;
  contentType: string;
  size: number;
}

export interface EmailResponse {
  content: Email[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

// Email Draft Types
export interface EmailDraft {
  id: number;
  userId: number;
  leadId?: number;
  subject: string;
  body: string;
  contentType: string;
  toEmails: string[];
  ccEmails?: string[];
  bccEmails?: string[];
  attachments?: EmailAttachment[];
  provider?: string;
  templateName?: string;
  templateData?: any;
  status: 'DRAFT' | 'SENT';
  createdByRobot: boolean;
  createdAt: string;
  updatedAt: string;
  sentAt?: string;
}

export interface EmailDraftResponse {
  content: EmailDraft[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

// Subscription Types
export interface SubscriptionType {
  id: number;
  name: string;
  code: string;
  stripePriceId: string;
  durationInDays: number;
  description: string;
  dailyLimit: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UserSubscription {
  id: number;
  userId: number;
  subscriptionType: SubscriptionType;
  subsStartDate: string;
  subsEndDate: string;
  isActive: boolean;
  status: 'ACTIVE' | 'EXPIRED' | 'GRACE_PERIOD';
  createdAt: string;
  updatedAt: string;
}

export interface SubscriptionResponse {
  content: UserSubscription[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

// Admin Dashboard Types
export interface DashboardData {
  totalUsers: number;
  activeSubscriptions: number;
  totalLeads: number;
  emailsSentToday: number;
  systemHealth: 'HEALTHY' | 'WARNING' | 'ERROR';
  recentErrors: DashboardError[];
}

export interface DashboardError {
  timestamp: string;
  message: string;
  level: string;
}

// Log Types
export interface SystemLog {
  id: number;
  level: 'INFO' | 'WARN' | 'ERROR' | 'SECURITY';
  type: 'SYSTEM' | 'BUSINESS' | 'SECURITY';
  message: string;
  details: string;
  userId?: number;
  ipAddress: string;
  userAgent: string;
  timestamp: string;
}

export interface LogResponse {
  content: SystemLog[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export interface LogAnalysis {
  totalLogs: number;
  errorCount: number;
  warningCount: number;
  securityCount: number;
  topErrors: LogError[];
  topUsers: LogUser[];
}

export interface LogError {
  message: string;
  count: number;
}

export interface LogUser {
  userId: number;
  logCount: number;
}

// System Info Types
export interface SystemInfo {
  version: string;
  javaVersion: string;
  springVersion: string;
  databaseVersion: string;
  uptime: string;
  memoryUsage: string;
  diskUsage: string;
  activeConnections: number;
}

// Email Limit Types
export interface EmailLimitStatus {
  userId: number;
  date: string;
  sentCount: number;
  dailyLimit: number;
  remaining: number;
  limitReached: boolean;
}

// Bounce Email Types
export interface BounceEmailStats {
  totalBounces: number;
  hardBounces: number;
  softBounces: number;
  topBouncedEmails: BouncedEmail[];
  bounceRate: number;
}

export interface BouncedEmail {
  email: string;
  bounceCount: number;
}

// Robot Types
export interface Robot {
  id: number;
  name: string;
  type: string;
  isActive: boolean;
  lastRunAt?: string;
  status: 'RUNNING' | 'STOPPED' | 'ERROR';
  config: any;
}

// Payment Types
export interface CheckoutSession {
  sessionId: string;
  url: string;
  subscriptionCode: string;
  userId: number;
}

export interface CustomerPortal {
  url: string;
}

// Auth Types
export interface LoginCredentials {
  username: string;
  password: string;
}

export interface RegisterData {
  username: string;
  email: string;
  password: string;
  name: string;
  surname: string;
  phone?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}

export interface OAuthLoginRequest {
  provider: 'GOOGLE' | 'MICROSOFT';
  redirectUri: string;
}

export interface OAuthResponse {
  authorizationUrl: string;
}

// API Response Types
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  errorCode?: string;
  errors?: ValidationError[];
}

export interface ValidationError {
  field: string;
  message: string;
}

// Pagination Types
export interface PaginationParams {
  page: number;
  size: number;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  first: boolean;
  last: boolean;
} 