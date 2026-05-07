import { useState } from 'react';
import { X } from 'lucide-react';
import { deletePlant } from '../../api/plantApi';

export default function DeleteModal({ plant, onClose, onSuccess }) {
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');

  const handleDelete = async () => {
    setLoading(true);
    try {
      const res = await deletePlant(plant.plantId);
      if (res.data.success) {
        onSuccess(plant.plantId);
        onClose();
      } else {
        setError(res.data.error?.message || 'Failed to delete plant');
      }
    } catch (err) {
      setError('Failed to delete plant. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>

        <div className="modal-header">
          <h2>Delete Plant</h2>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        {error && (
          <div className="auth-error-banner" style={{ marginBottom: '1rem' }}>
            {error}
          </div>
        )}

        <div className="delete-modal-icon">🗑️</div>

        <div className="delete-modal-text">
          <h3>Delete "{plant.nickname}"?</h3>
          <p>
            This plant will be moved to the Recycle Bin and permanently
            deleted after 30 days.
          </p>
        </div>

        <div className="modal-footer">
          <button
            type="button"
            className="btn-cancel"
            onClick={onClose}>
            Cancel
          </button>
          <button
            type="button"
            disabled={loading}
            className="btn-delete"
            onClick={handleDelete}>
            {loading ? 'Deleting...' : 'Move to Recycle Bin'}
          </button>
        </div>

      </div>
    </div>
  );
}