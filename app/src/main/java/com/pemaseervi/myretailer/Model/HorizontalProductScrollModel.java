package com.pemaseervi.myretailer.Model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class HorizontalProductScrollModel {

    private String productID;
    private String productImage;
    private String productTitle;
    private String productHindiName;
    private String productDescription;
    private String productPrice,sellingPrice;
    private String catalogName,productCategory;


    public HorizontalProductScrollModel(String productID,String productImage, String productTitle,String productHindiName, String productDescription, String productPrice,String sellingPrice,String catalogName,String productCategory) {
        this.productID = productID;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productHindiName=productHindiName;
        this.sellingPrice=sellingPrice;
        this.catalogName=catalogName;
        this.productCategory=productCategory;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getProductHindiName() throws UnsupportedEncodingException {
        return URLDecoder.decode(  productHindiName, "utf-8");
    }

    public void setProductHindiName(String productHindiName) {
        this.productHindiName = productHindiName;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
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

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
