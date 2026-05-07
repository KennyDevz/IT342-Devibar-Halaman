import { useState } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { loginUser } from '../api/authApi';
import { Mail, Lock, Eye, EyeOff } from 'lucide-react';
import Toast from '../components/Toast';
import '../styles/auth.css';
import { useGoogleLogin } from '@react-oauth/google';
import { googleAuth } from '../api/authApi';

export default function LoginPage() {
  const navigate  = useNavigate();
  const location  = useLocation();
  const { login } = useAuth();

  const [form, setForm]         = useState({ email: '', password: '' });
  const [error, setError]       = useState('');
  const [loading, setLoading]   = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [toast, setToast]       = useState(location.state?.toast || null);

  const handleChange = (e) =>
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await loginUser(form);
      if (res.data.success) {
        const userRole = res.data.data.user?.role?.toUpperCase() || 'USER';
        login(
          {
            accessToken:  res.data.data.accessToken,
            refreshToken: res.data.data.refreshToken,
          },
          res.data.data.user
        );
        if (userRole === 'ADMIN') {
            navigate('/admin/overview', { state: { toast: 'Admin login successful! 👑' } });
        } else {
            navigate('/plants', { state: { toast: 'Login successful! Welcome back 🌿' } });
        }
      } else {
        setError(res.data.error?.message || 'Login failed');
      }
    } catch (err) {
      if (!err.response) {
        setError('Cannot connect to the server. Please check your connection or try again later.');
      } else {
        setError(err.response?.data?.error?.message || 'Invalid email or password.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = useGoogleLogin({
    onSuccess: async (tokenResponse) => {
      setError('');
      setLoading(true); 
      
      try {
        const res = await googleAuth(tokenResponse.access_token);
        if (res.data.success) {
          const userRole = res.data.data.user?.role?.toUpperCase() || 'USER';
          login(
            {
              accessToken:  res.data.data.accessToken,
              refreshToken: res.data.data.refreshToken,
            },
            res.data.data.user
          );
          if (userRole === 'ADMIN') {
              navigate('/admin/overview', { state: { toast: 'Admin login successful! 👑' } });
          } else {
              navigate('/plants', { state: { toast: 'Login successful! Welcome back 🌿' } });
          }
        } else {
          setError(res.data.error?.message || 'Google login failed');
        }
      } catch (err) {
        if (!err.response) {
          setError('Cannot connect to the server. Please try again later.');
        } else {
          setError(err.response?.data?.error?.message || 'Google login failed. Please try again.');
        }
      } finally {
        setLoading(false);
      }
    },
    onError: () => {
      setError('Google login failed. Please try again.');
      setLoading(false);
    }
  });

  return (
    <div className="auth-page">

      {toast && (
        <Toast
          message={toast}
          type="success"
          onClose={() => setToast(null)}
        />
      )}

      {/* Left Panel */}
      <div className="auth-left">
        <div className="auth-left-content">
          <h1 className="auth-brand-name">Halaman</h1>
          <p className="auth-tagline">
            Connect with your plants, track their growth, and cultivate
            your own urban jungle. Your sanctuary, one leaf at a time.
          </p>
        </div>
      </div>

      {/* Right Panel */}
      <div className="auth-right">
        <div className="auth-card">

          <div className="auth-header">
            <h2 className="auth-heading">Welcome back to Halaman</h2>
            <p className="auth-subheading">Log in to manage your green space</p>
          </div>

          {error && <div className="auth-error-banner">{error}</div>}

          <form onSubmit={handleSubmit} className="auth-form">

            <div className="auth-field">
              <label className="auth-label">Email Address</label>
              <div className="auth-input-wrapper">
                <Mail size={18} className="auth-input-icon" />
                <input
                  type="email"
                  name="email"
                  placeholder="name@example.com"
                  value={form.email}
                  onChange={handleChange}
                  required
                  className="auth-input"
                />
              </div>
            </div>

            <div className="auth-field">
              <label className="auth-label">Password</label>
              <div className="auth-input-wrapper">
                <Lock size={18} className="auth-input-icon" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  name="password"
                  placeholder="••••••••"
                  value={form.password}
                  onChange={handleChange}
                  required
                  className="auth-input auth-input-password"
                />
                <button
                  type="button"
                  className="auth-input-toggle"
                  onClick={() => setShowPassword(prev => !prev)}>
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            <div className="auth-options">
              <label className="auth-remember">
                <input type="checkbox" />
                <span>Remember me</span>
              </label>
              <Link to="/forgot-password" className="auth-forgot">
                Forgot password?
              </Link>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="auth-btn">
              {loading ? 'Logging in...' : 'Log In'}
            </button>

            <div className="auth-divider">
              <span>Or continue with</span>
            </div>

            <button type="button" className="auth-google-btn" onClick={() => handleGoogleLogin()} disabled={loading} style={{ opacity: loading ? 0.6 : 1, cursor: loading ? 'not-allowed' : 'pointer' }}>
              <img
                src="https://www.svgrepo.com/show/475656/google-color.svg"
                alt="Google"
                width={20}
                height={20}
                style={{ filter: loading ? 'grayscale(100%)' : 'none' }}
              />
              {loading ? 'Please wait...' : 'Sign in with Google'}
            </button>

          </form>

          <p className="auth-switch-text">
            Don't have an account?{' '}
            <Link to="/register" className="auth-link">Sign up</Link>
          </p>

        </div>
      </div>

    </div>
  );
}