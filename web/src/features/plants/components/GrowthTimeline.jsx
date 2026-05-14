import React, { useState, useEffect } from 'react';
import { getPlantImageHistory, deletePlantImage } from '../api/plantApi';
import { Upload, Trash2, AlertTriangle } from 'lucide-react';
import UploadMilestoneModal from './UploadMilestoneModal';
import '../../../styles/gallery.css';

export default function GrowthTimeline({ plantId }) {
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);   
    const [sortOrder, setSortOrder] = useState('desc');
    const [isModalOpen, setIsModalOpen] = useState(false);

    const [imageToDelete, setImageToDelete] = useState(null); 
    const [isDeleting, setIsDeleting] = useState(false); 

    const fetchHistory = async () => {
        setLoading(true);
        try {
            const response = await getPlantImageHistory(plantId);
            console.log("🔥 RAW BACKEND DATA:", response.data);
            setHistory(response.data);
            setError(null);
        } catch (err) {
            console.error("Failed to load timeline", err);
            setError("Could not load the growth timeline. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    // 2. Use it on initial load
    useEffect(() => {
        if (plantId) {
            fetchHistory();
        }
    }, [plantId]);

    const confirmDelete = async () => {
        if (!imageToDelete) return;
        
        setIsDeleting(true);
        try {
            await deletePlantImage(plantId, imageToDelete);
            await fetchHistory(); 
            setImageToDelete(null); 
        } catch (err) {
            console.error("Failed to delete milestone", err);
            alert("Failed to delete the milestone. Please try again.");
        } finally {
            setIsDeleting(false);
        }
    };

    // Helpers
    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' }).toUpperCase();
    };

    const optimizeCloudinaryUrl = (url) => {
        if (!url || !url.includes('cloudinary.com')) return url;
        return url.replace('/upload/', '/upload/c_scale,w_400,q_auto/');
    };

    // Sort the history based on the dropdown selection
    const sortedHistory = [...history].sort((a, b) => {
        const dateA = new Date(a.uploadedAt);
        const dateB = new Date(b.uploadedAt);
        return sortOrder === 'desc' ? dateB - dateA : dateA - dateB;
    });

    if (loading) return <div className="timeline-loading">Loading growth history...</div>;
    if (error) return <div className="timeline-error">{error}</div>;

    return (
        <div className="milestone-container">
            {/* Controls Row */}
            <div className="milestone-controls">
                <div className="sort-wrapper">
                    <select 
                        value={sortOrder} 
                        onChange={(e) => setSortOrder(e.target.value)}
                        className="sort-dropdown"
                    >
                        <option value="desc">Sort by Date (Newest)</option>
                        <option value="asc">Sort by Date (Oldest)</option>
                    </select>
                </div>
                
                <button className="btn-upload-milestone" onClick={() => setIsModalOpen(true)}>
                    <Upload size={18} />
                    <span>Upload Milestone</span>
                </button>
            </div>

            {history.length === 0 ? (
                <div className="timeline-empty">
                    <span className="timeline-empty-icon">🌱</span>
                    <h3>No history yet</h3>
                    <p>Upload a progress photo to start tracking growth!</p>
                </div>
            ) : (
                <div className="milestone-grid">
                    {sortedHistory.map((record, index) => {
                        const isFirst = sortOrder === 'asc' ? index === 0 : index === sortedHistory.length - 1;
                        const tagText = isFirst ? "ARRIVAL" : `UPDATE`;

                        return (
                            <div key={index} className="milestone-card">
                                <div className="milestone-image-wrapper">
                                    <div className="milestone-tag">{tagText}</div>
                                    <img 
                                        src={optimizeCloudinaryUrl(record.fileUrl)} 
                                        alt={`Growth on ${formatDate(record.uploadedAt)}`} 
                                        className="milestone-image"
                                        loading="lazy"
                                    />
                                </div>
                                <div className="milestone-content">
                                    <div className="milestone-date">
                                        <span className="calendar-icon">📅</span> {formatDate(record.uploadedAt)}
                                    </div>
                                    <button 
                                            onClick={() => setImageToDelete(record.imageId)}
                                            style={{ background: 'none', border: 'none', color: '#dc2626', cursor: 'pointer', padding: '4px' }}
                                            title="Delete Milestone"
                                        >
                                            <Trash2 size={18} />
                                        </button>
                                    <p className="milestone-caption">
                                        {record.caption ? `"${record.caption}"` : "No caption provided."}
                                    </p>
                                </div>
                            </div>
                        );
                    })}
                </div>
            )}

            {imageToDelete && (
                <div className="custom-modal-overlay">
                    <div className="custom-modal-content">
                        <div className="custom-modal-icon">
                            <AlertTriangle size={32} color="#dc2626" />
                        </div>
                        <h3>Delete Milestone?</h3>
                        <p>Are you sure you want to delete this photo? This action cannot be undone and it will be removed from your timeline permanently.</p>
                        
                        <div className="custom-modal-actions">
                            <button 
                                className="btn-cancel" 
                                onClick={() => setImageToDelete(null)}
                                disabled={isDeleting}
                            >
                                Cancel
                            </button>
                            <button 
                                className="btn-confirm-delete" 
                                onClick={confirmDelete}
                                disabled={isDeleting}
                            >
                                {isDeleting ? 'Deleting...' : 'Yes, Delete'}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* The Modal */}
            <UploadMilestoneModal 
                isOpen={isModalOpen} 
                onClose={() => setIsModalOpen(false)} 
                plantId={plantId}
                onUploadSuccess={fetchHistory} 
            />
        </div>
    );
}