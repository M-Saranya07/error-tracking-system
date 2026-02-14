 import axios from 'axios';

const API_BASE_URL = 'http://localhost:8085/api';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const getErrors = async (filters = {}) => {
  try {
    const params = new URLSearchParams();
    if (filters.statusCode) params.append('statusCode', filters.statusCode);
    if (filters.severity) params.append('severity', filters.severity);
    if (filters.applicationName) params.append('applicationName', filters.applicationName);
    
    const response = await api.get(`/errors?${params}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching errors:', error);
    throw error;
  }
};

export const getAlerts = async (minutes = 60) => {
  try {
    const response = await api.get(`/alerts/recent?minutes=${minutes}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching alerts:', error);
    throw error;
  }
};

export const getAlertStatistics = async () => {
  try {
    const response = await api.get('/alerts/statistics');
    return response.data;
  } catch (error) {
    console.error('Error fetching alert statistics:', error);
    throw error;
  }
};

export const testEmail = async () => {
  try {
    const response = await api.post('/alerts/test-email');
    return response.data;
  } catch (error) {
    console.error('Error testing email:', error);
    throw error;
  }
};

// Backwards-compatible alias: some modules import `testAlerts`.
export const testAlerts = testEmail;
