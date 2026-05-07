package edu.cit.devibar.halaman.dto;

public class WeatherResponse {
    private Double temperature;
    private Integer humidity;
    private Boolean isDay;
    private Integer weatherCode;
    private String location;

    // Getters and Setters
    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }
    public Boolean getIsDay() { return isDay; }
    public Integer getWeatherCode() { return weatherCode; }
    public String getLocation() { return location; }

    public Integer getHumidity() { return humidity; }
    public void setHumidity(Integer humidity) { this.humidity = humidity; }
    public void setIsDay(Boolean isDay) { this.isDay = isDay; }
    public void setWeatherCode(Integer weatherCode) { this.weatherCode = weatherCode; }
    public void setLocation(String location) { this.location = location; }
}