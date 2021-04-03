package com.pemaseervi.myretailer.Model;

public class CartItemModel {

    public static final int CART_ITEM=0;
    public static final int TOTAL_AMOUNT=1;

    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
////////////cart itme

    private String productTitle,productHindiTitle;
    private String productPrice,sellingPrice,product_catalog,product_category;
//            ,cuttedPrice;
    private String product_image;
//    private Boolean inStock;
    private int productQuantityForOrder;
//    private int freeCoupen;
//    private int productQuantity;
//        ,offersApplied,coupenApplied;

    public CartItemModel(int type, String productTitle,String productHindiTitle, String productPrice, String sellingPrice,String  product_image,int productQuantityForOrder,String product_catalog,String product_category) {
        this.type = type;
        this.productTitle = productTitle;
        this.productHindiTitle=productHindiTitle;
        this.productPrice = productPrice;
        this.sellingPrice=sellingPrice;
        this.product_image = product_image;
//        this.inStock=inStock;
        this.productQuantityForOrder= productQuantityForOrder;
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

    public int getProductQuantityForOrder() {
        return productQuantityForOrder;
    }

    public void setProductQuantityForOrder(int productQuantityForOrder) {
        this.productQuantityForOrder = productQuantityForOrder;
    }

//    public Boolean getInStock() {
//        return inStock;
//    }
//
//    public void setInStock(Boolean inStock) {
//        this.inStock = inStock;
//    }

    public static int getCartItem() {
        return CART_ITEM;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

//    public int getProductQuantity() {
//        return productQuantity;
//    }
//
//    public void setProductQuantity(int productQuantity) {
//        this.productQuantity = productQuantity;
//    }
//    public CartItemModel(int type, String productTitle, String productPrice, String cuttedPrice, int product_image, int freeCoupen, int productQuantity, int offersApplied, int coupenApplied) {
//        this.type = type;
//        this.productTitle = productTitle;
//        this.productPrice = productPrice;
//        this.cuttedPrice = cuttedPrice;
//        this.product_image = product_image;
//        this.freeCoupen = freeCoupen;
//        this.productQuantity = productQuantity;
//        this.offersApplied = offersApplied;
//        this.coupenApplied = coupenApplied;
//    }
//
//    public static int getCartItem() {
//        return CART_ITEM;
//    }
//
//    public static int getTotalAmount() {
//        return TOTAL_AMOUNT;
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
//    public String getProductPrice() {
//        return productPrice;
//    }
//
//    public void setProductPrice(String productPrice) {
//        this.productPrice = productPrice;
//    }
//
//    public String getCuttedPrice() {
//        return cuttedPrice;
//    }
//
//    public void setCuttedPrice(String cuttedPrice) {
//        this.cuttedPrice = cuttedPrice;
//    }
//
//    public int getProduct_image() {
//        return product_image;
//    }
//
//    public void setProduct_image(int product_image) {
//        this.product_image = product_image;
//    }
//
//    public int getFreeCoupen() {
//        return freeCoupen;
//    }
//
//    public void setFreeCoupen(int freeCoupen) {
//        this.freeCoupen = freeCoupen;
//    }
//
//    public int getProductQuantity() {
//        return productQuantity;
//    }
//
//    public void setProductQuantity(int productQuantity) {
//        this.productQuantity = productQuantity;
//    }
//
//    public int getOffersApplied() {
//        return offersApplied;
//    }
//
//    public void setOffersApplied(int offersApplied) {
//        this.offersApplied = offersApplied;
//    }
//
//    public int getCoupenApplied() {
//        return coupenApplied;
//    }
//
//    public void setCoupenApplied(int coupenApplied) {
//        this.coupenApplied = coupenApplied;
//    }
//////////////cart item

    public CartItemModel(int type) {
        this.type = type;
    }

    private int totalItems,totalItemPrice,totalAmount,savedAmount;
    private String deliveryPrice;

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalItemPrice() {
        return totalItemPrice;
    }

    public void setTotalItemPrice(int totalItemPrice) {
        this.totalItemPrice = totalItemPrice;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getSavedAmount() {
        return savedAmount;
    }

    public void setSavedAmount(int savedAmount) {
        this.savedAmount = savedAmount;
    }

    public String getDeliveryPrice() {
        return deliveryPrice;
    }

    public void setDeliveryPrice(String deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    ////////////////cart total
//    private String totalItems;
//    private String totalItemPrice,deliveryAmount,savedAmount,totalAmount;
//
//    public CartItemModel(int type, String totalItems, String totalItemPrice, String deliveryAmount, String savedAmount, String totalAmount) {
//        this.type = type;
//        this.totalItems = totalItems;
//        this.totalItemPrice = totalItemPrice;
//        this.deliveryAmount = deliveryAmount;
//        this.savedAmount = savedAmount;
//        this.totalAmount = totalAmount;
//    }
//
//    public String getTotalItems() {
//        return totalItems;
//    }
//
//    public void setTotalItems(String totalItems) {
//        this.totalItems = totalItems;
//    }
//
//    public String getTotalItemPrice() {
//        return totalItemPrice;
//    }
//
//    public void setTotalItemPrice(String totalItemPrice) {
//        this.totalItemPrice = totalItemPrice;
//    }
//
//    public String getDeliveryAmount() {
//        return deliveryAmount;
//    }
//
//    public void setDeliveryAmount(String deliveryAmount) {
//        this.deliveryAmount = deliveryAmount;
//    }
//
//    public String getSavedAmount() {
//        return savedAmount;
//    }
//
//    public String getTotalAmountCart(){
//        return totalAmount;
//    }
//    public void setSavedAmount(String savedAmount) {
//        this.savedAmount = savedAmount;
//    }
//
//    public void setTotalAmount(String totalAmount) {
//        this.totalAmount = totalAmount;
//    }
/////////////////cart total
}
