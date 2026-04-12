import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Leaf, Calendar, Image, Trash2, Settings, LogOut } from 'lucide-react';
import '../styles/sidebar.css'; 

export default function Sidebar() {
    const navigate = useNavigate();
    const { logout } = useAuth();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <aside className="sidebar">
            <div className="sidebar-logo">
                <span className="sidebar-logo-icon">🌿</span>
                <span className="sidebar-logo-name">Halaman</span>
            </div>

            <nav className="sidebar-nav">
                <NavLink to="/plants" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
                    <Leaf size={18} /> My Plants
                </NavLink>
                <NavLink to="/schedule" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
                    <Calendar size={18} /> Schedule
                </NavLink>
                <NavLink to="/gallery" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
                    <Image size={18} /> Gallery
                </NavLink>
                <NavLink to="/recycle-bin" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
                    <Trash2 size={18} /> Recycle Bin
                </NavLink>
            </nav>

            <div className="sidebar-divider" />

            <div className="sidebar-bottom">
                <NavLink to="/settings" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
                    <Settings size={18} /> Settings
                </NavLink>
                <button className="sidebar-logout" onClick={handleLogout}>
                    <LogOut size={18} /> Logout
                </button>
            </div>
        </aside>
    );
};

