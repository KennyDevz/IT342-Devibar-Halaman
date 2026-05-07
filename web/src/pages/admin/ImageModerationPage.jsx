import React, { useState, useEffect } from 'react';
import { Search, Trash2, AlertTriangle, ExternalLink, Shield } from 'lucide-react';
import { getAllImages, deleteModerationImage } from '../../api/adminApi';
import '../../styles/image-moderation.css';

export default function ImageModerationPage() {
    const [images, setImages] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchImages = async () => {
            try {
                const res = await getAllImages();
                if (res.data.success) {
                    setImages(res.data.data);
                }
            } catch (error) {
                console.error("Failed to fetch images", error);
            } finally {
                setLoading(false);
            }
        };
        fetchImages();
    }, []);


    const handleDelete = async (imageId) => {
        const confirmDelete = window.confirm("Are you sure you want to permanently delete this image? This cannot be undone.");
        
        if (confirmDelete) {
            try {
                // Optimistically remove it from UI
                setImages(images.filter(img => img.id !== imageId));
                
                // Tell backend to delete it
                await deleteModerationImage(imageId);
                
            } catch (error) {
                console.error("Failed to delete image", error);
                alert("Failed to delete image. It may have already been removed.");
            }
        }
    };
    


    const filteredImages = images.filter(img => 
        img.uploaderName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        img.plantName.toLowerCase().includes(searchTerm.toLowerCase())
    );


    return (
        <div className="admin-gallery-container">
            <div className="admin-page-header">
                <div className="title-wrapper">
                    <div className="title-marker"></div>
                    <h2>Image Moderation</h2>
                </div>
                <p>Global view of all user-uploaded content. Monitor and remove inappropriate images.</p>
            </div>

            <div className="gallery-controls">
                <div className="search-bar-wrapper">
                    <Search className="search-icon" size={18} />
                    <input 
                        type="text" 
                        placeholder="Search by uploader or plant name..." 
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-input"
                    />
                </div>
                <div className="moderation-badge">
                    <Shield size={18} className="shield-icon" />
                    <span>Active Moderation Mode</span>
                </div>
            </div>

            <div className="image-grid">
                {filteredImages.map((img) => (
                    <div key={img.id} className="image-card">
                        <div className="image-wrapper">
                            <img src={img.imageUrl} alt={img.plantName} />
                            <div className="image-overlay">
                                <button className="overlay-btn delete" onClick={() => handleDelete(img.id)}>
                                    <Trash2 size={18} />
                                </button>
                                <button className="overlay-btn view" onClick={() => window.open(img.imageUrl, '_blank')}>
                                    <ExternalLink size={18} />
                                </button>
                            </div>
                        </div>
                        <div className="image-info">
                            <h4>{img.plantName}</h4>
                            <p className="uploader">By {img.uploaderName}</p>
                            <p className="date">{new Date(img.uploadedAt).toLocaleDateString()}</p>
                        </div>
                    </div>
                ))}
            </div>

            
        </div>
    );
}