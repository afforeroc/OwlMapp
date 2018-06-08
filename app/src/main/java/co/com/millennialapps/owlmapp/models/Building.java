package co.com.millennialapps.owlmapp.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;

/**
 * Created by Erick Velasco on 12/5/2018.
 */

public class Building {

    private String id;
    private String name;
    private String number;
    private String description;
    private double latitude;
    private double longitude;
    private String phone;
    private String email;
    private String webPage;

    @Exclude
    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebPage() {
        return webPage;
    }

    public void setWebPage(String webPage) {
        this.webPage = webPage;
    }
}
