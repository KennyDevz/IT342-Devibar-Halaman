import React, { useState } from 'react';
import { usePlants } from '../context/PlantContext';
import { ArrowLeft } from 'lucide-react';
import GrowthTimeline from '../components/GrowthTimeline';
import '../styles/gallery.css';
import '../styles/plant.css'; 

export default function GrowthGalleryPage() {
    const { plants, images, loading } = usePlants();
    const [selectedPlant, setSelectedPlant] = useState(null);

    // If a plant is selected, render the Timeline View instead of the Grid
    if (selectedPlant) {
        return (
            <div className="timeline-container">
                <div className="details-nav timeline-nav">
                    <button className="btn-back" onClick={() => setSelectedPlant(null)}>
                        <div className="icon-circle"><ArrowLeft size={16} /></div>
                        Back to Gallery
                    </button>
                    <h2>{selectedPlant.nickname}'s Growth Journey</h2>
                </div>
                
                <GrowthTimeline plantId={selectedPlant.plantId} />
            </div>
        );
    }

    // Otherwise, render the Main Gallery Grid
    return (
        <div className="gallery-container">
            <header className="catalog-header-top gallery-header">
                <div className="catalog-title-wrapper">
                    <div className="catalog-title-marker"></div>
                    <h2 className="catalog-title-text">Growth Gallery</h2>
                </div>
                <p className="gallery-subtitle">Select a plant to view its visual history.</p>
            </header>

            {loading ? (
                <p className="gallery-loading">Loading your gallery...</p>
            ) : plants.length === 0 ? (
                <div className="catalog-empty">
                    <span className="catalog-empty-icon">📷</span>
                    <h3>No photos yet</h3>
                    <p>Start adding plants to build your gallery!</p>
                </div>
            ) : (
                <div className="gallery-grid">
                    {plants.map(plant => {
                        const imageUrl = images[plant.id] || images[plant.plantId];
                        
                        return (
                            <div 
                                key={plant.plantId} 
                                className="gallery-card"
                                onClick={() => setSelectedPlant(plant)}
                            >
                                {imageUrl ? (
                                    <img 
                                        src={imageUrl} 
                                        alt={plant.nickname} 
                                        className="gallery-card-image"
                                    />
                                ) : (
                                    <div className="gallery-card-placeholder">
                                        🌿
                                    </div>
                                )}
                                
                                <div className="gallery-card-overlay">
                                    <h3>{plant.nickname}</h3>
                                </div>
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
}