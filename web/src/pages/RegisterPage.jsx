import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { registerUser } from '../api/authApi';
import { User, Mail, Lock, Eye, EyeOff } from 'lucide-react';
import Toast from '../components/Toast';
import '../styles/auth.css';
import { useGoogleLogin } from '@react-oauth/google';
import { googleAuth } from '../api/authApi';

export default function RegisterPage() {
  const navigate  = useNavigate();

  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [errors, setErrors]                           = useState({});
  const [apiError, setApiError]                       = useState('');
  const [loading, setLoading]                         = useState(false);
  const [agreed, setAgreed]                           = useState(false);
  const [showPassword, setShowPassword]               = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const handleChange = (e) =>
    setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

  const validate = () => {
    const errs = {};
    if (!form.firstName.trim()) errs.firstName = 'First name is required';
    if (!form.lastName.trim())  errs.lastName  = 'Last name is required';
    if (!form.email.trim())     errs.email     = 'Email is required';
    else if (!/\S+@\S+\.\S+/.test(form.email)) errs.email = 'Invalid email format';
    if (!form.password)         errs.password  = 'Password is required';
    else if (form.password.length < 8) errs.password = 'Must be at least 8 characters';
    if (form.password !== form.confirmPassword) errs.confirmPassword = 'Passwords do not match';
    if (!agreed) errs.agreed = 'You must agree to the Terms of Service';
    return errs;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    if (Object.keys(errs).length) { setErrors(errs); return; }
    setErrors({});
    setApiError('');
    setLoading(true);
    try {
      const res = await registerUser({
        firstName: form.firstName,
        lastName:  form.lastName,
        email:     form.email,
        password:  form.password,
      });
      if (res.data.success) {
        navigate('/login', { state: { toast: 'Account created successfully! Please log in.' } });
      } else {
        setApiError(res.data.error?.message || 'Registration failed');
      }
    } catch (err) {
      setApiError(err.response?.data?.error?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = useGoogleLogin({
  onSuccess: async (tokenResponse) => {
    try {
      const res = await googleAuth(tokenResponse.access_token);
      if (res.data.success) {
        navigate('/login', { state: { toast: 'Account created successfully! Please log in.' } });
      } else {
        setApiError(res.data.error?.message || 'Google sign up failed');
      }
    } catch (err) {
      setApiError('Google sign up failed. Please try again.');
    }
  },
  onError: () => {
    setApiError('Google sign up failed. Please try again.');
  }
});

  return (
    <div className="auth-page">

      {/* Left Panel */}
      <div className="auth-left">
        <div className="auth-left-content">
          <h1 className="auth-brand-name">Halaman</h1>
          <p className="auth-tagline">
            Start your botanical journey today. Create your digital garden
            and let your passion for plants bloom with Halaman's community.
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
            <h2 className="auth-heading">Join Halaman today</h2>
            <p className="auth-subheading">
              Create your account to start your green journey
            </p>
          </div>

          {apiError && <div className="auth-error-banner">{apiError}</div>}

          <form onSubmit={handleSubmit} className="auth-form">

            <div className="auth-row">
              <div className="auth-field">
                <label className="auth-label">First Name</label>
                <div className="auth-input-wrapper">
                  <User size={16} className="auth-input-icon" />
                  <input
                    type="text"
                    name="firstName"
                    placeholder="Jane"
                    value={form.firstName}
                    onChange={handleChange}
                    className={`auth-input ${errors.firstName ? 'error' : ''}`}
                  />
                </div>
                {errors.firstName && <span className="auth-field-error">{errors.firstName}</span>}
              </div>

              <div className="auth-field">
                <label className="auth-label">Last Name</label>
                <div className="auth-input-wrapper">
                  <input
                    type="text"
                    name="lastName"
                    placeholder="Doe"
                    value={form.lastName}
                    onChange={handleChange}
                    className={`auth-input auth-input-no-icon ${errors.lastName ? 'error' : ''}`}
                  />
                </div>
                {errors.lastName && <span className="auth-field-error">{errors.lastName}</span>}
              </div>
            </div>

            <div className="auth-field">
              <label className="auth-label">Email Address</label>
              <div className="auth-input-wrapper">
                <Mail size={18} className="auth-input-icon" />
                <input
                  type="email"
                  name="email"
                  placeholder="jane@example.com"
                  value={form.email}
                  onChange={handleChange}
                  className={`auth-input ${errors.email ? 'error' : ''}`}
                />
              </div>
              {errors.email && <span className="auth-field-error">{errors.email}</span>}
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
                  className={`auth-input auth-input-password ${errors.password ? 'error' : ''}`}
                />
                <button
                  type="button"
                  className="auth-input-toggle"
                  onClick={() => setShowPassword(prev => !prev)}>
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
              {errors.password && <span className="auth-field-error">{errors.password}</span>}
            </div>

            <div className="auth-field">
              <label className="auth-label">Confirm Password</label>
              <div className="auth-input-wrapper">
                <Lock size={18} className="auth-input-icon" />
                <input
                  type={showConfirmPassword ? 'text' : 'password'}
                  name="confirmPassword"
                  placeholder="••••••••"
                  value={form.confirmPassword}
                  onChange={handleChange}
                  className={`auth-input auth-input-password ${errors.confirmPassword ? 'error' : ''}`}
                />
                <button
                  type="button"
                  className="auth-input-toggle"
                  onClick={() => setShowConfirmPassword(prev => !prev)}>
                  {showConfirmPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
              {errors.confirmPassword && <span className="auth-field-error">{errors.confirmPassword}</span>}
            </div>

            <div className="auth-terms">
              <input
                type="checkbox"
                id="agree"
                checked={agreed}
                onChange={(e) => setAgreed(e.target.checked)}
              />
              <label htmlFor="agree">
                I agree to Halaman's{' '}
                <Link to="/terms" className="auth-link">Terms of Service</Link>
                {' '}and{' '}
                <Link to="/privacy" className="auth-link">Privacy Policy</Link>.
              </label>
            </div>
            {errors.agreed && <span className="auth-field-error">{errors.agreed}</span>}

            <button
              type="submit"
              disabled={loading}
              className="auth-btn">
              {loading ? 'Creating account...' : 'Create Account'}
            </button>

            <button type="button" className="auth-google-btn" onClick={() => handleGoogleLogin()}>
              <img
                src="https://www.svgrepo.com/show/475656/google-color.svg"
                alt="Google"
                width={20}
                height={20}
              />
              Sign up with Google
            </button>

          </form>

          <p className="auth-switch-text">
            Already have an account?{' '}
            <Link to="/login" className="auth-link">Log in</Link>
          </p>

        </div>
      </div>

    </div>
  );
}
