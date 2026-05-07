import api from './api';

export const registerUser   = (data) => api.post('/api/auth/register', data);
export const loginUser      = (data) => api.post('/api/auth/login', data);
export const getCurrentUser = ()     => api.get('/api/auth/me');
export const googleAuth = (token) => api.post('/api/auth/oauth/google', { token });
export const verifyOtp      = (data) => api.post('/api/auth/verify-otp', data);
export const resendOtp = (data) => api.post('/api/auth/resend-otp', data);