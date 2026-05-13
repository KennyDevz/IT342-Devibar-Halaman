import React, { useState, useEffect, useCallback } from 'react';
import { User, Mail, Lock, Trash2, CheckCircle, AlertCircle, Loader, TriangleAlert } from 'lucide-react';
import { useAuth } from '../../../core/AuthContext';
import { updateProfile, changePassword, deleteAccount } from '../api/settingsApi';
import '../../../styles/settings.css';

// --- Inline Toast Component ---
function Toast({ message, type, onClose }) {
    useEffect(() => {
        const timer = setTimeout(onClose, 4000);
        return () => clearTimeout(timer);
    }, [onClose]);

    return (
        <div className={`settings-toast settings-toast--${type}`}>
            {type === 'success' ? <CheckCircle size={16} /> : <AlertCircle size={16} />}
            <span>{message}</span>
        </div>
    );
}

// --- Delete Confirmation Modal ---
function DeleteConfirmModal({ onConfirm, onCancel, loading }) {
    return (
        <div className="delete-modal-overlay" onClick={onCancel}>
            <div className="delete-modal" onClick={(e) => e.stopPropagation()}>
                <div className="delete-modal-icon">
                    <TriangleAlert size={28} />
                </div>
                <h2 className="delete-modal-title">Delete Account?</h2>
                <p className="delete-modal-body">
                    This action is <strong>permanent and irreversible</strong>. All your plants,
                    care schedules, maintenance logs, and photos will be deleted.
                </p>
                <div className="delete-modal-actions">
                    <button
                        className="delete-modal-btn-cancel"
                        onClick={onCancel}
                        disabled={loading}
                    >
                        Cancel
                    </button>
                    <button
                        className="delete-modal-btn-confirm"
                        onClick={onConfirm}
                        disabled={loading}
                    >
                        {loading
                            ? <><Loader size={14} className="spin" /> Deleting...</>
                            : <><Trash2 size={14} /> Yes, Delete My Account</>
                        }
                    </button>
                </div>
            </div>
        </div>
    );
}

export default function SettingsPage() {
    const { user, login } = useAuth();

    // Toast state
    const [toast, setToast] = useState(null);

    // Profile Form State
    const [profileForm, setProfileForm] = useState({ firstName: '', lastName: '', email: '' });
    const [profileLoading, setProfileLoading] = useState(false);

    // Security Form State
    const [securityForm, setSecurityForm] = useState({ currentPassword: '', newPassword: '' });
    const [securityLoading, setSecurityLoading] = useState(false);

    // Delete state
    const [deleteLoading, setDeleteLoading] = useState(false);
    const [showDeleteModal, setShowDeleteModal] = useState(false);

    // Pre-fill form when user loads
    useEffect(() => {
        if (user) {
            setProfileForm({
                firstName: user.firstName || '',
                lastName: user.lastName || '',
                email: user.email || ''
            });
        }
    }, [user]);

    const showToast = (message, type = 'success') => {
        setToast({ message, type });
    };

    const handleProfileChange = (e) => {
        setProfileForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    const handleSecurityChange = (e) => {
        setSecurityForm(prev => ({ ...prev, [e.target.name]: e.target.value }));
    };

    // ── Save Profile ─────────────────────────────────────
    const handleSaveProfile = async (e) => {
        e.preventDefault();
        setProfileLoading(true);
        try {
            const res = await updateProfile({
                firstName: profileForm.firstName,
                lastName: profileForm.lastName,
            });
            // Update auth context so the navbar also reflects the change
            if (res.data?.data) {
                login({ user: { ...user, ...res.data.data }, accessToken: localStorage.getItem('accessToken'), refreshToken: localStorage.getItem('refreshToken') });
            }
            showToast('Profile updated successfully!', 'success');
        } catch (err) {
            const msg = err.response?.data?.error?.message || 'Failed to update profile.';
            showToast(msg, 'error');
        } finally {
            setProfileLoading(false);
        }
    };

    // ── Change Password ───────────────────────────────────
    const handleUpdatePassword = async (e) => {
        e.preventDefault();

        if (securityForm.newPassword.length < 6) {
            showToast('New password must be at least 6 characters.', 'error');
            return;
        }
        if (securityForm.currentPassword === securityForm.newPassword) {
            showToast('New password must differ from the current password.', 'error');
            return;
        }

        setSecurityLoading(true);
        try {
            await changePassword(securityForm);
            setSecurityForm({ currentPassword: '', newPassword: '' });
            showToast('Password changed successfully!', 'success');
        } catch (err) {
            const msg = err.response?.data?.error?.message || 'Failed to change password.';
            showToast(msg, 'error');
        } finally {
            setSecurityLoading(false);
        }
    };

    // ── Delete Account ────────────────────────────────────
    const handleConfirmDelete = useCallback(async () => {
        setDeleteLoading(true);
        try {
            await deleteAccount();
            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            window.location.href = '/login';
        } catch (err) {
            const msg = err.response?.data?.error?.message || 'Failed to delete account.';
            showToast(msg, 'error');
            setDeleteLoading(false);
            setShowDeleteModal(false);
        }
    }, []);

    return (
        <div className="settings-container">
            {/* Delete Confirmation Modal */}
            {showDeleteModal && (
                <DeleteConfirmModal
                    onConfirm={handleConfirmDelete}
                    onCancel={() => !deleteLoading && setShowDeleteModal(false)}
                    loading={deleteLoading}
                />
            )}
            {/* Toast notification */}
            {toast && (
                <Toast
                    message={toast.message}
                    type={toast.type}
                    onClose={() => setToast(null)}
                />
            )}

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
                            <button className="btn-change-avatar" disabled>Change Avatar</button>
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
                                    required
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
                                    required
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
                                    disabled
                                />
                            </div>
                            <span className="settings-field-hint">Email cannot be changed.</span>
                        </div>

                        <button type="submit" className="btn-save-profile" disabled={profileLoading}>
                            {profileLoading ? <><Loader size={14} className="spin" /> Saving...</> : 'Save Changes'}
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
                                placeholder="At least 6 characters"
                                value={securityForm.newPassword}
                                onChange={handleSecurityChange}
                                required
                            />
                        </div>

                        <button type="submit" className="btn-update-password" disabled={securityLoading}>
                            {securityLoading ? <><Loader size={14} className="spin" /> Updating...</> : 'Update Password'}
                        </button>
                    </form>

                    <div className="security-divider"></div>

                    <button
                        type="button"
                        className="btn-delete-account"
                        onClick={() => setShowDeleteModal(true)}
                        disabled={deleteLoading}
                    >
                        <Trash2 size={16} /> Delete My Account
                    </button>
                </div>

            </div>
        </div>
    );
}