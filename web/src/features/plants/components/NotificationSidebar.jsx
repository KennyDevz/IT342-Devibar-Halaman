import { X, Droplets, AlertTriangle, BellOff, Clock } from 'lucide-react';

export default function NotificationSidebar({ isOpen, onClose, attentionPlants, onSelectPlant }) {
  if (!isOpen) return null;

  const formatTime = (dueDate) => {
    if (!dueDate) return '';
    const today = new Date();
    today.setHours(0, 0, 0, 0); 
    const date = new Date(dueDate);
    date.setHours(0, 0, 0, 0);
    
    const diffTime = date.getTime() - today.getTime();
    const diffDays = Math.round(diffTime / (1000 * 60 * 60 * 24));
    
    if (diffDays < 0) {
      const absDays = Math.abs(diffDays);
      return `${absDays} day${absDays > 1 ? 's' : ''} ago`;
    }
    if (diffDays === 0) {
      return 'Today';
    }
    return '';
  };

  return (
    <div className="drawer-overlay" onClick={onClose}>
      <div className="drawer" onClick={e => e.stopPropagation()}>
        <div className="drawer-header">
          <h2>Notifications</h2>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>
        
        <div className="drawer-content">
          {attentionPlants.length === 0 ? (
            <div className="notification-empty">
              <BellOff size={48} />
              <p>You have no new notifications.<br/>All your plants are happy!</p>
            </div>
          ) : (
            attentionPlants.map(plant => {
              const isOverdue = plant.status === 'Overdue';
              const timeText = formatTime(plant.nextDueDate);

              return (
                <div 
                  key={plant.plantId} 
                  className="notification-item"
                  onClick={() => {
                    onSelectPlant(plant);
                    onClose();
                  }}
                >
                  <div className="notification-image-wrapper">
                    {plant.imageUrl ? (
                      <img src={plant.imageUrl} alt={plant.nickname} className="notification-image" />
                    ) : (
                      <div className="notification-image-placeholder">🪴</div>
                    )}
                    <div className={`notification-badge ${isOverdue ? 'overdue' : 'due-today'}`}>
                      {isOverdue ? <AlertTriangle size={12} strokeWidth={3} /> : <Droplets size={12} strokeWidth={3} />}
                    </div>
                  </div>
                  
                  <div className="notification-content-right">
                    <div className="notification-header-row">
                      <span className={`notification-status-text ${isOverdue ? 'overdue' : 'due-today'}`}>
                        {isOverdue ? 'OVERDUE WATERING' : 'TIME TO WATER'}
                      </span>
                      <span className="notification-time">
                        <Clock size={12} /> {timeText}
                      </span>
                    </div>
                    <h4 className="notification-nickname">{plant.nickname}</h4>
                    <p className="notification-desc">Needs attention for optimal growth.</p>
                  </div>
                </div>
              );
            })
          )}
        </div>
      </div>
    </div>
  );
}
