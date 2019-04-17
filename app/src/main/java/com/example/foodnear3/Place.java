package com.example.foodnear3;

public class Place {

    private int code;
    private String name;
    private String image_link;
    private String address;

    public Place(int code, String name, String image_link, String address) {
        this.code = code;
        this.name = name;
        this.image_link = image_link;
        this.address = address;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage_link() {
        return image_link;
    }

    public void setImage_link(String image_link) {
        this.image_link = image_link;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
