package com.example.ecommerce.model;

public class AdminOrder {

    private String orderName, orderPhoneNUmber, orderAddress, orderCity, state, date, time, totalPrice;

    public AdminOrder() {
    }

    public AdminOrder(String orderName, String orderPhoneNUmber, String orderAddress, String orderCity, String state, String date, String time, String totalPrice) {
        this.orderName = orderName;
        this.orderPhoneNUmber = orderPhoneNUmber;
        this.orderAddress = orderAddress;
        this.orderCity = orderCity;
        this.state = state;
        this.date = date;
        this.time = time;
        this.totalPrice = totalPrice;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderPhoneNUmber() {
        return orderPhoneNUmber;
    }

    public void setOrderPhoneNUmber(String orderPhoneNUmber) {
        this.orderPhoneNUmber = orderPhoneNUmber;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderCity() {
        return orderCity;
    }

    public void setOrderCity(String orderCity) {
        this.orderCity = orderCity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
