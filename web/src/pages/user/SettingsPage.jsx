import React, { useState, useEffect } from 'react';
import { User, Mail, Lock, Trash2 } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import '../../styles/settings.css';

export default function SettingsPage() {
    const { user } = useAuth(); // Grab the logged-in user from your context

    // Profile Form State
    const [profileForm, setProfileForm] = useState({
        firstName: '',
        lastName: '',
        email: ''
    });

    // Security Form State
    const [securityForm, setSecurityForm] = useState({
        currentPassword: '',
        newPassword: ''
    });

    // Pre-fill the form when the component loads
    useEffect(() => {
        if (user) {
            setProfileForm({
                firstName: user.firstName || '',
                lastName: user.lastName || '',
                email: user.email || ''
            });
        }
    }, [user]);

    const handleProfileChange = (e) => {
        setProfileForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleSecurityChange = (e) => {
        setSecurityForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleSaveProfile = (e) => {
        e.preventDefault();
        // TODO: Call your future API endpoint (e.g., updateProfile(profileForm))
        console.log("Saving profile:", profileForm);
        alert("Profile details saved! (Backend wiring needed)");
    };

    const handleUpdatePassword = (e) => {
        e.preventDefault();
        // TODO: Call your future API endpoint (e.g., updatePassword(securityForm))
        console.log("Updating password:", securityForm);
        alert("Password updated! (Backend wiring needed)");
    };

    const handleDeleteAccount = () => {
        if (window.confirm("Are you sure you want to permanently delete your account? All your plants and photos will be lost.")) {
            // TODO: Call your future API endpoint to delete the user
            console.log("Deleting account...");
            alert("Account deleted! (Backend wiring needed)");
        }
    };

    return (
        <div className="settings-container">
            <div className="settings-header">
                <h1>Account Settings</h1>
                <p>Manage your personal information and security preferences.</p>
            </div>

            <div className="settings-grid">
                
                {/* LEFT CARD: Profile Information */}
                <div className="settings-card">
                    <div className="profile-avatar-section">
                        <div className="avatar-placeholder">
                            <User size={32} />
                        </div>
                        <div className="avatar-info">
                            <h3>Profile Picture</h3>
                            <button className="btn-change-avatar">Change Avatar</button>
                        </div>
                    </div>

                    <form onSubmit={handleSaveProfile}>
                        <div className="settings-form-row">
                            <div className="settings-form-group">
                                <label>First Name</label>
                                <input 
                                    type="text" 
                                    name="firstName"
                                    className="settings-input" 
                                    value={profileForm.firstName}
                                    onChange={handleProfileChange}
                                />
                            </div>
                            <div className="settings-form-group">
                                <label>Last Name</label>
                                <input 
                                    type="text" 
                                    name="lastName"
                                    className="settings-input" 
                                    value={profileForm.lastName}
                                    onChange={handleProfileChange}
                                />
                            </div>
                        </div>

                        <div className="settings-form-group">
                            <label>Email Address</label>
                            <div className="settings-input-wrapper">
                                <Mail size={18} className="settings-input-icon" />
                                <input 
                                    type="email" 
                                    name="email"
                                    className="settings-input with-icon" 
                                    value={profileForm.email}
                                    onChange={handleProfileChange}
                                    disabled // Usually best to disable email changes unless handling verification
                                />
                            </div>
                        </div>

                        <button type="submit" className="btn-save-profile">
                            Save Changes
                        </button>
                    </form>
                </div>

                {/* RIGHT CARD: Security */}
                <div className="settings-card">
                    <div className="security-header">
                        <div className="security-icon-wrapper">
                            <Lock size={20} />
                        </div>
                        <h3>Security</h3>
                    </div>

                    <form onSubmit={handleUpdatePassword}>
                        <div className="settings-form-group">
                            <label>Current Password</label>
                            <input 
                                type="password" 
                                name="currentPassword"
                                className="settings-input" 
                                placeholder="••••••••"
                                value={securityForm.currentPassword}
                                onChange={handleSecurityChange}
                                required
                            />
                        </div>

                        <div className="settings-form-group">
                            <label>New Password</label>
                            <input 
                                type="password" 
                                name="newPassword"
                                className="settings-input" 
                                placeholder="New password"
                                value={securityForm.newPassword}
                                onChange={handleSecurityChange}
                                required
                            />
                        </div>

                        <button type="submit" className="btn-update-password">
                            Update Password
                        </button>
                    </form>

                    <div className="security-divider"></div>

                    <button type="button" className="btn-delete-account" onClick={handleDeleteAccount}>
                        <Trash2 size={16} /> Delete My Account
                    </button>
                </div>

            </div>
        </div>
    );
}