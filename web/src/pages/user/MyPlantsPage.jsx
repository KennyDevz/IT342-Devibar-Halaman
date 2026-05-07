import { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { usePlants } from '../../context/PlantContext';
import Toast from '../../components/Toast';
import { Plus, Search, ChevronDown, Bell } from 'lucide-react';
import PlantCard from '../../components/plant/PlantCard';
import AddPlantModal from '../../components/plant/AddPlantModal';
import EditPlantModal from '../../components/plant/EditPlantModal';
import DeleteModal from '../../components/plant/DeleteModal';
import PlantDetailsPage from './PlantDetailsPage';
import { getCurrentWeather } from '../../api/plantApi';
import '../../styles/plant.css';

export default function MyPlantsPage() {
  const { user } = useAuth();
  const location = useLocation();
  const [toast, setToast] = useState(location.state?.toast || null);

  // --- GLOBAL STATE ---
  const { plants, loading, addPlantToState, updatePlantInState, removePlantFromState } = usePlants();
  
  // --- LOCAL UI STATE ---
  const [selectedPlant, setSelectedPlant] = useState(null); 
  const [searchQuery, setSearchQuery] = useState('');
  const [activeFilter, setActiveFilter] = useState('All');
  const [sortBy, setSortBy] = useState('name-asc');

  const [weather, setWeather] = useState({ temp: '--', humidity: '--', isDay: true, code: 0, location: 'Loading...'});
  
  // --- MODAL STATE ---
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [editingPlant, setEditingPlant] = useState(null);
  const [deletingPlant, setDeletingPlant] = useState(null);

  // --- CLEAN DATE LOGIC ---
  const getPlantStatus = (plant) => {
    if (!plant.nextDueDate) return 'Watered'; 
    
    const today = new Date();
    today.setHours(0, 0, 0, 0); 
    
    const dueDate = new Date(plant.nextDueDate);
    dueDate.setHours(0, 0, 0, 0);
    
    const diffTime = dueDate.getTime() - today.getTime();
    const diffDays = Math.round(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) return 'Overdue';
    if (diffDays === 0) return 'Due Today';
    return 'Watered';
  };

  useEffect(() => {
    const fetchWeather = async () => {
        // 1. Check the browser's temporary storage first
        const cachedWeather = sessionStorage.getItem('halaman_weather');
        const cachedTime = sessionStorage.getItem('halaman_weather_time');

        // 2. If we have data, and it is less than 30 minutes (1,800,000 ms) old, use it instantly!
        if (cachedWeather && cachedTime && (Date.now() - cachedTime < 1800000)) {
            setWeather(JSON.parse(cachedWeather));
            return; 
        }

        try {
            const res = await getCurrentWeather();
            if (res.data) {
                const freshWeatherData = { 
                    temp: res.data.temperature, 
                    humidity: res.data.humidity,
                    isDay: res.data.isDay,
                    code: res.data.weatherCode,
                    location: res.data.location || 'Did not work'
                };

                setWeather(freshWeatherData);

                // 4. Save the fresh data and the exact timestamp to sessionStorage
                sessionStorage.setItem('halaman_weather', JSON.stringify(freshWeatherData));
                sessionStorage.setItem('halaman_weather_time', Date.now());
            }
        } catch (error) {
            console.error("Failed to load weather widget data");
        }
    };
    
    fetchWeather();
  }, []);

  const getWeatherIcon = (code, isDay) => {
    // Standard WMO Weather Codes mapping
    if (code === 0) return isDay ? '☀️' : '🌙'; // Clear
    if (code === 1 || code === 2) return isDay ? '🌤️' : '☁️'; // Partly cloudy
    if (code === 3) return '☁️'; // Overcast
    if (code >= 45 && code <= 48) return '🌫️'; // Fog
    if (code >= 51 && code <= 67) return '🌧️'; // Rain or Drizzle
    if (code >= 71 && code <= 77) return '❄️'; // Snow
    if (code >= 80 && code <= 82) return '🌦️'; // Rain showers
    if (code >= 95 && code <= 99) return '⛈️'; // Thunderstorm
    
    return isDay ? '☀️' : '🌙'; // Fallback
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

  const sortedPlants = [...filteredPlants].sort((a, b) => {
    if (sortBy === 'name-asc') {
      return a.nickname.localeCompare(b.nickname);
    } else if (sortBy === 'name-desc') {
      return b.nickname.localeCompare(a.nickname);
    } else if (sortBy === 'date-desc') {
      return new Date(b.createdAt) - new Date(a.createdAt);
    } else if (sortBy === 'date-asc') {
      return new Date(a.createdAt) - new Date(b.createdAt);
    }
    return 0;
  });

  // Automatically update the global state when modals finish their actions
  const handleAddSuccess = (newPlant) => addPlantToState(newPlant);
  const handleEditSuccess = (updatedPlant) => updatePlantInState(updatedPlant);
  const handleDeleteSuccess = (deletedId) => removePlantFromState(deletedId);

  return (
    <>
      {toast && (
        <Toast message={toast} type="success" onClose={() => setToast(null)} />
      )}

      {selectedPlant ? (
        <PlantDetailsPage 
          plant={selectedPlant} 
          onBack={() => setSelectedPlant(null)} 
        />
      ) : (
        <>
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
                  <span className="weather-icon">{getWeatherIcon(weather.code, weather.isDay)}</span>
                  <div className="weather-info">
                      <span className="weather-location">{weather.location}</span>
                      <span className="weather-temp">{weather.temp}°C</span>
                      <span className="weather-desc">{weather.humidity}% Humidity</span>
                  </div>
              </div>
            </div>
          </div>

          <div className="catalog-header-top">
            <div className="catalog-title-wrapper">
              <div className="catalog-title-marker"></div>
              <h2 className="catalog-title-text">My Plant Catalog</h2>
            </div>
            
            <div className="catalog-header-actions">
              <button className="btn-add-plant" onClick={() => setIsAddOpen(true)}>
                <Plus size={18} /> Add New Plant
              </button>
            </div>
          </div>

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
              
              <div className="sort-dropdown" style={{ position: 'relative', display: 'flex', alignItems: 'center', gap: '4px' }}>
                Sort by: 
                <select 
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value)}
                  style={{ border: 'none', background: 'transparent', outline: 'none', fontWeight: '600', cursor: 'pointer', appearance: 'none', paddingRight: '16px', color: '#101828' }}
                >
                  <option value="name-asc">Name (A-Z)</option>
                  <option value="name-desc">Name (Z-A)</option>
                  <option value="date-desc">Newest First</option>
                  <option value="date-asc">Oldest First</option>
                </select>
                <ChevronDown size={16} color="#101828" style={{ position: 'absolute', right: 0, pointerEvents: 'none' }} />
              </div>
            </div>
          </div>

          {loading ? (
            <div className="catalog-empty">
              <p>Loading your green space...</p>
            </div>
          ) : sortedPlants.length > 0 ? (
            <div className="plant-grid">
              {sortedPlants.map(plant => (
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

      {isAddOpen && <AddPlantModal onClose={() => setIsAddOpen(false)} onSuccess={handleAddSuccess} />}
      {editingPlant && <EditPlantModal plant={editingPlant} onClose={() => setEditingPlant(null)} onSuccess={handleEditSuccess} />}
      {deletingPlant && <DeleteModal plant={deletingPlant} onClose={() => setDeletingPlant(null)} onSuccess={handleDeleteSuccess} />}
    </>
  );
}