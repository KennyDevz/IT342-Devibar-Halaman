package edu.cit.devibar.halaman.service;

import edu.cit.devibar.halaman.dto.WeatherResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeatherService {

    public WeatherResponse getCurrentWeather() {
        String url = "https://api.open-meteo.com/v1/forecast?latitude=10.3157&longitude=123.8854&current=temperature_2m,relative_humidity_2m,is_day,weather_code";

        RestTemplate restTemplate = new RestTemplate();
        WeatherResponse weather = new WeatherResponse();

        try {
            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode current = root.path("current");

            weather.setTemperature(current.path("temperature_2m").asDouble());
            weather.setHumidity(current.path("relative_humidity_2m").asInt());

            weather.setIsDay(current.path("is_day").asInt() == 1);
            weather.setWeatherCode(current.path("weather_code").asInt());
        } catch (Exception e) {
            // Failsafe in case Open-Meteo is down so your dashboard doesn't crash
            weather.setTemperature(0.0);
            weather.setHumidity(0);
            weather.setIsDay(true);
            weather.setWeatherCode(0);
        }

        return weather;
    }
}