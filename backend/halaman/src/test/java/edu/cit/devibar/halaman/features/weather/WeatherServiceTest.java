package edu.cit.devibar.halaman.features.weather;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void getCurrentWeather_ShouldReturnFallback_WhenCityIsInvalid() {
        // Since WeatherService currently creates RestTemplate internally, 
        // we can test the fallback mechanism by providing an invalid/empty city name
        // which will cause the API call to fail or parsing to fail.
        
        WeatherResponse response = weatherService.getCurrentWeather("   ");

        // The fallback mechanism should return default values
        assertNotNull(response);
        assertEquals(0.0, response.getTemperature());
        assertEquals(0, response.getHumidity());
        assertTrue(response.getIsDay());
        assertEquals(0, response.getWeatherCode());
        assertEquals("Location Unavailable", response.getLocation());
    }
}
