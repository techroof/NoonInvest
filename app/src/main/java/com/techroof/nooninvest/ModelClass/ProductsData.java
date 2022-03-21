package com.techroof.nooninvest.ModelClass;

public class ProductsData {

    private String id;
    private String category;
    private String image;
    private String name;

    public ProductsData() {
    }

    public ProductsData(String id, String category, String image, String name) {
        this.id = id;
        this.category = category;
        this.image = image;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
