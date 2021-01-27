package com.company.admin.model;

public class Product {

    private String name;
    private String price;
    private String description;
    private boolean status;

    public Product() {
    }

    public Product(String name, String price, String description, boolean status) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.status = status;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
