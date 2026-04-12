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

export const getCurrentWeather = () => {
    return api.get('/api/weather/current'); 
};

export const getPlantImageHistory = (plantId) => {
    return api.get(`/api/plants/${plantId}/images/history`);
};

export const uploadMilestoneImage = (plantId, formData) => {
    return api.post(`/api/plants/${plantId}/images`, formData, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });
};

export const getRecycledPlants = () => {
    return api.get('/api/plants/recycle-bin');
};


export const restorePlant = (plantId) => {
    return api.put(`/api/plants/${plantId}/restore`);
};

export const permanentlyDeletePlant = (plantId) => {
    return api.delete(`/api/plants/${plantId}/permanent`);
};