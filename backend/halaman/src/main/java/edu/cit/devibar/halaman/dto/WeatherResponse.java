package edu.cit.devibar.halaman.dto;

public class WeatherResponse {
    private Double temperature;
    private Integer humidity;
    private Boolean isDay;
    private Integer weatherCode;

    // Getters and Setters
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Boolean getIsDay() { return isDay; }
    public Integer getWeatherCode() { return weatherCode; }

    public Integer getHumidity() { return humidity; }
    public void setHumidity(Integer humidity) { this.humidity = humidity; }
    public void setIsDay(Boolean isDay) { this.isDay = isDay; }
    public void setWeatherCode(Integer weatherCode) { this.weatherCode = weatherCode; }
}