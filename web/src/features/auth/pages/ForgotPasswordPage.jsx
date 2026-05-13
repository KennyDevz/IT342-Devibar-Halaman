import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Mail } from 'lucide-react';
import { forgotPassword } from '../api/authApi';
import '../../../styles/auth.css';

export default function ForgotPasswordPage() {
  const [email, setEmail]       = useState('');
  const [loading, setLoading]   = useState(false);
  const [sent, setSent]         = useState(false);
  const [error, setError]       = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await forgotPassword({ email });
      // Always show success — backend never reveals if email exists
      setSent(true);
    } catch {
      setError('Something went wrong. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      {/* Left Panel */}
      <div className="auth-left">
        <div className="auth-left-content">
          <h1 className="auth-brand-name">Halaman</h1>
          <p className="auth-tagline">
            Don't worry, it happens to the best of us. We'll help you get back into your garden.
          </p>
        </div>
      </div>

      {/* Right Panel */}
      <div className="auth-right">
        <div className="auth-card">

          <Link to="/login" className="auth-back">← Back to login</Link>

          <div className="auth-header">
            <h2 className="auth-heading">Forgot your password?</h2>
            <p className="auth-subheading">
              Enter your email and we'll send you a reset code.
            </p>
          </div>

          {error && <div className="auth-error-banner">{error}</div>}

          {sent ? (
            <div className="auth-error-banner auth-success-banner">
              ✅ If that email is registered, a reset code is on its way. Check your inbox (and spam folder).
              <br /><br />
              <Link to="/reset-password" state={{ email }} style={{ color: '#15803d', fontWeight: 600 }}>
                Enter reset code →
              </Link>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="auth-form">
              <div className="auth-field">
                <label className="auth-label">Email Address</label>
                <div className="auth-input-wrapper">
                  <Mail size={18} className="auth-input-icon" />
                  <input
                    type="email"
                    placeholder="name@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    className="auth-input"
                  />
                </div>
              </div>

              <button type="submit" disabled={loading} className="auth-btn">
                {loading ? 'Sending...' : 'Send Reset Code'}
              </button>
            </form>
          )}

          <p className="auth-switch-text">
            Remember your password?{' '}
            <Link to="/login" className="auth-link">Log in</Link>
          </p>

        </div>
      </div>
    </div>
  );
}
