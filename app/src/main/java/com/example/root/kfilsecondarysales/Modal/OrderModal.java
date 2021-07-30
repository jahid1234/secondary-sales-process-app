package com.example.root.kfilsecondarysales.Modal;

/**
 * Created by root on 10/14/19.
 */

public class OrderModal {

    String pKey;
    String productCode;
    String productName;
    String quantity;
    String unit;
    String orderCode;
    String price;

    public OrderModal(String productCode, String price, String quantity, String unit, String orderCode) {
        this.productCode = productCode;
        this.quantity = quantity;
        this.unit = unit;
        this.orderCode = orderCode;
        this.price     = price;

    }


    public String getPrice() {
        return price;
    }

    public String getpKey() {
        return pKey;
    }

    public String getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getOrderCode() {
        return orderCode;
    }
}
