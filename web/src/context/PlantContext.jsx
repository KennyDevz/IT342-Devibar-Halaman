import React, { createContext, useState, useContext, useEffect } from 'react';
import { getPlants, getPlantImages } from '../api/plantApi';
import { useAuth } from './AuthContext';

const PlantContext = createContext(null);

export const PlantProvider = ({ children }) => {
    const { user } = useAuth();
    const [plants, setPlants] = useState([]);
    const [loading, setLoading] = useState(true);
    
    const [images, setImages] = useState({}); 

    useEffect(() => {
        if (user) {
            fetchPlants();
        } else {
            setPlants([]); 
            setImages({}); 
            setLoading(false);
        }
    }, [user]);

    const fetchPlants = async () => {
        setLoading(true);
        try {
            const res = await getPlants();
            if (res.data.success) {
                setPlants(res.data.data.plants || []);
            }
        } catch (error) {
            console.error("Failed to fetch plants", error);
        } finally {
            setLoading(false);
        }
    };

    const loadPlantImage = async (plantId) => {
        if (images[plantId] !== undefined) return;

        try {
            const res = await getPlantImages(plantId);
            if (res.data.success && res.data.data && res.data.data.length > 0) {
                setImages(prev => ({ ...prev, [plantId]: res.data.data[0].fileUrl }));
            } else {
                setImages(prev => ({ ...prev, [plantId]: null }));
            }
        } catch (error) {
            setImages(prev => ({ ...prev, [plantId]: null }));
        }
    };

    const addPlantToState = (newPlant) => setPlants(prev => [...prev, newPlant]);
    
    const updatePlantInState = (updatedPlant) => {
        setPlants(prev => prev.map(p => p.plantId === updatedPlant.plantId ? updatedPlant : p));
    };
    
    const removePlantFromState = (deletedId) => {
        setPlants(prev => prev.filter(p => p.plantId !== deletedId));
        setImages(prev => {
            const newImages = { ...prev };
            delete newImages[deletedId];
            return newImages;
        });
    };

    return (
        <PlantContext.Provider value={{ 
            plants, 
            loading, 
            images,           
            loadPlantImage,   
            fetchPlants, 
            addPlantToState, 
            updatePlantInState, 
            removePlantFromState 
        }}>
            {children}
        </PlantContext.Provider>
    );
};

export const usePlants = () => useContext(PlantContext);