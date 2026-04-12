import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Trash2 } from 'lucide-react';
import { getRecycledPlants, restorePlant, permanentlyDeletePlant } from '../api/plantApi';
import RecycleBinModal from '../components/RecycleBinModal'; // New Component
import '../styles/recyclebin.css';

export default function RecycleBinPage() {
    const [deletedPlants, setDeletedPlants] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isProcessing, setIsProcessing] = useState(false);
    
    // Controlled state for the Modal component
    const [modalConfig, setModalConfig] = useState({ 
        isOpen: false, 
        type: null, 
        plant: null 
    });
    
    const navigate = useNavigate();

    const fetchTrash = async () => {
        setLoading(true);
        try {
            const response = await getRecycledPlants();
            // Maps the response to the plants list
            setDeletedPlants(response.data.data.plants || []);
        } catch (error) {
            console.error("Failed to load recycle bin", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchTrash();
    }, []);

    // --- Helper for Mockup UI ---
    const getDaysAgo = (deletedAtString) => {
        if (!deletedAtString) return "Deleted recently";
        const deletedDate = new Date(deletedAtString);
        const today = new Date();
        const diffDays = Math.floor(Math.abs(today - deletedDate) / (1000 * 60 * 60 * 24));
        return diffDays === 0 ? "Deleted today" : `Deleted ${diffDays} days ago`;
    };

    // --- Modal Handlers ---
    const openModal = (type, plant = null) => {
        setModalConfig({ isOpen: true, type, plant });
    };

    const closeModal = () => {
        setModalConfig({ isOpen: false, type: null, plant: null });
    };

    // --- Logic Execution ---
    const handleConfirmAction = async () => {
        setIsProcessing(true);
        const { type, plant } = modalConfig;

        try {
            if (type === 'delete-single') {
                await permanentlyDeletePlant(plant.plantId);
            } else if (type === 'restore-single') {
                await restorePlant(plant.plantId);
            } else if (type === 'restore-all') {
                await Promise.all(deletedPlants.map(p => restorePlant(p.plantId)));
            } else if (type === 'empty-bin') {
                await Promise.all(deletedPlants.map(p => permanentlyDeletePlant(p.plantId)));
            }
            
            // Refresh list after any successful action
            await fetchTrash();
        } catch (error) {
            alert("The action could not be completed. Please try again.");
        } finally {
            setIsProcessing(false);
            closeModal();
        }
    };

    if (loading) return <div className="rb-page-wrapper">Loading Recycle Bin...</div>;

    return (
        <div className="rb-page-wrapper">
            <div className="rb-container">
                
                {/* Header Section */}
                <div className="rb-top-bar">
                    <button className="btn-back" onClick={() => navigate(-1)}>
                        <ArrowLeft size={16} /> Back
                    </button>
                    
                    <div className="rb-header-row">
                        <div className="rb-title-section">
                            <h1>Recycle Bin</h1>
                            <p>Items here will be permanently deleted after 30 days.</p>
                        </div>
                        
                        <div className="rb-bulk-actions">
                            <button 
                                className="btn-restore-all" 
                                onClick={() => openModal('restore-all')}
                                disabled={deletedPlants.length === 0 || isProcessing}
                            >
                                Restore All
                            </button>
                            <button 
                                className="btn-empty-bin" 
                                onClick={() => openModal('empty-bin')}
                                disabled={deletedPlants.length === 0 || isProcessing}
                            >
                                Empty Bin
                            </button>
                        </div>
                    </div>
                </div>

                {/* List Container */}
                <div className="rb-list-wrapper">
                    {deletedPlants.length === 0 ? (
                        <div style={{ textAlign: 'center', padding: '60px 0', color: '#9CA3AF' }}>
                            <Trash2 size={48} style={{ opacity: 0.2, marginBottom: '16px' }} />
                            <h3>Your recycle bin is clear</h3>
                        </div>
                    ) : (
                        deletedPlants.map(plant => (
                            <div key={plant.plantId} className="rb-list-item">
                                <div className="rb-item-left">
                                    <img 
                                        src="https://via.placeholder.com/72/E5E7EB/9CA3AF?text=Leaf" 
                                        alt={plant.nickname} 
                                        className="rb-item-image"
                                    />
                                    <div className="rb-item-info">
                                        <h3>{plant.nickname}</h3>
                                        <p className="species">{plant.speciesName}</p>
                                        <p className="deleted-date">{getDaysAgo(plant.deletedAt)}</p>
                                    </div>
                                </div>
                                
                                <div className="rb-item-actions">
                                    <button 
                                        className="btn-restore-single" 
                                        onClick={() => openModal('restore-single', plant)}
                                    >
                                        Restore
                                    </button>
                                    <button 
                                        className="btn-delete-single" 
                                        onClick={() => openModal('delete-single', plant)}
                                    >
                                        <Trash2 size={16} />
                                    </button>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>

            {/* Reusable Modal Component */}
            <RecycleBinModal 
                modalConfig={modalConfig}
                isProcessing={isProcessing}
                onClose={closeModal}
                onConfirm={handleConfirmAction}
                totalItems={deletedPlants.length}
            />
        </div>
    );
}