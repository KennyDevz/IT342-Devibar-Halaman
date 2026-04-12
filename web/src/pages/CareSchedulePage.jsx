import React, { useState } from 'react';
import { usePlants } from '../context/PlantContext'; 
import Toast from '../components/Toast';
import '../styles/schedule.css';

export default function CareSchedulePage() {
    // --- GLOBAL STATE ---
    const { plants, loading, fetchPlants } = usePlants();
    
    // --- LOCAL UI STATE ---
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [toast, setToast] = useState({ show: false, message: '', type: '' });

    const calendarDays = Array.from({ length: 7 }).map((_, i) => {
        const date = new Date();
        date.setDate(date.getDate() + i);
        return date;
    });

    const handleLogCare = async (plantId, taskType) => {
        try {
            showToast(`${taskType} logged successfully!`, 'success');
            fetchPlants();
        } catch (error) {
            showToast('Failed to log task', 'error');
        }
    };

    const showToast = (message, type) => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: '' }), 3000);
    };

    // --- DATE NORMALIZATION LOGIC ---
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const normalizedSelectedDate = new Date(selectedDate);
    normalizedSelectedDate.setHours(0, 0, 0, 0);

    const isTodaySelected = normalizedSelectedDate.getTime() === today.getTime();

    const getTaskStatusLabel = (dateString) => {
        if (!dateString) return null;
        const dueDate = new Date(dateString);
        dueDate.setHours(0, 0, 0, 0);

        if (dueDate.getTime() < today.getTime()) return 'overdue';
        if (dueDate.getTime() === today.getTime()) return 'due-today';
        return null; 
    };

    const filteredTasks = plants.filter(plant => {
        if (!plant.nextDueDate) return false;
        
        const dueDate = new Date(plant.nextDueDate);
        dueDate.setHours(0, 0, 0, 0);
        
        if (isTodaySelected) {
            return dueDate.getTime() <= today.getTime();
        }
        
        return dueDate.getTime() === normalizedSelectedDate.getTime();
    });

    return (
        <div className="schedule-container">
            <header className="schedule-header">
                <h1 className="schedule-title">My Care Schedule</h1>
                <p className="schedule-subtitle">Keep track of your plant's wellness routine.</p>
            </header>

            <div className="calendar-strip">
                {calendarDays.map((date, index) => {
                    const isSelected = date.toDateString() === selectedDate.toDateString();
                    return (
                        <button
                            key={index}
                            onClick={() => setSelectedDate(date)}
                            className={`calendar-btn ${isSelected ? 'selected' : ''}`}
                        >
                            <span className="calendar-day-label">
                                {date.toLocaleDateString('en-US', { weekday: 'short' })}
                            </span>
                            <span className="calendar-date-label">
                                {date.getDate()}
                            </span>
                        </button>
                    );
                })}
            </div>

            <div className="tasks-section">
                <div className="tasks-header">
                    <h2 className="tasks-title">{isTodaySelected ? "Tasks for Today" : "Upcoming Tasks"}</h2>
                    <span className="tasks-badge">
                        {filteredTasks.length} ITEMS REMAINING
                    </span>
                </div>

                {loading ? (
                    <p className="tasks-loading">Loading schedule...</p>
                ) : filteredTasks.length === 0 ? (
                    <div className="tasks-empty">
                        <p>{isTodaySelected ? "No tasks scheduled for today. You're all caught up!" : "No tasks scheduled for this future date."}</p>
                    </div>
                ) : (
                    <div className="task-list">
                        {filteredTasks.map(plant => {
                            const status = getTaskStatusLabel(plant.nextDueDate);
                            
                            return (
                                <div key={plant.plantId} className="task-card">
                                    <div className="task-info-wrapper">
                                        <div className="task-icon">🪴</div>
                                        <div className="task-details">
                                            
                                            <div className="task-tags-wrapper">
                                                <span className="task-tag">WATERING</span>
                                                {status === 'overdue' && <span className="task-tag overdue">OVERDUE</span>}
                                                {status === 'due-today' && <span className="task-tag due-today">DUE TODAY</span>}
                                            </div>

                                            <h3 className="task-name">Watering — {plant.nickname}</h3>
                                            <p className="task-species">Species: {plant.speciesName}</p>
                                        </div>
                                    </div>
                                    
                                    {isTodaySelected && (
                                        <button 
                                            onClick={() => handleLogCare(plant.plantId, 'WATERING')}
                                            className="task-action-btn"
                                            title="Mark as Done"
                                        >
                                            ✓
                                        </button>
                                    )}
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>

            {toast.show && <Toast message={toast.message} type={toast.type} />}
        </div>
    );
}