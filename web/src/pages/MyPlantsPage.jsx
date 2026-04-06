import { useState, useEffect } from 'react';
import { useNavigate, useLocation, NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Toast from '../components/Toast';
import {
  Leaf, Calendar, Image, Trash2, Settings, LogOut, Plus, Search, ChevronDown, Bell
} from 'lucide-react';
import { getPlants } from '../api/plantApi';
import PlantCard from '../components/PlantCard';
import AddPlantModal from '../components/AddPlantModal';
import EditPlantModal from '../components/EditPlantModal';
import DeleteModal from '../components/DeleteModal';
import PlantDetailsPage from './PlantDetailsPage';
import '../styles/plant.css';

export default function MyPlantsPage() {
  const { user, logout }  = useAuth();
  const navigate          = useNavigate();
  const location          = useLocation();
  const [toast, setToast] = useState(location.state?.toast || null);

  // --- PLANT STATE ---
  const [plants, setPlants] = useState([]);
  const [selectedPlant, setSelectedPlant] = useState(null); 
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  // --- UI & FILTER STATE ---
  const [searchQuery, setSearchQuery] = useState('');
  const [activeFilter, setActiveFilter] = useState('All');
  
  // --- MODAL STATE ---
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [editingPlant, setEditingPlant] = useState(null);
  const [deletingPlant, setDeletingPlant] = useState(null);

  useEffect(() => {
    fetchPlants();
  }, []);

  const fetchPlants = async () => {
    try {
      const res = await getPlants();
      if (res.data.success) {
        setPlants(res.data.data.plants || []);
      } else {
        setError(res.data.error?.message || 'Failed to load plants.');
      }
    } catch (err) {
      setError('Failed to connect to the server.');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getPlantStatus = (plant) => {
    if (!plant.nextDueDate) return 'Watered'; 
    const now = new Date();
    const due = new Date(plant.nextDueDate);
    const diff = Math.floor((due - now) / (1000 * 60 * 60 * 24));
    if (diff < 0) return 'Overdue';
    if (diff === 0) return 'Due Today';
    return 'Watered';
  };

  const attentionCount = plants.filter(
    p => getPlantStatus(p) === 'Due Today' || getPlantStatus(p) === 'Overdue'
  ).length;

  const filteredPlants = plants.filter(plant => {
    const matchesSearch = 
      plant.nickname.toLowerCase().includes(searchQuery.toLowerCase()) ||
      plant.speciesName.toLowerCase().includes(searchQuery.toLowerCase());
    
    if (activeFilter === 'All') return matchesSearch;
    return matchesSearch && getPlantStatus(plant) === activeFilter;
  });

  const handleAddSuccess = (newPlant) => setPlants(prev => [...prev, newPlant]);
  const handleEditSuccess = (updatedPlant) => {
    setPlants(prev => prev.map(p => p.plantId === updatedPlant.plantId ? updatedPlant : p));
  };
  const handleDeleteSuccess = (deletedId) => {
    setPlants(prev => prev.filter(p => p.plantId !== deletedId));
  };

  return (
    <div className="dashboard">
      {toast && (
        <Toast message={toast} type="success" onClose={() => setToast(null)} />
      )}

      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-logo">
          <span className="sidebar-logo-icon">🌿</span>
          <span className="sidebar-logo-name">Halaman</span>
        </div>

        <nav className="sidebar-nav">
          <NavLink to="/my-plants" className={({ isActive }) => `sidebar-link ${isActive ? 'active' : ''}`}>
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

      {/* Main Content */}
      <main className="dashboard-main">
        
        {error && <div className="auth-error-banner" style={{ marginBottom: '1.5rem' }}>{error}</div>}

        {/* --- CONDITIONAL RENDERING: Details vs Grid --- */}
        {selectedPlant ? (
          
          /* VIEW 1: Plant Details Page (No Topbar here) */
          <PlantDetailsPage 
            plant={selectedPlant} 
            onBack={() => setSelectedPlant(null)} 
          />

        ) : (
          
          /* VIEW 2: Dashboard Grid and Filters */
          <>
            {/* Topbar (Only visible on the main dashboard) */}
            <div className="dashboard-topbar">
              <div className="dashboard-welcome">
                <h1>Hello, {user?.firstName || 'John'}!</h1>
                <p>You have {attentionCount} plants that need attention today.</p>
              </div>

              <div className="topbar-actions">
                <button className="btn-bell">
                  <Bell size={24} />
                  {attentionCount > 0 && <span className="bell-badge"></span>}
                </button>
                
                <div className="weather-widget">
                  <span className="weather-icon">☀️</span>
                  <div className="weather-info">
                    <span className="weather-location">Cebu City, PH</span>
                    <span className="weather-temp">28.5°C</span>
                    <span className="weather-desc">Sunny Sky</span>
                  </div>
                </div>
              </div>
            </div>

            {/* My Plant Catalog Header */}
            <div className="catalog-header-top">
              <div className="catalog-title-wrapper">
                <div className="catalog-title-marker"></div>
                <h2 className="catalog-title-text">My Plant Catalog</h2>
              </div>
              
              <div className="catalog-header-actions">
                <button className="btn-select">Select Plants</button>
                <button className="btn-add-plant" onClick={() => setIsAddOpen(true)}>
                  <Plus size={18} /> Add New Plant
                </button>
              </div>
            </div>

            {/* Search & Filter Card */}
            <div className="search-filter-card">
              <div className="search-box">
                <Search size={18} color="#99A1AF" />
                <input 
                  type="text" 
                  placeholder="Search by nickname or species..." 
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
              </div>

              <div className="filter-sort-section">
                <div className="filter-pills">
                  {['All', 'Watered', 'Due Today', 'Overdue'].map(tab => {
                    const displayName = tab === 'Due Today' ? 'Due Today!' : tab;
                    return (
                      <button 
                        key={tab}
                        onClick={() => setActiveFilter(tab)}
                        className={`filter-pill ${activeFilter === tab ? 'active' : ''}`}
                      >
                        {displayName}
                      </button>
                    );
                  })}
                </div>
                
                <div className="vertical-divider"></div>
                
                <div className="sort-dropdown">
                  Sort by: <span>Name</span>
                  <ChevronDown size={16} color="#101828" />
                </div>
              </div>
            </div>

            {/* Dynamic Grid / Empty State */}
            {loading ? (
              <div className="catalog-empty">
                <p>Loading your green space...</p>
              </div>
            ) : filteredPlants.length > 0 ? (
              <div className="plant-grid">
                {filteredPlants.map(plant => (
                  <div 
                    key={plant.plantId} 
                    onClick={() => setSelectedPlant(plant)} 
                    style={{ cursor: 'pointer' }}
                  >
                    <PlantCard 
                      plant={plant} 
                      onEdit={() => setEditingPlant(plant)}
                      onDelete={() => setDeletingPlant(plant)}
                    />
                  </div>
                ))}
              </div>
            ) : (
              <div className="catalog-empty">
                <span className="catalog-empty-icon">🪴</span>
                <h3>No plants found</h3>
                <p>
                  {searchQuery || activeFilter !== 'All' 
                    ? "We couldn't find any plants matching your current filters." 
                    : "Start your collection by adding your first plant!"}
                </p>
              </div>
            )}
          </>
        )}
      </main>

      {/* MODALS */}
      {isAddOpen && <AddPlantModal onClose={() => setIsAddOpen(false)} onSuccess={handleAddSuccess} />}
      {editingPlant && <EditPlantModal plant={editingPlant} onClose={() => setEditingPlant(null)} onSuccess={handleEditSuccess} />}
      {deletingPlant && <DeleteModal plant={deletingPlant} onClose={() => setDeletingPlant(null)} onSuccess={handleDeleteSuccess} />}

    </div>
  );
}