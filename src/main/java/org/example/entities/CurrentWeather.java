package org.example.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
public class CurrentWeather {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double temp;

    @Column(name = "wind_speed")
    private double windSpeed;

    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;

    public CurrentWeather(long id, double temp, double windSpeed, Location location) {
        this.id = id;
        this.temp = temp;
        this.windSpeed = windSpeed;
        this.location = location;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

