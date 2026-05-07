import { ArrowLeft, Droplets, Calendar, Plus, Scissors, Sparkles, RefreshCw, Archive } from 'lucide-react';
import { useState, useEffect } from 'react';
import { getPlantMaintenanceLogs } from '../../api/plantApi';
import LogCareModal from '../../components/plant/LogCareModal';
import { usePlants } from '../../context/PlantContext'; 

export default function PlantDetailsPage({ plant, onBack }) {
  const { fetchPlants, images, loadPlantImage } = usePlants(); 
  
  const imageUrl = images[plant.plantId];
  const [logs, setLogs] = useState([]); 
  const [isLogModalOpen, setIsLogModalOpen] = useState(false);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  useEffect(() => {
    loadPlantImage(plant.plantId);
    
    const loadDetails = async () => {
      try {
        const logsRes = await getPlantMaintenanceLogs(plant.plantId);
        
        if (logsRes.data.success) {
            const logsArray = logsRes.data.data?.logs || logsRes.data.data || [];
            
            if (Array.isArray(logsArray)) {
                setLogs(logsArray);
            } else {
                setLogs([]);
            }
        }
      } catch (err) {
        console.log("Could not load maintenance logs.", err);
      }
    };
    
    loadDetails();
  }, [plant.plantId, refreshTrigger, loadPlantImage]); 

  // --- USE THE PLANT PROP INSTEAD OF A SEPARATE API CALL ---
  const getDaysUntilWater = () => {
    if (!plant.nextDueDate) return "Unknown";
    
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const dueDate = new Date(plant.nextDueDate);
    dueDate.setHours(0, 0, 0, 0);
    
    const diffDays = Math.round((dueDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) return "Overdue";
    if (diffDays === 0) return "Today";
    return `In ${diffDays} Days`;
  };

  const calculatePlantAge = (createdAt) => {
    if (!createdAt) return "Unknown age";
    
    const startDate = new Date(createdAt);
    const today = new Date();
    
    // Calculate the difference in time
    const diffTime = Math.abs(today - startDate);
    const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays === 0) return "Added today";
    if (diffDays === 1) return "1 day old";
    if (diffDays < 30) return `${diffDays} days old`;
    
    const diffMonths = Math.floor(diffDays / 30);
    if (diffMonths === 1) return "1 month old";
    if (diffMonths < 12) return `${diffMonths} months old`;
    
    const diffYears = Math.floor(diffDays / 365);
    return diffYears === 1 ? "1 year old" : `${diffYears} years old`;
  };

  const formatLogDate = (dateString) => {
    const date = new Date(dateString);
    const today = new Date();
    
    if (date.toDateString() === today.toDateString()) {
      return `TODAY, ${date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
    }
    return date.toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' }).toUpperCase();
  };

  const getTaskIcon = (taskType, isActive) => {
    const color = isActive ? "white" : "#99A1AF";
    switch(taskType) {
      case 'WATERING': return <Droplets size={18} color={color} />;
      case 'FERTILIZING': return <Sparkles size={18} color={color} />;
      case 'PRUNING': return <Scissors size={18} color={color} />;
      case 'REPOTTING': return <Archive size={18} color={color} />;
      default: return <RefreshCw size={18} color={color} />;
    }
  };

  return (
    <div className="details-page-container">
      
      {/* Top Nav */}
      <div className="details-nav">
        <button className="btn-back" onClick={onBack}>
          <div className="icon-circle"><ArrowLeft size={16} /></div>
          Back to Catalog
        </button>
      </div>

      {/* Hero Card */}
      <div className="hero-card">
        <div className="hero-info">
          <div className="hero-image-container">
            {imageUrl ? (
               <img src={imageUrl} alt={plant.nickname} className="hero-image" />
            ) : (
               <div style={{width: '100%', height: '100%', background: '#E5E7EB', borderRadius: '18px', display: 'flex', alignItems: 'center', justifyContent: 'center'}}>🌿</div>
            )}
          </div>
          <div className="hero-text">
            <h1>{plant.nickname}</h1>
            <div className="hero-badges">
              <span className="species-text">{plant.speciesName}</span>
            </div>
          </div>
        </div>
        <button className="btn-log-care" onClick={() => setIsLogModalOpen(true)}>
            <Plus size={18} /> Log Care Action
        </button>
      </div>

      {/* Stats Container */}
      <div className="stats-container">
        <div className="stat-card">
          <div className="stat-icon water"><Droplets size={24} /></div>
          <div className="stat-label">Next Water</div>
          <div className="stat-value">{getDaysUntilWater()}</div>
        </div>
        <div className="stat-card">
          <div className="stat-icon age"><Calendar size={24} /></div>
          <div className="stat-label">Age</div>
          <div className="stat-value">{calculatePlantAge(plant.createdAt)}</div>
        </div>
      </div>

      {/* Maintenance History */}
      <div className="history-section">
        <h2>Maintenance History</h2>
        
        <div className="timeline">
          {logs.length === 0 ? (
            <p style={{ color: '#99A1AF', fontStyle: 'italic' }}>No care actions logged yet. Time to get your hands dirty!</p>
          ) : (
            logs.map((log, index) => {
              const isFirst = index === 0;
              const isLast = index === logs.length - 1;
              
              return (
                <div className="timeline-item" key={log.maintenanceId || index}>
                  <div className="timeline-line-container">
                    <div className={`timeline-icon ${isFirst ? 'active' : ''}`}>
                      {getTaskIcon(log.taskType, isFirst)}
                    </div>
                    {!isLast && <div className="timeline-line"></div>}
                  </div>
                  <div className="timeline-content">
                    <div className="timeline-header">
                      {/* Safety check: ensure taskType exists before calling .charAt() */}
                      <h4>{log.taskType ? log.taskType.charAt(0) + log.taskType.slice(1).toLowerCase() : 'Task'}</h4>
                      <span className="timeline-date">{formatLogDate(log.completedAt)}</span>
                    </div>
                    {log.notes && <p className="timeline-notes">{log.notes}</p>}
                  </div>
                </div>
              );
            })
          )}
        </div>
      </div>

      {isLogModalOpen && (
        <LogCareModal 
          plant={plant} 
          onClose={() => setIsLogModalOpen(false)}
          onSuccess={() => {
            setRefreshTrigger(prev => prev + 1); 
            fetchPlants(); 
          }}
        />
      )}
    </div>
  );
}