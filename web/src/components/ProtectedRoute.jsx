import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute({ children }) {
    const { user, loading } = useAuth();

    if (loading) return <div>Loading...</div>;

    if (!user) {
        return <Navigate to="/login" />;
    }
    if (user.role?.toUpperCase() === 'ADMIN') {
        return <Navigate to="/admin/overview" />;
    }

    return children;
}