package com.example.root.kfilsecondarysales.Modal;

/**
 * Created by root on 9/15/19.
 */

public class ProductModal {

    String productName;
    String quantity;
    String unit;
    String date;
    public ProductModal(String productName, String quantity, String unit,String date) {
        this.productName = productName;
        this.quantity = quantity;
        this.unit = unit;
        this.date = date;
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

    public String getDate() {
        return date;
    }
}
