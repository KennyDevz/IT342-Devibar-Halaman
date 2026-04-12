import React from 'react';
import { Trash2, RefreshCcw, AlertTriangle } from 'lucide-react';
import '../styles/recyclebin.css'; // Uses the exact same CSS we just wrote

export default function RecycleBinModal({ 
    modalConfig, 
    isProcessing, 
    onClose, 
    onConfirm, 
    totalItems 
}) {
    if (!modalConfig.isOpen) return null;

    const { type, plant } = modalConfig;
    
    // Default configs
    let icon = <Trash2 size={24} />;
    let isDanger = true;
    let subtitle, title, desc, btnText;

    if (type === 'empty-bin') {
        icon = <AlertTriangle size={24} />;
        title = "Empty Bin?";
        desc = <span style={{display: 'inline'}}>This action is irreversible. All items currently in your Recycle Bin will be <strong className="danger-text">permanently deleted</strong>.</span>;
        btnText = "CONFIRM PERMANENT DELETION";
    } 
    else if (type === 'restore-all') {
        icon = <RefreshCcw size={24} />;
        isDanger = false;
        subtitle = "RESTORE ALL?";
        title = "Recover All Plants";
        desc = `This will move all ${totalItems} items back to your main collection dashboard.`;
        btnText = "CONFIRM RESTORE ALL";
    } 
    else if (type === 'delete-single') {
        subtitle = "PERMANENT DELETE";
        title = plant?.nickname || plant?.speciesName;
        desc = "Are you sure you want to permanently delete this plant? This action cannot be undone.";
        btnText = "DELETE PERMANENTLY";
    } 
    else if (type === 'restore-single') {
        icon = <RefreshCcw size={24} />;
        isDanger = false;
        subtitle = "RESTORE PLANT";
        title = plant?.nickname || plant?.speciesName;
        desc = "Would you like to restore this plant to your collection? It will reappear in your main dashboard.";
        btnText = "CONFIRM RESTORE";
    }

    return (
        <div className="rb-modal-overlay">
            <div className="rb-modal-card">
                <div className={`rb-modal-icon-wrapper ${isDanger ? 'danger' : 'success'}`}>
                    {icon}
                </div>
                
                {subtitle && (
                    <p className={`rb-modal-subtitle ${isDanger ? 'danger' : 'success'}`}>
                        {subtitle}
                    </p>
                )}
                
                <h2 className="rb-modal-title">{title}</h2>
                <p className="rb-modal-desc">{desc}</p>
                
                <button 
                    className={`rb-modal-btn-main ${isDanger ? 'danger' : 'success'}`}
                    onClick={onConfirm}
                    disabled={isProcessing}
                >
                    {isProcessing ? "PROCESSING..." : btnText}
                </button>
                
                <button 
                    className="rb-modal-btn-cancel" 
                    onClick={onClose}
                    disabled={isProcessing}
                >
                    CANCEL
                </button>
            </div>
        </div>
    );
}