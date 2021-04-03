package com.pemaseervi.myretailer.Model;

import java.util.Date;

public class MyOrderItemModel {
    private String productImage,productId;
    private String productTitle,productHindiTitle,orderStatus;
    private String address;
    private Date orderedDate,packedDate,shippedDate,deliveredDate,cancelledDate;
    private String fullName,orderID,paymentMethod,pincode,productPrice;
    private Long ProductQuantity;
    private String userId;

    public MyOrderItemModel(String productId, String orderID, String productPrice, Long productQuantity, String userId,String productTitle,String productHindiTitle,String productImage) {
        this.productImage=productImage;
        this.productTitle=productTitle;
        this.productHindiTitle=productHindiTitle;
        this.productId = productId;
//        this.orderStatus = orderStatus;
//        this.address = address;
//        this.orderedDate = orderedDate;
//        this.packedDate = packedDate;
//        this.shippedDate = shippedDate;
//        this.deliveredDate = deliveredDate;
//        this.cancelledDate = cancelledDate;
//        this.fullName = fullName;
        this.orderID = orderID;
//        this.paymentMethod = paymentMethod;
//        this.pincode = pincode;
        this.productPrice = productPrice;
        ProductQuantity = productQuantity;
        this.userId = userId;

    }



    public String getProductHindiTitle() {
        return productHindiTitle;
    }

    public void setProductHindiTitle(String productHindiTitle) {
        this.productHindiTitle = productHindiTitle;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Date getPackedDate() {
        return packedDate;
    }

    public void setPackedDate(Date packedDate) {
        this.packedDate = packedDate;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public Date getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public Long getProductQuantity() {
        return ProductQuantity;
    }

    public void setProductQuantity(Long productQuantity) {
        ProductQuantity = productQuantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
