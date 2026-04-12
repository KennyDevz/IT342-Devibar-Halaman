import React from 'react';
import Sidebar from './Sidebar';
import '../styles/plant.css'; 

export default function DashboardLayout({ children }){
    return (
        <div className="dashboard">
            <Sidebar />
            
            <main className="dashboard-main">
                {children}
            </main>
        </div>
    );
};

