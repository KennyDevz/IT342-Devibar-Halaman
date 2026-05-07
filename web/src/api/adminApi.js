import api from './api';

export const getSystemMetrics = () => api.get('/api/admin/metrics');
export const getRecentActivity = () => api.get('/api/admin/activity');

//USER MANAGEMENT:
export const getAllUsers = () => api.get('/api/admin/users');
export const toggleUserStatus = (id) => api.put(`/api/admin/users/${id}/toggle-status`);

//IMAGE MODERATION:
export const getAllImages = () => api.get('/api/admin/images');
export const deleteModerationImage = (id) => api.delete(`/api/admin/images/${id}`);