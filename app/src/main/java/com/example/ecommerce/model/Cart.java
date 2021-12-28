package com.example.ecommerce.model;

public class Cart {

    private String pid;
    private String productName;
    private String productPrice;
    private String quantity;
    private String discount;

    public Cart() {
    }

    public Cart(String pid, String productName, String productPrice, String quantity, String discount) {
        this.pid = pid;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.discount = discount;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
