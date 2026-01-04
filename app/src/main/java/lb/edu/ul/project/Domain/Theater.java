package lb.edu.ul.project.Domain;

import java.io.Serializable;

public class Theater implements Serializable {
    private String name;
    private String address;
    private double distance;
    private String phone;
    private String openingHours;

    public Theater() {
    }

    public Theater(String name, String address, double distance, String phone, String openingHours) {
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.phone = phone;
        this.openingHours = openingHours;
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
}
