import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import MyPlantsPage from './pages/user/MyPlantsPage';
import DashboardLayout from './components/DashboardLayout';
import CareSchedulePage from './pages/user/CareSchedulePage';
import GrowthGalleryPage from './pages/user/GrowthGalleryPage'; 
import RecycleBinPage from './pages/user/RecycleBinPage';
import SettingsPage from './pages/user/SettingsPage';
import AdminLayout from './components/admin/AdminLayout';
import AdminRoute from './components/admin/AdminRoute';
import ProtectedRoute from './components/ProtectedRoute';   
import AdminOverviewPage from './pages/admin/AdminOverviewPage';
import AdminUsersPage from './pages/admin/UserManagementPage';
import ImageModerationPage from './pages/admin/ImageModerationPage';
import { PlantProvider } from './context/PlantContext';

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