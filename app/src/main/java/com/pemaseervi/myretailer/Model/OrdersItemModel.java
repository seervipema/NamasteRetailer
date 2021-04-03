package com.pemaseervi.myretailer.Model;

public class OrdersItemModel {
    private String productImage;
    private String orderStatus;
    private String address;
    private String orderedDate,packedDate,shippedDate,deliveredDate,cancelledDate;
    private String fullName,paymentMethod,pincode,orderId;
    private Long totalItems,totalAmount;

    public OrdersItemModel(String productImage, String orderStatus, String address, String orderedDate, String packedDate, String shippedDate, String deliveredDate, String cancelledDate, String fullName, String paymentMethod, String pincode, Long totalItems, Long totalAmount,String orderId) {
        this.productImage = productImage;
        this.orderStatus = orderStatus;
        this.address = address;
        this.orderedDate = orderedDate;
        this.packedDate = packedDate;
        this.shippedDate = shippedDate;
        this.deliveredDate = deliveredDate;
        this.cancelledDate = cancelledDate;
        this.fullName = fullName;
        this.paymentMethod = paymentMethod;
        this.pincode = pincode;
        this.totalItems = totalItems;
        this.totalAmount = totalAmount;
        this.orderId=orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
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

    public String getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(String orderedDate) {
        this.orderedDate = orderedDate;
    }

    public String getPackedDate() {
        return packedDate;
    }

    public void setPackedDate(String packedDate) {
        this.packedDate = packedDate;
    }

    public String getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(String shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(String deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public String getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(String cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }
}
