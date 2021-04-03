package com.pemaseervi.myretailer.Model;

import java.util.ArrayList;

public class SearchModel {
    //    private int freeCoupens,totalRating;
    private String productImage,productTitle,productHindiTitle,product_price;
    private boolean inStock;
    private ArrayList<String> tags;

    public SearchModel(String productImage, String productTitle,String productHindiTitle, String product_price,boolean inStock) {
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productHindiTitle=productHindiTitle;
        this.product_price = product_price;
        this.inStock= inStock;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getProductImage() {
        return productImage;
    }

    public String getProductHindiTitle() {
        return productHindiTitle;
    }

    public void setProductHindiTitle(String productHindiTitle) {
        this.productHindiTitle = productHindiTitle;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProduct_price() {
        return product_price;
    }

    public void setProduct_price(String product_price) {
        this.product_price = product_price;
    }
}
