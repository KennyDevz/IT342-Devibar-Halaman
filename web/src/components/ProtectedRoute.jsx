import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children }) {
    const { user, loading } = useAuth();

    const hasToken = !!localStorage.getItem('accessToken');

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!user && !hasToken) {
        return <Navigate to="/login" replace />;
    }

    if (user?.role?.toUpperCase() === 'ADMIN') {
        return <Navigate to="/admin/overview" replace />;
    }

    return children;
}