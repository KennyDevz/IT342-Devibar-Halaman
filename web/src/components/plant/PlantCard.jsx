import { useState, useEffect, useRef } from 'react'; // 🌟 NEW: Imported useRef
import { MoreVertical, Pencil, Trash2, Droplets, Thermometer } from 'lucide-react';
import { usePlants } from '../../context/PlantContext';

export default function PlantCard({ plant, onEdit, onDelete }) {
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef(null); 
  const { images, loadPlantImage } = usePlants();
  const imageUrl = images[plant.plantId];

  useEffect(() => {
    loadPlantImage(plant.plantId);
  }, [plant.plantId, loadPlantImage]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      // If the click is outside the menuRef container, close the menu
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setMenuOpen(false);
      }
    };
    // Only add the event listener if the menu is actually open
    if (menuOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    // Cleanup the listener
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [menuOpen]);

  const getStatus = () => {
    if (!plant.nextDueDate) return 'watered'; 
    
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const dueDate = new Date(plant.nextDueDate);
    dueDate.setHours(0, 0, 0, 0);
    
    const diffTime = dueDate.getTime() - today.getTime();
    const diffDays = Math.round(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) return 'overdue';
    if (diffDays === 0) return 'due-today';
    return 'watered';
  };

  const getStatusLabel = () => {
    const status = getStatus();
    if (status === 'overdue')   return 'Overdue';
    if (status === 'due-today') return 'Due Today!';
    return 'Watered';
  };

  return (
    <div className="plant-card">

      {/* Image Section */}
      <div className="plant-card-image">
        {imageUrl ? (
          <img 
            src={imageUrl} 
            alt={plant.nickname} 
            style={{ width: '100%', height: '100%', objectFit: 'cover' }} 
          />
        ) : (
          <div className="plant-card-image-placeholder">🌿</div>
        )}
        <span className={`plant-status-badge ${getStatus()}`}>
          {getStatusLabel()}
        </span>
      </div>

      {/* Info Section */}
      <div className="plant-card-info">
        <div className="plant-card-name">
          <h3>{plant.nickname}</h3>
          <p>{plant.speciesName}</p>
        </div>

        {/* Three dots menu */}
        {/* 🌟 NEW 3: Attach the ref to the menu container */}
        <div className="plant-card-menu" ref={menuRef}> 
          <button
            className="plant-menu-btn"
            onClick={(e) => {
              e.stopPropagation();
              setMenuOpen(prev => !prev);
            }}>
            <MoreVertical size={20} />
          </button>

          {menuOpen && (
            <div className="plant-dropdown">
              <button
                className="plant-dropdown-item"
                onClick={(e) => {
                  e.stopPropagation();
                  setMenuOpen(false);
                  onEdit(plant);
                }}>
                <Pencil size={14} />
                Edit Plant
              </button>
              <button
                className="plant-dropdown-item delete"
                onClick={(e) => {
                  e.stopPropagation();
                  setMenuOpen(false);
                  onDelete(plant);
                }}>
                <Trash2 size={14} />
                Delete
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Footer Section */}
      <div className="plant-card-footer">
        <div className="plant-footer-item">
          <Droplets size={14} color="#43A047" />
          Every {plant.wateringFrequencyDays} days
        </div>
      </div>

    </div>
  );
}