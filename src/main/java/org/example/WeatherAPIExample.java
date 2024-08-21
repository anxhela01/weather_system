package org.example;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.example.entities.CurrentWeather;
import org.example.entities.Forecast;
import org.example.entities.Location;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WeatherAPIExample {
    public static void main(String[] args) {
        // Scanner to take user input
        Scanner scanner = new Scanner(System.in);

        // Prompt for city
        System.out.print("Enter the name of the city: ");
        String city = scanner.nextLine();

        // API Key and URL
        String apiKey = "a80907b4c4c047d4a09160316240508&q";
        String url = "https://api.weatherapi.com/v1/forecast.json?key=" + apiKey + "&q=" + city + "&days=3&aqi=no&alerts=no";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == 200) {
                String json = EntityUtils.toString(response.getEntity());
                JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

                // Extract location details
                JsonObject locationJson = jsonObject.getAsJsonObject("location");
                String region = locationJson.get("region").getAsString();
                String country = locationJson.get("country").getAsString();
                double lat = locationJson.get("lat").getAsDouble();
                double lon = locationJson.get("lon").getAsDouble();

                // Extract current weather details
                JsonObject currentJson = jsonObject.getAsJsonObject("current");
                double tempC = currentJson.get("temp_c").getAsDouble();
                double windSpeed = currentJson.get("wind_kph").getAsDouble();

                // Create Location entity
                Location location = new Location();
                location.setName(city);
                location.setRegion(region);
                location.setCountry(country);
                location.setLat(lat);
                location.setLog(lon);

                // Create CurrentWeather entity
                // In your WeatherAPIExample.java
                CurrentWeather currentWeather = new CurrentWeather(0, tempC, windSpeed, location);

                currentWeather.setTemp(tempC);
                currentWeather.setWindSpeed(windSpeed);
                currentWeather.setLocation(location);

                // Set current weather in Location entity
                location.setCurrentWeather(currentWeather);

                // Extract forecast details
                JsonArray forecastArray = jsonObject.getAsJsonObject("forecast").getAsJsonArray("forecastday");
                List<Forecast> forecasts = new ArrayList<>();

                for (int i = 0; i < forecastArray.size(); i++) {
                    JsonObject dayJson = forecastArray.get(i).getAsJsonObject().getAsJsonObject("day");
                    LocalDate date = LocalDate.parse(forecastArray.get(i).getAsJsonObject().get("date").getAsString());

                    double minTemp = dayJson.get("mintemp_c").getAsDouble();
                    double maxTemp = dayJson.get("maxtemp_c").getAsDouble();
                    double avgTemp = dayJson.get("avgtemp_c").getAsDouble();

                    Forecast forecast = new Forecast(0, date, minTemp, maxTemp, avgTemp, location);

                    forecasts.add(forecast);
                }

                // Set forecasts in Location entity
                location.setForecastList(forecasts);

                // Save to the database
                try (Session session = new Configuration().configure().buildSessionFactory().openSession()) {
                    Transaction transaction = session.beginTransaction();

                    session.save(location);
                    session.save(currentWeather);
                    for (Forecast forecast : forecasts) {
                        session.save(forecast);
                    }

                    transaction.commit();
                }

                System.out.println("Data saved successfully to the database.");
            } else {
                System.out.println("Error: " + response.getStatusLine().getStatusCode());
            }
            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
