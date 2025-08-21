import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/v1';

// Axios instance oluştur
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Cookie'leri gönder
});

// Request interceptor - cookie'ler otomatik gönderilir
api.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - 401 durumunda login'e yönlendir
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (credentials: { email: string; password: string }) =>
    api.post('/auth/login', credentials),
  
  register: (userData: {
    username: string;
    email: string;
    password: string;
    name: string;
    surname: string;
    phone?: string;
  }) => api.post('/users/register', userData),
  
  logout: () => api.post('/auth/logout'),
  
  me: () => api.get('/auth/me'),
  
  refresh: (refreshToken: string) =>
    api.post('/users/refresh', { refreshToken }),
};

// OAuth API
export const oauthAPI = {
  login: (provider: string, redirectUri: string) =>
    api.post('/oauth/login', { provider, redirectUri }),
  
  callback: (code: string, state: string) =>
    api.get(`/oauth/callback?code=${code}&state=${state}`),
};

// User API
export const userAPI = {
  getAll: (page = 0, size = 10) =>
    api.get(`/users?page=${page}&size=${size}`),
  
  getById: (id: number) => api.get(`/users/${id}`),
  
  update: (id: number, data: any) => api.put(`/users/${id}`, data),
  
  delete: (id: number) => api.delete(`/users/${id}`),
  
  checkPermissions: (id: number, permissions: string[]) =>
    api.post(`/users/${id}/permissions`, { permissions }),
};

// Lead API
export const leadAPI = {
  getAll: (page = 0, size = 10, status?: string) =>
    api.get(`/leads?page=${page}&size=${size}${status ? `&status=${status}` : ''}`),
  
  getById: (id: number) => api.get(`/leads/${id}`),
  
  create: (data: any) => api.post('/leads', data),
  
  update: (id: number, data: any) => api.put(`/leads/${id}`, data),
  
  delete: (id: number) => api.delete(`/leads/${id}`),
  
  search: (query: string, status?: string) =>
    api.get(`/leads/search?query=${query}${status ? `&status=${status}` : ''}`),
};

// Email API
export const emailAPI = {
  send: (data: any) => api.post('/emails/send', data),
  
  getAll: (page = 0, size = 10, label?: string) =>
    api.get(`/emails?page=${page}&size=${size}${label ? `&label=${label}` : ''}`),
  
  getById: (id: string) => api.get(`/emails/${id}`),
  
  delete: (id: string) => api.delete(`/emails/${id}`),
  
  markAsRead: (id: string) => api.post(`/emails/${id}/mark-read`),
};

// Email Draft API
export const emailDraftAPI = {
  getAll: (page = 0, size = 10, status?: string) =>
    api.get(`/email-drafts?page=${page}&size=${size}${status ? `&status=${status}` : ''}`),
  
  getById: (id: number) => api.get(`/email-drafts/${id}`),
  
  create: (data: any) => api.post('/email-drafts', data),
  
  update: (id: number, data: any) => api.put(`/email-drafts/${id}`, data),
  
  delete: (id: number) => api.delete(`/email-drafts/${id}`),
  
  send: (id: number, userId: number) => api.post(`/email-drafts/${id}/send`, { userId }),
  
  getRobotDrafts: (userId: number, page = 0, size = 10) =>
    api.get(`/email-drafts/robot?userId=${userId}&page=${page}&size=${size}`),
  
  createRobotDraft: (data: any) => api.post('/email-drafts/robot', data),
  
  getAllDrafts: (userId: number, page = 0, size = 10) =>
    api.get(`/email-drafts/all?userId=${userId}&page=${page}&size=${size}`),
};

// Subscription API
export const subscriptionAPI = {
  getAll: (page = 0, size = 10) =>
    api.get(`/subscriptions?page=${page}&size=${size}`),
  
  getById: (id: number) => api.get(`/subscriptions/${id}`),
  
  create: (data: any) => api.post('/subscriptions', data),
  
  update: (id: number, data: any) => api.put(`/subscriptions/${id}`, data),
  
  delete: (id: number) => api.delete(`/subscriptions/${id}`),
};

// Payment API
export const paymentAPI = {
  createCheckout: (data: any) => api.post('/payments/checkout', data),
  
  getSuccess: (sessionId: string) => api.get(`/payments/success?session_id=${sessionId}`),
  
  getCancel: (sessionId: string) => api.get(`/payments/cancel?session_id=${sessionId}`),
  
  getPortal: (userId: number) => api.get(`/payments/portal?userId=${userId}`),
};

// Admin API
export const adminAPI = {
  getDashboard: () => api.get('/admin/dashboard'),
  
  getLogs: (page = 0, size = 10, level?: string, type?: string) =>
    api.get(`/logs?page=${page}&size=${size}${level ? `&level=${level}` : ''}${type ? `&type=${type}` : ''}`),
  
  cleanupLogs: (days = 30) => api.get(`/logs/cleanup?days=${days}`),
  
  emergencyCleanup: () => api.get('/logs/emergency-cleanup'),
  
  getLogAnalysis: (startDate: string, endDate: string) =>
    api.get(`/logs/analysis?startDate=${startDate}&endDate=${endDate}`),
  
  getSystemInfo: () => api.get('/system/info'),
};

// Email Limits API
export const emailLimitAPI = {
  getStatus: (userId: number) => api.get(`/email-limits/status?userId=${userId}`),
  
  getRemaining: (userId: number) => api.get(`/email-limits/remaining?userId=${userId}`),
  
  reset: (userId: number) => api.post(`/email-limits/reset?userId=${userId}`),
};

// Bounce Email API
export const bounceEmailAPI = {
  getStats: (startDate: string, endDate: string) =>
    api.get(`/bounce-emails/stats?startDate=${startDate}&endDate=${endDate}`),
  
  processAll: () => api.post('/bounce-emails/process-all'),
  
  check: (emails: string[]) => api.post('/bounce-emails/check', { emails }),
};

export default api; 