import { render, screen } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import '@testing-library/jest-dom';

// Updated relative paths pointing OUT of the tests folder
import MyPlantsPage from '../pages/MyPlantsPage';
import { useAuth } from '../../../core/AuthContext';
import { usePlants } from '../context/PlantContext';
import { getCurrentWeather } from '../api/plantApi';

// 1. Mock the Router Hooks
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useLocation: () => ({ state: null }), // Mock location state for the Toast
  };
});

// 2. Mock the Contexts and API
vi.mock('../../../core/AuthContext', () => ({
  useAuth: vi.fn(),
}));

vi.mock('../context/PlantContext', () => ({
  usePlants: vi.fn(),
}));

vi.mock('../api/plantApi', () => ({
  getCurrentWeather: vi.fn(),
}));

describe('TC-PLANT-002: My Plants Dashboard UI Tests', () => {

  beforeEach(() => {
    vi.clearAllMocks();
    
    // Default Auth Mock
    useAuth.mockReturnValue({
      user: { firstName: 'Kenneth' }
    });

    // Default Weather Mock
    getCurrentWeather.mockResolvedValue({
      data: { temperature: 28, humidity: 65, isDay: true, weatherCode: 0, location: 'Cebu City' }
    });
  });

  const renderComponent = () => {
    return render(
      <BrowserRouter>
        <MyPlantsPage />
      </BrowserRouter>
    );
  };

  it('renders the welcome header and Add Plant button', () => {
    // Arrange: Mock empty plant state
    usePlants.mockReturnValue({
      plants: [],
      loading: false,
    });
    
    renderComponent();
    
    // Assert
    expect(screen.getByText(/Hello, Kenneth!/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /Add New Plant/i })).toBeInTheDocument();
    expect(screen.getByText(/My Plant Catalog/i)).toBeInTheDocument();
  });

  it('displays the empty state when the user has no plants', () => {
    usePlants.mockReturnValue({
      plants: [],
      images: {},
      loadPlantImage: vi.fn(),
      loading: false,
    });

    renderComponent();

    expect(screen.getByText(/No plants found/i)).toBeInTheDocument();
    expect(screen.getByText(/Start your collection by adding your first plant!/i)).toBeInTheDocument();
  });

  it('successfully renders the plant grid when data is provided from Context', () => {
    // Arrange: Mock populated plant state
    usePlants.mockReturnValue({
      plants: [
        {
          plantId: '1',
          nickname: 'Monstera Deliciosa',
          speciesName: 'Monstera',
          nextDueDate: new Date(Date.now() + 86400000).toISOString(), // Tomorrow
          createdAt: new Date().toISOString()
        }
      ],
      images: {},
      loadPlantImage: vi.fn(),
      loading: false,
    });

    renderComponent();

    // Assert: Check if the plant nickname made it to the screen
    expect(screen.getByText('Monstera Deliciosa')).toBeInTheDocument();
    
    // The empty state should NOT be there
    expect(screen.queryByText(/No plants found/i)).not.toBeInTheDocument();
  });
});