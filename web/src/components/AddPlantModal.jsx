import { useState, useRef } from 'react';
import { X, Plus, UploadCloud } from 'lucide-react';
import { createPlant, uploadPlantImage } from '../api/plantApi';

export default function AddPlantModal({ onClose, onSuccess }) {
  const fileInputRef = useRef(null);
  
  const [form, setForm] = useState({
    nickname: '',
    speciesName: '',
    wateringFrequencyDays: '',
  });
  
  const [imageFile, setImageFile]= useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  
  const [errors, setErrors]= useState({});
  const [loading, setLoading]= useState(false);
  const [loadingText, setLoadingText]= useState('Add Plant');

  const handleChange = (e) =>
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

  // Handle Image Selection
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setImageFile(file);
      setImagePreview(URL.createObjectURL(file));
    }
  };

  const clearImage = (e) => {
    e.stopPropagation(); // Prevent triggering the click on the upload zone
    setImageFile(null);
    setImagePreview(null);
    if (fileInputRef.current) fileInputRef.current.value = '';
  };

  const validate = () => {
    const errs = {};
    if (!form.nickname.trim())errs.nickname= 'Nickname is required';
    if (!form.speciesName.trim())errs.speciesName= 'Species name is required';
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
    setLoadingText('Saving details...');

    try {
      // 1. Create the plant record first
      const res = await createPlant({
        nickname: form.nickname,
        speciesName: form.speciesName,
        wateringFrequencyDays: Number(form.wateringFrequencyDays),
      });

      if (res.data.success) {
        const newPlant = res.data.data.plant;

        // 2. If an image was selected, upload it using the new plant's ID
        if (imageFile) {
          setLoadingText('Uploading photo...');
          const formData = new FormData();
          formData.append('file', imageFile); // The backend expects a multipart 'file' part
          
          try {
            await uploadPlantImage(newPlant.plantId, formData);
            // Optionally, you could attach the returned image URL to the newPlant object here
          } catch (imgErr) {
            console.error("Image upload failed, but plant was created.", imgErr);
            // We don't block success if only the image fails, 
            // but you could add a toast notification here.
          }
        }

        onSuccess(newPlant);
        onClose();
      } else {
        setErrors({ api: res.data.error?.message || 'Failed to add plant' });
      }
    } catch (err) {
      setErrors({ api: 'Failed to add plant. Please try again.' });
    } finally {
      setLoading(false);
      setLoadingText('Add Plant');
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal" onClick={e => e.stopPropagation()}>

        <div className="modal-header">
          <h2>Add a New Plant</h2>
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
              placeholder="e.g. 'Office Fern'"
              value={form.nickname}
              onChange={handleChange}
              className={`modal-input ${errors.nickname ? 'error' : ''}`}
            />
            {errors.nickname && <span className="modal-field-error">{errors.nickname}</span>}
          </div>

          <div className="modal-field">
            <label className="modal-label">Species Name</label>
            <input
              type="text"
              name="speciesName"
              placeholder="e.g. 'Snake Plant'"
              value={form.speciesName}
              onChange={handleChange}
              className={`modal-input ${errors.speciesName ? 'error' : ''}`}
            />
            {errors.speciesName && <span className="modal-field-error">{errors.speciesName}</span>}
          </div>

          <div className="modal-field">
            <label className="modal-label">Watering Schedule (Days)</label>
            <input
              type="number"
              name="wateringFrequencyDays"
              placeholder="7"
              min="1"
              value={form.wateringFrequencyDays}
              onChange={handleChange}
              className={`modal-input ${errors.wateringFrequencyDays ? 'error' : ''}`}
            />
            {errors.wateringFrequencyDays && (
              <span className="modal-field-error">{errors.wateringFrequencyDays}</span>
            )}
          </div>

          {/* NEW: Image Upload Zone */}
          <div className="modal-field">
            <label className="modal-label">Upload initial photo (Optional)</label>
            <div className="upload-zone" onClick={() => fileInputRef.current.click()}>
              {imagePreview ? (
                <>
                  <img src={imagePreview} alt="Preview" className="upload-preview" />
                  <button type="button" className="upload-remove-btn" onClick={clearImage}>
                    <X size={16} />
                  </button>
                </>
              ) : (
                <div className="upload-placeholder">
                  <UploadCloud size={28} color="#99A1AF" />
                  <span>Click to browse</span>
                </div>
              )}
              <input
                type="file"
                ref={fileInputRef}
                accept="image/*"
                style={{ display: 'none' }}
                onChange={handleImageChange}
              />
            </div>
          </div>

          <div className="modal-footer">
            <button type="button" className="btn-cancel" onClick={onClose} disabled={loading}>
              Cancel
            </button>
            <button type="submit" disabled={loading} className="btn-submit">
              {!loading && <Plus size={16} />}
              {loadingText}
            </button>
          </div>

        </form>
      </div>
    </div>
  );
}