import React, { useState, useEffect } from 'react';
import { Users, Leaf, Image as ImageIcon, Activity, UserPlus, Trash2 } from 'lucide-react';
import { getSystemMetrics, getRecentActivity } from '../../api/adminApi';
import '../../styles/admin-overview.css';

export default function AdminOverviewPage() {
    const [metrics, setMetrics] = useState({
        totalUsers: 0,
        totalPlants: 0,
        totalImages: 0,
        activeUsers: 0
    });

    const [loading, setLoading] = useState(true);
    const [recentActivity, setRecentActivity] = useState([]);

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                const [metricsRes, activityRes] = await Promise.all([
                    getSystemMetrics(),
                    getRecentActivity()
                ]);

                if (metricsRes.data.success) setMetrics(metricsRes.data.data);
                if (activityRes.data.success) setRecentActivity(activityRes.data.data);

            } catch (error) {
                console.error("Failed to load dashboard data", error);
            } finally {
                setLoading(false);
            }
        };
        fetchDashboardData();
    }, []);

    const getIcon = (actionType) => {
        switch(actionType) {
            case 'USER_REGISTER': return <UserPlus size={16} color="#3B82F6" />;
            // You can add more later if you log other actions!
            default: return <Activity size={16} color="#6B7280" />;
        }
    };

    const timeAgo = (dateString) => {
        if (!dateString) return 'Unknown time';
        
        const now = new Date();
        const past = new Date(dateString);
        const diffInSeconds = Math.floor((now - past) / 1000);
        
        if (diffInSeconds < 60) return 'Just now';
        if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)} mins ago`;
        if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)} hrs ago`;
        return `${Math.floor(diffInSeconds / 86400)} days ago`;
    };

    return (
        <div className="admin-overview-container">
            <div className="admin-page-header">
                <div className="title-wrapper">
                    <div className="title-marker"></div>
                    <h2>System Overview</h2>
                </div>
                <p>Monitoring platform activity and real-time system metrics.</p>
            </div>

            {/* Metrics Grid */}
            <div className="metrics-grid">
                <div className="metric-card">
                    <div className="metric-info">
                        <span className="metric-label">TOTAL USERS</span>
                        <span className="metric-value">{metrics.totalUsers}</span>
                    </div>
                    <div className="metric-icon-wrapper blue">
                        <Users size={24} />
                    </div>
                </div>

                <div className="metric-card">
                    <div className="metric-info">
                        <span className="metric-label">TOTAL PLANTS</span>
                        <span className="metric-value">{metrics.totalPlants}</span>
                    </div>
                    <div className="metric-icon-wrapper green">
                        <Leaf size={24} />
                    </div>
                </div>

                <div className="metric-card">
                    <div className="metric-info">
                        <span className="metric-label">TOTAL GALLERY IMAGES</span>
                        <span className="metric-value">{metrics.totalImages.toLocaleString()}</span>
                    </div>
                    <div className="metric-icon-wrapper purple">
                        <ImageIcon size={24} />
                    </div>
                </div>

                <div className="metric-card">
                    <div className="metric-info">
                        <span className="metric-label">DAILY ACTIVE USERS</span>
                        <span className="metric-value">{metrics.activeUsers}</span>
                    </div>
                    <div className="metric-icon-wrapper orange">
                        <Activity size={24} />
                    </div>
                </div>
            </div>

            {/* Activity Feed */}
            <div className="activity-section">
                <div className="activity-header">
                    <h3>Recent System Activity</h3>
                </div>

                <div className="activity-card">
                    {loading ? (
                        <p style={{padding: '20px', color: '#6B7280'}}>Loading activity...</p>
                    ) : recentActivity.length === 0 ? (
                        <p style={{padding: '20px', color: '#6B7280'}}>No recent activity found.</p>
                    ) : (
                        recentActivity.map((log) => (
                            <div key={log.id} className="activity-item">
                                <div className={`activity-icon-circle ${log.type.toLowerCase()}`}>
                                    {getIcon(log.type)} 
                                </div>
                                <div className="activity-details">
                                    <span className="activity-text">{log.text}</span>
                                    <span className="activity-time">{timeAgo(log.timestamp)}</span>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}