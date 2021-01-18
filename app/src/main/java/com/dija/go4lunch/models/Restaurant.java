package com.dija.go4lunch.models;

public class Restaurant {
    private String rid;
    private String name;
    private String adress;
    private String phone;
    private int like;
    private String urlRestaurantWebsite;
    private String urlRestaurantPicture;


    public Restaurant() {
    }

    public Restaurant(String rid, String name, String adress, String phone, int like, String urlRestaurantWebsite, String urlRestaurantPicture) {
        this.rid = rid;
        this.name = name;
        this.adress = adress;
        this.phone = phone;
        this.like = like;
        this.urlRestaurantWebsite = urlRestaurantWebsite;
        this.urlRestaurantPicture = urlRestaurantPicture;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getUrlRestaurantWebsite() {
        return urlRestaurantWebsite;
    }

    public void setUrlRestaurantWebsite(String urlRestaurantWebsite) {
        this.urlRestaurantWebsite = urlRestaurantWebsite;
    }

    public String getUrlRestaurantPicture() {
        return urlRestaurantPicture;
    }

    public void setUrlRestaurantPicture(String urlRestaurantPicture) {
        this.urlRestaurantPicture = urlRestaurantPicture;
    }
}