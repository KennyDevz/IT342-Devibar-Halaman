package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.dto.WeatherResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeatherService {

    // Now it accepts a dynamic city name!
    public WeatherResponse getCurrentWeather(String cityName) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();
        WeatherResponse weather = new WeatherResponse();

        try {
            String formattedCity = cityName.replace(" ", "+");
            String geoUrl = "https://geocoding-api.open-meteo.com/v1/search?name=" + formattedCity + "&count=1&language=en&format=json";

            String geoResponse = restTemplate.getForObject(geoUrl, String.class);
            JsonNode geoRoot = mapper.readTree(geoResponse);

            // Check if the city was actually found
            if (!geoRoot.has("results") || geoRoot.path("results").isEmpty()) {
                throw new RuntimeException("City not found");
            }

            JsonNode cityData = geoRoot.path("results").get(0);
            double lat = cityData.path("latitude").asDouble();
            double lon = cityData.path("longitude").asDouble();
            String officialName = cityData.path("name").asText();
            String country = cityData.path("country").asText();

            // Set our dynamic location string!
            weather.setLocation(officialName + ", " + country);

            String weatherUrl = "https://api.open-meteo.com/v1/forecast?latitude=" + lat + "&longitude=" + lon + "&current=temperature_2m,relative_humidity_2m,is_day,weather_code";

            String weatherResponse = restTemplate.getForObject(weatherUrl, String.class);
            JsonNode weatherRoot = mapper.readTree(weatherResponse);
            JsonNode current = weatherRoot.path("current");

            weather.setTemperature(current.path("temperature_2m").asDouble());
            weather.setHumidity(current.path("relative_humidity_2m").asInt());
            weather.setIsDay(current.path("is_day").asInt() == 1);
            weather.setWeatherCode(current.path("weather_code").asInt());

        } catch (Exception e) {
            // Failsafe: If anything breaks (typo, no internet), return safe defaults
            weather.setTemperature(0.0);
            weather.setHumidity(0);
            weather.setIsDay(true);
            weather.setWeatherCode(0);
            weather.setLocation("Location Unavailable");
        }

        return weather;
    }
}