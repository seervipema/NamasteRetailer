package com.pemaseervi.myretailer.Model;

public class OrderStatusModel {
    private String orderStatus;

    public OrderStatusModel(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
