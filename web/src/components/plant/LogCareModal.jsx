import { useState } from 'react';
import { X, Droplets, Calendar, FileText, ChevronDown } from 'lucide-react';
import {logMaintenance } from '../../api/plantApi';

export default function LogCareModal({ plant, onClose, onSuccess,defaultTask = 'WATERING', lockTask = false }) {
  const [taskType, setTaskType] = useState(defaultTask);
  const [notes, setNotes] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async () => {
    setLoading(true);
    setError('');
    
    try {
      const payload = {
        plantId: plant.plantId,
        taskType: taskType,
        notes: notes
      };
      
      const res = await logMaintenance(payload);
      
      if (res.data.success) {
        onSuccess(); 
        onClose();   
      } else {
        setError(res.data.error?.message || "Failed to log care.");
      }
    } catch (err) {
      setError("Server connection failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" style={{ maxWidth: '384px' }} onClick={(e) => e.stopPropagation()}>
        
        <div className="modal-header">
          <h2>Log Plant Care</h2>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        {error && <div className="modal-field-error" style={{marginBottom: '1rem'}}>{error}</div>}

        <div className="care-form">
          {/* Task Type */}
          <div className="care-field">
            <label className="care-label">Task Type</label>
            <div className="care-input-wrapper">
              <div className="care-input-icon"><Droplets size={18} /></div>
              <select 
                className="care-select" 
                value={taskType}
                onChange={(e) => setTaskType(e.target.value)}
                disabled={lockTask}
                style={lockTask ? { backgroundColor: '#f3f4f6', cursor: 'not-allowed', color: '#6B7280' } : {}}
              >
                <option value="WATERING">Watering</option>
                <option value="FERTILIZING">Fertilizing</option>
                <option value="PRUNING">Pruning</option>
                <option value="REPOTTING">Repotting</option>
              </select>
              {!lockTask && <div className="care-dropdown-icon"><ChevronDown size={16} /></div>}
            </div>
          </div>

          <div className="care-field">
            <label className="care-label">Date Completed</label>
            <div className="care-input-wrapper">
              <div className="care-input-icon"><Calendar size={18} /></div>
              <input 
                type="text" 
                className="care-input" 
                value="Today" 
                readOnly 
              />
            </div>
          </div>

          {/* Notes */}
          <div className="care-field">
            <label className="care-label">Notes (Optional)</label>
            <div className="care-input-wrapper" style={{ alignItems: 'flex-start' }}>
              <div className="care-input-icon" style={{ top: '14px' }}><FileText size={18} color="#99A1AF" /></div>
              <textarea 
                className="care-textarea"
                placeholder="e.g., Added liquid fertilizer..."
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
              />
            </div>
          </div>

          {/* Footer Buttons */}
          <div className="modal-footer" style={{ marginTop: '0.5rem' }}>
            <button className="btn-cancel" onClick={onClose} disabled={loading} style={{ borderRadius: '14px' }}>
              Cancel
            </button>
            <button className="btn-submit" onClick={handleSubmit} disabled={loading} style={{ borderRadius: '14px' }}>
              {loading ? 'Saving...' : 'Save Log'}
            </button>
          </div>

        </div>
      </div>
    </div>
  );
}