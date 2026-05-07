import React from 'react';
import { LogOut } from 'lucide-react';

export default function LogoutModal({ isOpen, onClose, onConfirm }) {
    if (!isOpen) return null;

    return (
        <div className="rb-modal-overlay" style={{ zIndex: 9999 }}>
            <div className="rb-modal-card">
                <div className="rb-modal-icon-wrapper danger">
                    <LogOut size={24} />
                </div>
                
                <h2 className="rb-modal-title">Log Out?</h2>
                <p className="rb-modal-desc">
                    Are you sure you want to log out of your account? You will need to sign back in to manage your plants.
                </p>
                
                <button 
                    className="rb-modal-btn-main danger"
                    onClick={onConfirm}
                >
                    YES, LOG OUT
                </button>
                
                <button 
                    className="rb-modal-btn-cancel" 
                    onClick={onClose}
                >
                    CANCEL
                </button>
            </div>
        </div>
    );
}