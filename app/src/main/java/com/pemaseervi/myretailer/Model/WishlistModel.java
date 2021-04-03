package com.pemaseervi.myretailer.Model;

import java.util.ArrayList;

public class WishlistModel {
//    private int freeCoupens,totalRating;
    private String productImage,productTitle,productHindiTitle,product_price,sellingPrice,product_catalog,product_category;
    private boolean inStock;
    private ArrayList<String>tags;
//        rating,product_price,cutted_price,paymentMethod;

//    public WishlistModel(String productImage, int freeCoupens, int totalRating, String productTitle, String rating, String product_price, String cutted_price, String paymentMethod) {
//        this.productImage = productImage;
//        this.freeCoupens = freeCoupens;
//        this.totalRating = totalRating;
//        this.productTitle = productTitle;
//        this.rating = rating;
//        this.product_price = product_price;
//        this.cutted_price = cutted_price;
//        this.paymentMethod = paymentMethod;
//    }
//
//    public String getProductImage() {
//        return productImage;
//    }
//
//    public void setProductImage(String productImage) {
//        this.productImage = productImage;
//    }
//
//    public int getFreeCoupens() {
//        return freeCoupens;
//    }
//
//    public void setFreeCoupens(int freeCoupens) {
//        this.freeCoupens = freeCoupens;
//    }
//
//    public int getTotalRating() {
//        return totalRating;
//    }
//
//    public void setTotalRating(int totalRating) {
//        this.totalRating = totalRating;
//    }
//
//    public String getProductTitle() {
//        return productTitle;
//    }
//
//    public void setProductTitle(String productTitle) {
//        this.productTitle = productTitle;
//    }
//
//    public String getRating() {
//        return rating;
//    }
//
//    public void setRating(String rating) {
//        this.rating = rating;
//    }
//
//    public String getProduct_price() {
//        return product_price;
//    }
//
//    public void setProduct_price(String product_price) {
//        this.product_price = product_price;
//    }
//
//    public String getCutted_price() {
//        return cutted_price;
//    }
//
//    public void setCutted_price(String cutted_price) {
//        this.cutted_price = cutted_price;
//    }
//
//    public String getPaymentMethod() {
//        return paymentMethod;
//    }
//
//    public void setPaymentMethod(String paymentMethod) {
//        this.paymentMethod = paymentMethod;
//    }

    public WishlistModel(String productImage, String productTitle,String productHindiTitle, String product_price,String sellingPrice,boolean inStock,String product_catalog, String product_category) {
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productHindiTitle=productHindiTitle;
        this.product_price = product_price;
        this.sellingPrice=sellingPrice;
        this.inStock= inStock;
        this.product_catalog=product_catalog;
        this.product_category=product_category;

    }

    public String getProduct_catalog() {
        return product_catalog;
    }

    public void setProduct_catalog(String product_catalog) {
        this.product_catalog = product_catalog;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
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
