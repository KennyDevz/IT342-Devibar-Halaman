import { useState, useEffect } from 'react';
import { useNavigate, useLocation, Link, useSearchParams } from 'react-router-dom';
import { KeyRound, Eye, EyeOff, RefreshCw } from 'lucide-react';
import { forgotPassword, resetPassword } from '../api/authApi';
import '../../../styles/auth.css';

export default function ResetPasswordPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();

  // Accept email from router state (coming from ForgotPasswordPage) or URL param (from email link)
  const emailFromState  = location.state?.email;
  const emailFromQuery  = searchParams.get('email');
  const [email, setEmail] = useState(emailFromState || emailFromQuery || '');

  const [otpCode, setOtpCode]         = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading]         = useState(false);
  const [error, setError]             = useState('');
  const [successMsg, setSuccessMsg]   = useState('');

  // Resend OTP cooldown
  const [countdown, setCountdown]       = useState(0);
  const [resendLoading, setResendLoading] = useState(false);

  // Security Check: If they didn't come from the forgot password page or email link, kick them out
  useEffect(() => {
    if (!email) {
      navigate('/forgot-password', { replace: true });
    }
  }, [email, navigate]);

  useEffect(() => {
    if (countdown > 0) {
      const t = setTimeout(() => setCountdown(c => c - 1), 1000);
      return () => clearTimeout(t);
    }
  }, [countdown]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (otpCode.length !== 6) {
      setError('Please enter a valid 6-digit code.');
      return;
    }
    if (newPassword.length < 6) {
      setError('New password must be at least 6 characters.');
      return;
    }

    setLoading(true);
    try {
      const res = await resetPassword({ email, otpCode, newPassword });
      if (res.data.success) {
        navigate('/login', { state: { toast: '🔑 Password reset! You can now log in.' } });
      } else {
        setError(res.data.error?.message || 'Reset failed. Please try again.');
      }
    } catch (err) {
      setError(err.response?.data?.error?.message || 'Invalid or expired code.');
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    if (countdown > 0 || !email) return;
    setError('');
    setSuccessMsg('');
    setResendLoading(true);
    try {
      await forgotPassword({ email });
      setSuccessMsg('A new reset code has been sent to your email.');
      setOtpCode('');
      setCountdown(60);
    } catch {
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
            Almost there. Enter the code from your email and choose a strong new password.
          </p>
        </div>
      </div>

      {/* Right Panel */}
      <div className="auth-right">
        <div className="auth-card">

          <Link to="/forgot-password" className="auth-back">← Back</Link>

          <div className="auth-header">
            <h2 className="auth-heading">Reset your password</h2>
            <p className="auth-subheading">
              We sent a 6-digit code to <strong>{email}</strong>.
            </p>
          </div>

          {error      && <div className="auth-error-banner">{error}</div>}
          {successMsg && <div className="auth-error-banner auth-success-banner">{successMsg}</div>}

          <form onSubmit={handleSubmit} className="auth-form">



            {/* OTP Code */}
            <div className="auth-field">
              <label className="auth-label">Reset Code</label>
              <div className="auth-input-wrapper">
                <KeyRound size={18} className="auth-input-icon" />
                <input
                  type="text"
                  placeholder="123456"
                  maxLength="6"
                  value={otpCode}
                  onChange={(e) => setOtpCode(e.target.value.replace(/\D/g, ''))}
                  className={`auth-input auth-input-otp ${error ? 'error' : ''}`}
                />
              </div>
            </div>

            {/* New Password */}
            <div className="auth-field">
              <label className="auth-label">New Password</label>
              <div className="auth-input-wrapper">
                <KeyRound size={18} className="auth-input-icon" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  placeholder="At least 6 characters"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  required
                  className="auth-input auth-input-password"
                />
                <button
                  type="button"
                  className="auth-input-toggle"
                  onClick={() => setShowPassword(p => !p)}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading || otpCode.length < 6 || newPassword.length < 6}
              className="auth-btn"
            >
              {loading ? 'Resetting...' : 'Reset Password'}
            </button>
          </form>

          {/* Resend */}
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
