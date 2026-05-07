import { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { verifyOtp, resendOtp } from '../api/authApi';
import { KeyRound, RefreshCw } from 'lucide-react';
import '../styles/auth.css';

export default function VerifyOtpPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  const [searchParams] = useSearchParams();
  const email = location.state?.email || searchParams.get('email');
  

  const [otpCode, setOtpCode] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [error, setError]     = useState('');
  const [loading, setLoading] = useState(false);

  const [countdown, setCountdown] = useState(60);
  const [resendLoading, setResendLoading] = useState(false);

  useEffect(() => {
    if (countdown > 0) {
      const timer = setTimeout(() => setCountdown(countdown - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [countdown]);

  // Security Check: If they didn't come from the register page, kick them out
  useEffect(() => {
    if (!email) {
      navigate('/register', { replace: true });
    }
  }, [email, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (otpCode.length !== 6) {
      setError('Please enter a valid 6-digit code.');
      return;
    }

    setError('');
    setLoading(true);

    try {
      const res = await verifyOtp({ email, otpCode });
      
      if (res.data.success) {
         navigate('/login', { state: { toast: 'Account verified!' } });
      } else {
        setError(res.data.error?.message || 'Verification failed');
      }
    } catch (err) {
      setError(err.response?.data?.error?.message || 'Invalid or expired OTP code.');
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    if (countdown > 0) return; // Prevent clicking while timer is active
    
    setError('');
    setSuccessMsg('');
    setResendLoading(true);

    try {
      const res = await resendOtp({ email });
      if (res.data.success) {
        setSuccessMsg('A new verification code has been sent to your email.');
        setCountdown(60); // Reset the clock to 60 seconds!
        setOtpCode(''); // Clear the input box
      } else {
        setError(res.data.error?.message || 'Failed to resend code');
      }
    } catch (err) {
      setError('Failed to resend code. Please try again later.');
    } finally {
      setResendLoading(false);
    }
  };

  if (!email) return null;

  return (
    <div className="auth-page">

      {/* Left Panel */}
      <div className="auth-left">
        <div className="auth-left-content">
          <h1 className="auth-brand-name">Halaman</h1>
          <p className="auth-tagline">
            Just one more step. Verify your email to secure your digital garden.
          </p>
        </div>
      </div>

      {/* Right Panel */}
      <div className="auth-right">
        <div className="auth-card">
          
          <Link to="/login" className="auth-back">
            ← Back to login
          </Link>

          <div className="auth-header">
            <h2 className="auth-heading">Check your inbox</h2>
            <p className="auth-subheading">
              We sent a 6-digit verification code to <strong>{email}</strong>
            </p>
          </div>

          {error && <div className="auth-error-banner">{error}</div>}
          {successMsg && <div className="auth-error-banner auth-success-banner">{successMsg}</div>}

          <form onSubmit={  handleSubmit} className="auth-form">
            <div className="auth-field">
              <label className="auth-label">Verification Code</label>
              <div className="auth-input-wrapper">
                <KeyRound size={18} className="auth-input-icon" />
                <input
                  type="text"
                  name="otpCode"
                  placeholder="123456"
                  maxLength="6"
                  value={otpCode}
                  onChange={(e) => {
                    // Only allow numbers to be typed
                    const value = e.target.value.replace(/\D/g, '');
                    setOtpCode(value);
                  }}
                  className={`auth-input ${error ? 'error' : ''}`}
                  style={{ letterSpacing: '0.25em', fontSize: '1.1rem', fontWeight: 'bold' }}
                />
              </div>
            </div>

            <button 
              type="submit" 
              disabled={loading || otpCode.length < 6} 
              className="auth-btn">
              {loading ? 'Verifying...' : 'Verify Account'}
            </button>
          </form>

          <div className="auth-resend-container">
            Didn't receive the code?{' '}
            {countdown > 0 ? (
              <span className="auth-resend-timer">Resend in {countdown}s</span>
            ) : (
              <button 
                onClick={handleResend} 
                disabled={resendLoading}
                className="auth-resend-btn"
              >
                {resendLoading ? 'Sending...' : <><RefreshCw size={14} /> Resend now</>}
              </button>
            )}
          </div>

        </div>
      </div>
    </div>
  );
}