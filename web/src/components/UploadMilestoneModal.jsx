import React, { useState, useRef } from 'react';
import { Upload, X, Image as ImageIcon } from 'lucide-react';
import { uploadMilestoneImage } from '../api/plantApi';
import '../styles/gallery.css';

export default function UploadMilestoneModal({ isOpen, onClose, plantId, onUploadSuccess }) {
    const [selectedFile, setSelectedFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(null);
    const [caption, setCaption] = useState('');
    const [isUploading, setIsUploading] = useState(false);
    const [error, setError] = useState(null);
    const fileInputRef = useRef(null);

    if (!isOpen) return null;

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setSelectedFile(file);
            setPreviewUrl(URL.createObjectURL(file));
            setError(null);
        }
    };

    const handleClose = () => {
        setSelectedFile(null);
        setPreviewUrl(null);
        setCaption('');
        setError(null);
        onClose();
    };

    const handleUpload = async () => {
        if (!selectedFile) {
            setError("Please select an image first.");
            return;
        }

        setIsUploading(true);
        setError(null);

        const formData = new FormData();
        formData.append('file', selectedFile);
        if (caption.trim() !== '') {
            formData.append('caption', caption);
        }

        try {
            await uploadMilestoneImage(plantId, formData);
            onUploadSuccess(); 
            handleClose();
        } catch (err) {
            console.error("Upload failed", err);
            setError("Failed to upload milestone. Please try again.");
        } finally {
            setIsUploading(false);
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <div className="modal-header">
                    <h2>Upload Milestone</h2>
                    <button className="btn-close-modal" onClick={handleClose} disabled={isUploading}>
                        <X size={20} />
                    </button>
                </div>

                <div className="modal-body">
                    {error && <div className="modal-error">{error}</div>}

                    {/* Hidden file input */}
                    <input 
                        type="file" 
                        accept="image/jpeg, image/png, image/webp" 
                        ref={fileInputRef}
                        onChange={handleFileChange}
                        style={{ display: 'none' }} 
                    />

                    {/* Image Preview / Upload Area */}
                    <div 
                        className={`upload-dropzone ${previewUrl ? 'has-image' : ''}`}
                        onClick={() => !isUploading && fileInputRef.current.click()}
                    >
                        {previewUrl ? (
                            <img src={previewUrl} alt="Preview" className="upload-preview" />
                        ) : (
                            <div className="upload-prompt">
                                <ImageIcon size={48} color="#9CA3AF" />
                                <p>Click to select a photo</p>
                                <span>JPG, PNG, WEBP</span>
                            </div>
                        )}
                    </div>

                    {/* Caption Input */}
                    <div className="form-group" style={{ marginTop: '20px' }}>
                        <label htmlFor="caption">Milestone Caption (Optional)</label>
                        <textarea 
                            id="caption"
                            placeholder="e.g., The newest leaf just unfurled!"
                            value={caption}
                            onChange={(e) => setCaption(e.target.value)}
                            rows={3}
                            disabled={isUploading}
                            className="modal-textarea"
                        />
                    </div>
                </div>

                <div className="modal-footer">
                    <button className="btn-cancel" onClick={handleClose} disabled={isUploading}>
                        Cancel
                    </button>
                    <button 
                        className="btn-submit" 
                        onClick={handleUpload} 
                        disabled={isUploading || !selectedFile}
                    >
                        {isUploading ? (
                            <span className="loading-text">Uploading...</span>
                        ) : (
                            <>
                                <Upload size={16} /> Save Milestone
                            </>
                        )}
                    </button>
                </div>
            </div>
        </div>
    );
}