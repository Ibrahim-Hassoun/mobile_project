package lb.edu.ul.project.Domain;

import java.io.Serializable;

public class Theater implements Serializable {
    private String name;
    private String address;
    private double distance;
    private String phone;
    private String openingHours;
    private double latitude;
    private double longitude;

    public Theater() {
    }

    public Theater(String name, String address, double distance, String phone, String openingHours) {
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.phone = phone;
        this.openingHours = openingHours;
    }

    public Theater(String name, String address, double distance, String phone, String openingHours, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.phone = phone;
        this.openingHours = openingHours;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
