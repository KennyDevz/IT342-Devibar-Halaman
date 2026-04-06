import { ArrowLeft, Droplets, Calendar, Plus, Scissors, Sparkles, RefreshCw, Archive } from 'lucide-react';
import { useState, useEffect } from 'react';
import { getPlantImages, getPlantSchedule, getPlantMaintenanceLogs } from '../api/plantApi';
import LogCareModal from '../components/LogCareModal';

export default function PlantDetailsPage({ plant, onBack }) {
  const [imageUrl, setImageUrl] = useState(null);
  const [schedule, setSchedule] = useState(null);
  const [logs, setLogs] = useState([]); // NEW: State for history
  const [isLogModalOpen, setIsLogModalOpen] = useState(false);
  const [refreshTrigger, setRefreshTrigger] = useState(0);

  useEffect(() => {
    const loadDetails = async () => {
      try {
        // 1. Fetch Images
        const imgRes = await getPlantImages(plant.plantId);
        if (imgRes.data.success && imgRes.data.data.length > 0) {
          setImageUrl(imgRes.data.data[0].fileUrl);
        }

        // 2. Fetch Schedule
        const schedRes = await getPlantSchedule(plant.plantId);
        if (schedRes.data.success) {
          setSchedule(schedRes.data.data);
        }

        // 3. Fetch Maintenance History
        try {
            const logsRes = await getPlantMaintenanceLogs(plant.plantId);
            if (logsRes.data.success) {
            setLogs(logsRes.data.data);
            }
        } catch (err) {
            console.log("Could not load maintenance logs.");
        }

      } catch (err) {
        console.error("Failed to load details", err);
      }
    };
    loadDetails();
  }, [plant.plantId, refreshTrigger]); // FIXED: Added refreshTrigger so it reloads after logging!

  // Calculate days until next water
  const getDaysUntilWater = () => {
    if (!schedule || !schedule.nextDueDate) return "Unknown";
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const dueDate = new Date(schedule.nextDueDate);
    dueDate.setHours(0, 0, 0, 0);
    
    const diffDays = Math.ceil((dueDate - today) / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) return "Overdue";
    if (diffDays === 0) return "Today";
    return `In ${diffDays} Days`;
  };

  // Helper function to format the dates for the timeline
  const formatLogDate = (dateString) => {
    const date = new Date(dateString);
    const today = new Date();
    
    if (date.toDateString() === today.toDateString()) {
      return `TODAY, ${date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
    }
    return date.toLocaleDateString('en-US', { month: 'short', day: '2-digit', year: 'numeric' }).toUpperCase();
  };

  // Helper function to get the right icon based on task
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
              <div className="dot-separator"></div>
              <div style={{background: '#F0FDF4', color: '#2E7D32', padding: '4px 12px', borderRadius: '20px', fontSize: '12px', fontWeight: '700', textTransform: 'uppercase', display: 'flex', alignItems: 'center', gap: '6px'}}>
                <div style={{width: '6px', height: '6px', background: '#2E7D32', borderRadius: '50%', opacity: 0.5}}></div>
                Healthy
              </div>
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
          <div className="stat-value">New Plant</div>
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
                <div className="timeline-item" key={log.maintenanceId}>
                  <div className="timeline-line-container">
                    <div className={`timeline-icon ${isFirst ? 'active' : ''}`}>
                      {getTaskIcon(log.taskType, isFirst)}
                    </div>
                    {!isLast && <div className="timeline-line"></div>}
                  </div>
                  <div className="timeline-content">
                    <div className="timeline-header">
                      <h4>{log.taskType.charAt(0) + log.taskType.slice(1).toLowerCase()}</h4>
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
          }}
        />
      )}
    </div>
  );
}