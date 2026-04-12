import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import MyPlantsPage from './pages/MyPlantsPage';
import DashboardLayout from './components/DashboardLayout';
import CareSchedulePage from './pages/CareSchedulePage';
import GrowthGalleryPage from './pages/GrowthGalleryPage'; 
import RecycleBinPage from './pages/RecycleBinPage';
import { PlantProvider } from './context/PlantContext';

function ProtectedRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <div>Loading...</div>;
  return user ? children : <Navigate to="/login" />;
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <PlantProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            
            {/* Protect and Wrap My Plants Page */}
            <Route path="/plants" element={
              <ProtectedRoute>
                <DashboardLayout>
                  <MyPlantsPage />
                </DashboardLayout>
              </ProtectedRoute>
            } />

            {/* Protect and Wrap Schedule Page */}
            <Route path="/schedule" element={
              <ProtectedRoute>
                <DashboardLayout>
                  <CareSchedulePage />
                </DashboardLayout>
              </ProtectedRoute>
            } />

            {/* Protect and Wrap the NEW Growth Gallery Page */}
            <Route path="/gallery" element={
              <ProtectedRoute>
                <DashboardLayout>
                  <GrowthGalleryPage />
                </DashboardLayout>
              </ProtectedRoute>
            } />

            {/*Recycle Bin Page */}
            <Route path="/recycle-bin" element={
              <ProtectedRoute>
                <DashboardLayout>
                  <RecycleBinPage />
                </DashboardLayout>
              </ProtectedRoute>
            } />

            {/* Default redirect to /plants instead of /login if they hit an unknown URL */}
            <Route path="*" element={<Navigate to="/plants" />} />
          </Routes>
        </PlantProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}