package co.com.owlmapp.models;

/**
 * Created by Erick Velasco on 12/5/2018.
 */

public class Building {

    private String id;
    private String name;
    private String number;
    private String description;
    private String urlImage;

    public Building(String description, String name){
        this.name = name.substring(3);
        this.description = description;
        this.number = name.substring(0, 3);
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

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
