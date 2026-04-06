import { useState } from 'react';
import { X } from 'lucide-react';
import { updatePlant } from '../api/plantApi';

export default function EditPlantModal({ plant, onClose, onSuccess }) {
  const [form, setForm] = useState({
    nickname:             plant.nickname,
    speciesName:          plant.speciesName,
    wateringFrequencyDays: String(plant.wateringFrequencyDays),
  });
  const [errors, setErrors]   = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) =>
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

  const validate = () => {
    const errs = {};
    if (!form.nickname.trim())    errs.nickname    = 'Nickname is required';
    if (!form.speciesName.trim()) errs.speciesName = 'Species name is required';
    if (!form.wateringFrequencyDays) {
      errs.wateringFrequencyDays = 'Watering frequency is required';
    } else if (Number(form.wateringFrequencyDays) < 1) {
      errs.wateringFrequencyDays = 'Must be at least 1 day';
    }
    return errs;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setErrors({});
    setLoading(true);
    try {
      const res = await updatePlant(plant.plantId, {
        nickname: form.nickname,
        speciesName: form.speciesName,
        wateringFrequencyDays: Number(form.wateringFrequencyDays),
      });
      if (res.data.success) {
        onSuccess(res.data.data.plant);
        onClose();
      } else {
        setErrors({ api: res.data.error?.message || 'Failed to update plant' });
      }
    } catch (err) {
      setErrors({ api: 'Failed to update plant. Please try again.' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>

        <div className="modal-header">
          <h2>Edit Plant</h2>
          <button className="modal-close" onClick={onClose}>
            <X size={20} />
          </button>
        </div>

        {errors.api && (
          <div className="auth-error-banner" style={{ marginBottom: '1rem' }}>
            {errors.api}
          </div>
        )}

        <form onSubmit={handleSubmit} className="modal-form">

          <div className="modal-field">
            <label className="modal-label">Plant Nickname</label>
            <input
              type="text"
              name="nickname"
              value={form.nickname}
              onChange={handleChange}
              className={`modal-input ${errors.nickname ? 'error' : ''}`}
            />
            {errors.nickname && (
              <span className="modal-field-error">{errors.nickname}</span>
            )}
          </div>

          <div className="modal-field">
            <label className="modal-label">Species Name</label>
            <input
              type="text"
              name="speciesName"
              value={form.speciesName}
              onChange={handleChange}
              className={`modal-input ${errors.speciesName ? 'error' : ''}`}
            />
            {errors.speciesName && (
              <span className="modal-field-error">{errors.speciesName}</span>
            )}
          </div>

          <div className="modal-field">
            <label className="modal-label">Watering Frequency (days)</label>
            <input
              type="number"
              name="wateringFrequencyDays"
              min="1"
              value={form.wateringFrequencyDays}
              onChange={handleChange}
              className={`modal-input ${errors.wateringFrequencyDays ? 'error' : ''}`}
            />
            {errors.wateringFrequencyDays && (
              <span className="modal-field-error">
                {errors.wateringFrequencyDays}
              </span>
            )}
          </div>

          <div className="modal-footer">
            <button
              type="button"
              className="btn-cancel"
              onClick={onClose}>
              Cancel
            </button>
            <button
              type="submit"
              disabled={loading}
              className="btn-submit">
              {loading ? 'Saving...' : 'Save Changes'}
            </button>
          </div>

        </form>
      </div>
    </div>
  );
}