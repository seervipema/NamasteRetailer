package com.pemaseervi.myretailer.Model;

public class CategoryWiseProductModel {

    private String productTitle,productHindiTitle,product_catalog,product_category;
    private int productPrice,productQuantityForCart;
    private String productImageResource,sellingPrice;


    public CategoryWiseProductModel(String productTitle,String productHindiTitle, int productPrice, int productQuantityForCart, String productImageResource,String sellingPrice, String product_catalog, String product_category) {
        this.productTitle = productTitle;
        this.productHindiTitle=productHindiTitle;
        this.productPrice = productPrice;
        this.productQuantityForCart = productQuantityForCart;
        this.productImageResource = productImageResource;
        this.sellingPrice=sellingPrice;
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

    public String getProductHindiTitle() {
        return productHindiTitle;
    }

    public void setProductHindiTitle(String productHindiTitle) {
        this.productHindiTitle = productHindiTitle;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getProductQuantityForCart() {
        return productQuantityForCart;
    }

    public void setProductQuantityForCart(int productQuantityForCart) {
        this.productQuantityForCart = productQuantityForCart;
    }

    public String getProductImageResource() {
        return productImageResource;
    }

    public void setProductImageResource(String productImageResource) {
        this.productImageResource = productImageResource;
    }
}
