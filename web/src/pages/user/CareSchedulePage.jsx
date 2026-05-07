import React, { useState } from 'react';
import { usePlants } from '../../context/PlantContext'; 
import Toast from '../../components/Toast';
import LogCareModal from '../../components/plant/LogCareModal';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import '../../styles/schedule.css';

export default function CareSchedulePage() {
    // --- 1. GLOBAL & LOCAL STATE ---
    const { plants, loading, fetchPlants } = usePlants();
    
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [weekOffset, setWeekOffset] = useState(0);
    const [activeLogItem, setActiveLogItem] = useState(null);
    const [toast, setToast] = useState({ show: false, message: '', type: '' });

    // --- 2. CORE DATE NORMALIZATION ---
    // We must define 'today' at the very top so all functions can use it safely!
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const todayTime = today.getTime();

    const normalizedSelectedDate = new Date(selectedDate);
    normalizedSelectedDate.setHours(0, 0, 0, 0);
    const selectedTime = normalizedSelectedDate.getTime();
    
    const isTodaySelected = selectedTime === todayTime;

    // --- 3. CALENDAR GENERATION ---
    const calendarDays = Array.from({ length: 7 }).map((_, i) => {
        const date = new Date();
        date.setDate(date.getDate() + (weekOffset * 7));
        
        // Find the Sunday of that week
        const day = date.getDay();
        const diff = date.getDate() - day;
        const sunday = new Date(date.setDate(diff));
        
        const currentDay = new Date(sunday);
        currentDay.setDate(sunday.getDate() + i);
        
        currentDay.setHours(0, 0, 0, 0); 
        return currentDay;
    });

    const monthYearLabel = calendarDays[0].toLocaleDateString('en-US', { 
        month: 'long', 
        year: 'numeric' 
    });

    // --- 4. HELPER FUNCTIONS ---
    const showToast = (message, type) => {
        setToast({ show: true, message, type });
        setTimeout(() => setToast({ show: false, message: '', type: '' }), 3000);
    };

    const getTaskStatusLabel = (dateString) => {
        if (!dateString) return null;
        const dueDate = new Date(dateString);
        dueDate.setHours(0, 0, 0, 0);

        if (dueDate.getTime() < todayTime) return 'overdue';
        if (dueDate.getTime() === todayTime) return 'due-today';
        return null; 
    };

    // Failsafe: Use (plants || []) to prevent crashes if context is still loading
    const safePlants = plants || [];

    const checkHasTasks = (dateToCheck) => {
        const checkTime = dateToCheck.getTime();
        const isCheckingToday = checkTime === todayTime;

        return safePlants.some(plant => {
            if (!plant.nextDueDate) return false;
            
            const dueDate = new Date(plant.nextDueDate);
            dueDate.setHours(0, 0, 0, 0);
            const dueTime = dueDate.getTime();

            // If we are checking the "Today" button, look for today AND overdue
            if (isCheckingToday) {
                return dueTime <= todayTime; 
            }
            // For future buttons, only look for exact date matches
            return dueTime === checkTime; 
        });
    };

    // --- 5. DATA FILTERING ---
    const filteredTasks = safePlants.filter(plant => {
        if (!plant.nextDueDate) return false;
        
        const dueDate = new Date(plant.nextDueDate);
        dueDate.setHours(0, 0, 0, 0);
        const dueTime = dueDate.getTime();
        
        // If the user clicked "Today", show them today's tasks + overdue tasks
        if (isTodaySelected) {
            return dueTime <= todayTime;
        }
        
        // Otherwise, only show the tasks for the specific future date they clicked
        return dueTime === selectedTime;
    });

    // --- 6. RENDER UI ---
    return (
        <div className="schedule-container">
            <header className="schedule-header">
                <div className="header-left">
                    <h1 className="schedule-title">My Care Schedule</h1>
                    <p className="schedule-subtitle">Keep track of your plant's wellness routine.</p>
                </div>

                <div className="schedule-controls">
                    <button 
                        className="nav-btn" 
                        onClick={() => setWeekOffset(prev => prev - 1)}
                        disabled={weekOffset === 0}
                    >
                        <ChevronLeft size={20} />
                    </button>
                    
                    <span className="month-display">{monthYearLabel}</span>
                    
                    <button className="nav-btn" onClick={() => setWeekOffset(prev => prev + 1)}>
                        <ChevronRight size={20} />
                    </button>
                </div>
            </header>

            <div className="calendar-strip">
                {calendarDays.map((date, index) => {
                    const isSelected = date.getTime() === selectedTime; 
                    const hasTask = checkHasTasks(date);
                    const isThisDateToday = date.getTime() === todayTime;
                    
                    return (
                        <button
                            key={index}
                            onClick={() => setSelectedDate(date)}
                            className={`calendar-btn ${isSelected ? 'selected' : ''} ${isThisDateToday ? 'is-today' : ''}`}
                        >
                            {hasTask && <span className="task-indicator-dot"></span>}
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
                    <h2 className="tasks-title">{isTodaySelected ? "Tasks for Today" : "Scheduled Tasks"}</h2>
                    <span className="tasks-badge">
                        {filteredTasks.length} ITEMS REMAINING
                    </span>
                </div>

                {loading ? (
                    <p className="tasks-loading">Loading schedule...</p>
                ) : filteredTasks.length === 0 ? (
                    <div className="tasks-empty">
                        <p>{isTodaySelected ? "No tasks scheduled for today. You're all caught up!" : "No tasks scheduled for this date."}</p>
                    </div>
                ) : (
                    <div className="task-list">
                        {filteredTasks.map(plant => {
                            const status = getTaskStatusLabel(plant.nextDueDate);
                            
                            return (
                                <div key={plant.plantId} className="task-card">
                                    <div className="task-info-wrapper">
                                        <div className="task-icon">
                                            {plant.imageUrl ? (
                                                <img src={plant.imageUrl} alt={plant.nickname} />
                                            ) : (
                                                <span>🪴</span>
                                            )}
                                        </div>
                                        
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
                                    
                                    {/* We usually only let users mark tasks as done if it's today or overdue */}
                                    {isTodaySelected && (
                                        <button 
                                            onClick={() => setActiveLogItem({ plant: plant, type: 'WATERING' })}
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

            {activeLogItem && (
                <LogCareModal 
                    plant={activeLogItem.plant} 
                    defaultTask={activeLogItem.type} 
                    lockTask={true}                  
                    onClose={() => setActiveLogItem(null)} 
                    onSuccess={() => {
                        setActiveLogItem(null);
                        showToast("Care logged successfully!", "success");
                        fetchPlants();
                    }}
                />
            )}

            {toast.show && <Toast message={toast.message} type={toast.type} />}
        </div>
    );
}