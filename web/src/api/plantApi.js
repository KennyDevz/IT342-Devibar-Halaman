import api from './api';

export const getPlants    = ()         => api.get('/api/plants');
export const createPlant  = (data)     => api.post('/api/plants', data);
export const updatePlant  = (id, data) => api.put(`/api/plants/${id}`, data);
export const deletePlant  = (id)       => api.delete(`/api/plants/${id}`);

export const uploadPlantImage = (id, formData) => 
  api.post(`/api/plants/${id}/images`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

export const getPlantImages = (id) => api.get(`/api/plants/${id}/images`);
export const getPlantSchedule = (plantId) => api.get(`/api/plants/${plantId}/schedule`);

export const logMaintenance = (data) => api.post('/api/maintenance', data);
export const getPlantMaintenanceLogs = (plantId) => api.get(`/api/maintenance/${plantId}`);