import React, { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { LayoutDashboard, Users, Image as ImageIcon, LogOut } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
import LogoutModal from '../LogoutModal';
import '../../styles/admin.css'; 

export default function AdminLayout({ children }) {
    const navigate = useNavigate();
    const { logout } = useAuth();
    const [isLogoutModalOpen, setIsLogoutModalOpen] = useState(false);

    const handleConfirmLogout = () => {
        setIsLogoutModalOpen(false);
        logout();
        navigate('/login');
    };

    return (
        <div className="admin-layout">
            <aside className="admin-sidebar">
                <div className="admin-sidebar-logo">
                    <span className="logo-icon">🌿</span>
                    <div className="logo-text">
                        <span className="brand-name">HALAMAN</span>
                        <span className="brand-role">ADMIN</span>
                    </div>
                </div>

                <nav className="admin-nav">
                    <NavLink to="/admin/overview" className={({ isActive }) => `admin-nav-link ${isActive ? 'active' : ''}`}>
                        <LayoutDashboard size={18} /> Overview
                    </NavLink>
                    <NavLink to="/admin/users" className={({ isActive }) => `admin-nav-link ${isActive ? 'active' : ''}`}>
                        <Users size={18} /> Manage Users
                    </NavLink>
                    <NavLink to="/admin/gallery" className={({ isActive }) => `admin-nav-link ${isActive ? 'active' : ''}`}>
                        <ImageIcon size={18} /> Manage Gallery
                    </NavLink>
                </nav>

                <div className="admin-sidebar-bottom">
                    <button className="admin-logout-btn" onClick={() => setIsLogoutModalOpen(true)}>
                        <LogOut size={18} /> Logout
                    </button>
                </div>
            </aside>

            <main className="admin-main-content">
                {children}
            </main>

            {/* Reuse our existing Logout Modal */}
            <LogoutModal 
                isOpen={isLogoutModalOpen}
                onClose={() => setIsLogoutModalOpen(false)}
                onConfirm={handleConfirmLogout}
            />
        </div>
    );
}