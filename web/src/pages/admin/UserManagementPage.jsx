import React, { useState, useEffect } from 'react';
import { Search, MoreVertical, Edit2, ShieldAlert, CheckCircle } from 'lucide-react';
import { getAllUsers, toggleUserStatus } from '../../api/adminApi';
import '../../styles/user-management.css';

export default function UserManagementPage() {
    const [users, setUsers] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const res = await getAllUsers();
                if (res.data.success) {
                    // Map the backend data to match our UI format
                    const formattedUsers = res.data.data.map(u => ({
                        id: u.id || u.userId || u.uuid,
                        firstName: u.firstName,
                        lastName: u.lastName,
                        email: u.email,
                        role: u.role,
                        status: u.status || 'ACTIVE', // Fallback just in case
                        // Format the Java LocalDateTime to a clean string
                        joined: new Date(u.createdAt).toLocaleDateString()
                    }));
                    setUsers(formattedUsers);
                }
            } catch (error) {
                console.error("Failed to fetch users", error);
            } finally {
                setLoading(false);
            }
        };
        fetchUsers();
    }, []);

    // Filter users based on search input
    const filteredUsers = users.filter(user => 
        user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.firstName.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.lastName.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const handleToggleStatus = async (userId) => {
        try {
            setUsers(users.map(user => {
                if (user.id === userId) {
                    return { ...user, status: user.status === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE' };
                }
                return user;
            }));

            await toggleUserStatus(userId);
            
        } catch (error) {
            console.error("Failed to toggle status", error);
            alert("Failed to update user status. Please try again.");
            // If it failed, you could optionally fetchUsers() again to reset the UI to the truth
        }
    };

    return (
        <div className="admin-users-container">
            <div className="admin-page-header">
                <div className="title-wrapper">
                    <div className="title-marker"></div>
                    <h2>User Management</h2>
                </div>
                <p>View, edit, and manage system access for all registered accounts.</p>
            </div>

            <div className="table-controls">
                <div className="search-bar-wrapper">
                    <Search className="search-icon" size={18} />
                    <input 
                        type="text" 
                        placeholder="Search users by name or email..." 
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-input"
                    />
                </div>
            </div>

            <div className="table-card">
                <table className="users-table">
                    <thead>
                        <tr>
                            <th>USER</th>
                            <th>ROLE</th>
                            <th>STATUS</th>
                            <th>DATE JOINED</th>
                            <th className="text-right">ACTIONS</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredUsers.map((user) => (
                            <tr key={user.id}>
                                <td>
                                    <div className="user-cell">
                                        <div className="user-avatar">
                                            {user.firstName.charAt(0)}{user.lastName.charAt(0)}
                                        </div>
                                        <div className="user-info">
                                            <span className="user-name">{user.firstName} {user.lastName}</span>
                                            <span className="user-email">{user.email}</span>
                                        </div>
                                    </div>
                                </td>
                                <td>
                                    <span className={`role-badge ${user.role.toLowerCase()}`}>
                                        {user.role}
                                    </span>
                                </td>
                                <td>
                                    <span className={`status-badge ${user.status.toLowerCase()}`}>
                                        {user.status === 'ACTIVE' ? <CheckCircle size={14} /> : <ShieldAlert size={14} />}
                                        {user.status}
                                    </span>
                                </td>
                                <td>
                                    <span className="date-cell">{user.joined}</span>
                                </td>
                                <td className="actions-cell">
                                    <div className="action-buttons">
                                        <button className="action-btn edit" title="Edit Role">
                                            <Edit2 size={16} />
                                        </button>
                                        <button 
                                            className={`action-btn ${user.status === 'ACTIVE' ? 'suspend' : 'activate'}`} 
                                            title={user.status === 'ACTIVE' ? 'Suspend User' : 'Activate User'}
                                            onClick={() => handleToggleStatus(user.id)}
                                        >
                                            {user.status === 'ACTIVE' ? <ShieldAlert size={16} /> : <CheckCircle size={16} />}
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
                
                {filteredUsers.length === 0 && (
                    <div className="empty-state">
                        <p>No users found matching "{searchTerm}"</p>
                    </div>
                )}
            </div>
        </div>
    );
}