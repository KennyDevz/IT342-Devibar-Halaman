import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './core/AuthContext';
import LoginPage from './features/auth/pages/LoginPage';
import RegisterPage from './features/auth/pages/RegisterPage';
import MyPlantsPage from './features/plants/pages/MyPlantsPage';
import DashboardLayout from './components/DashboardLayout';
import CareSchedulePage from './features/schedule/pages/CareSchedulePage';
import GrowthGalleryPage from './features/plants/pages/GrowthGalleryPage'; 
import RecycleBinPage from './features/plants/pages/RecycleBinPage';
import SettingsPage from './features/settings/pages/SettingsPage';
import AdminLayout from './features/admin/components/AdminLayout';
import AdminRoute from './core/AdminRoute';
import ProtectedRoute from './core/ProtectedRoute';
import AdminOverviewPage from './features/admin/pages/AdminOverviewPage';
import AdminUsersPage from './features/admin/pages/UserManagementPage';
import ImageModerationPage from './features/admin/pages/ImageModerationPage';
import { PlantProvider } from './features/plants/context/PlantContext';
import VerifyOtpPage from './features/auth/pages/VerifyOtpPage';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <PlantProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/verify" element={<VerifyOtpPage />} />
            
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

            <Route path="/settings" element={
              <ProtectedRoute>
                <DashboardLayout>
                  <SettingsPage />
                </DashboardLayout>
              </ProtectedRoute>
            } />

            <Route path="/admin/overview" element={
                <AdminRoute>
                    <AdminLayout>
                        <AdminOverviewPage />
                    </AdminLayout> 
                </AdminRoute>
            } />

            <Route path="/admin/users" element={
                <AdminRoute>
                    <AdminLayout>
                        <AdminUsersPage />
                    </AdminLayout>
                </AdminRoute>
            } />

            <Route path="/admin/gallery" element={
                <AdminRoute>
                    <AdminLayout>
                        <ImageModerationPage />
                    </AdminLayout>
                </AdminRoute>
            } />

            {/* Default redirect to /plants instead of /login if they hit an unknown URL */}
            <Route path="*" element={<Navigate to="/plants" />} />
          </Routes>
        </PlantProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}