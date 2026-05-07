package edu.cit.devibar.halaman.controller;

import edu.cit.devibar.halaman.dto.WeatherResponse;
import edu.cit.devibar.halaman.service.WeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public ResponseEntity<WeatherResponse> getWeather() {
        WeatherResponse response = weatherService.getCurrentWeather("Cebu City");
        return ResponseEntity.ok(response);
    }
}