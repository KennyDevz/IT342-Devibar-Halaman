import { useState } from 'react';
import { useNavigate, useLocation, NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Toast from '../components/Toast';
import {
  Leaf,
  Calendar,
  Image,
  Trash2,
  Settings,
  LogOut,
  Plus,
} from 'lucide-react';
import '../styles/dashboard.css';

export default function DashboardPage() {
  const { user, logout }  = useAuth();
  const navigate          = useNavigate();
  const location          = useLocation();
  const [toast, setToast] = useState(location.state?.toast || null);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="dashboard">

      {toast && (
        <Toast
          message={toast}
          type="success"
          onClose={() => setToast(null)}
        />
      )}

      {/* Sidebar */}
      <aside className="sidebar">

        <div className="sidebar-logo">
          <span className="sidebar-logo-icon">🌿</span>
          <span className="sidebar-logo-name">Halaman</span>
        </div>

        <nav className="sidebar-nav">
          <NavLink
            to="/dashboard"
            className={({ isActive }) =>
              `sidebar-link ${isActive ? 'active' : ''}`
            }>
            <Leaf size={18} />
            My Plants
          </NavLink>

          <NavLink
            to="/schedule"
            className={({ isActive }) =>
              `sidebar-link ${isActive ? 'active' : ''}`
            }>
            <Calendar size={18} />
            Schedule
          </NavLink>

          <NavLink
            to="/gallery"
            className={({ isActive }) =>
              `sidebar-link ${isActive ? 'active' : ''}`
            }>
            <Image size={18} />
            Gallery
          </NavLink>

          <NavLink
            to="/recycle-bin"
            className={({ isActive }) =>
              `sidebar-link ${isActive ? 'active' : ''}`
            }>
            <Trash2 size={18} />
            Recycle Bin
          </NavLink>
        </nav>

        <div className="sidebar-divider" />

        <div className="sidebar-bottom">
          <NavLink
            to="/settings"
            className={({ isActive }) =>
              `sidebar-link ${isActive ? 'active' : ''}`
            }>
            <Settings size={18} />
            Settings
          </NavLink>

          <button
            className="sidebar-logout"
            onClick={handleLogout}>
            <LogOut size={18} />
            Logout
          </button>
        </div>

      </aside>

      {/* Main Content */}
      <main className="dashboard-main">

        {/* Topbar */}
        <div className="dashboard-topbar">
          <div className="dashboard-welcome">
            <h1>Hello, {user?.firstName}!</h1>
            <p>Manage and track your plant collection.</p>
          </div>

          {/* Weather Widget Placeholder */}
          <div className="weather-widget">
            <span className="weather-icon">☀️</span>
            <div className="weather-info">
              <span className="weather-location">Your Location</span>
              <span className="weather-temp">--°C</span>
              <span className="weather-desc">Weather coming soon</span>
            </div>
          </div>
        </div>

        {/* My Plant Catalog */}
        <div className="catalog-header">
          <span className="catalog-title">My Plant Catalog</span>
          <div className="catalog-actions">
            <button className="btn-add-plant">
              <Plus size={16} />
              Add New Plant
            </button>
          </div>
        </div>

        {/* Empty State */}
        <div className="catalog-empty">
          <span className="catalog-empty-icon">🌱</span>
          <h3>No plants yet</h3>
          <p>Start your collection by adding your first plant!</p>
          <button className="btn-add-plant">
            <Plus size={16} />
            Add New Plant
          </button>
        </div>

      </main>

    </div>
  );
}