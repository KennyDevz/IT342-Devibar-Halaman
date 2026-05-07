import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export default function AdminRoute({ children }) {
    const { user, loading } = useAuth();

    if (loading) return <div>Loading...</div>;

    // 1. If they aren't logged in at all, send to login
    if (!user) {
        return <Navigate to="/login" />;
    }

    // 2. If they are logged in, but lack the ADMIN role, bounce them to their dashboard
    // Using toUpperCase() just in case your backend sends 'admin' instead of 'ADMIN'
    if (user.role?.toUpperCase() !== 'ADMIN') {
        return <Navigate to="/plants" />;
    }

    // 3. If they pass both checks, render the admin page!
    return children;
}