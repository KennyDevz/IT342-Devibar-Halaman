import { useEffect } from 'react';
import '../styles/Toast.css';

export default function Toast({ message, type = 'success', onClose }) {
  useEffect(() => {
    const timer = setTimeout(onClose, 5000);
    return () => clearTimeout(timer);
  }, [onClose]);

  return (
    <div className={`toast toast-${type}`}>
      <span className="toast-icon">
        {type === 'success' ? '✅' : '❌'}
      </span>
      {message}
    </div>
  );
}