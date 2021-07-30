package com.example.root.kfilsecondarysales.Modal;

/**
 * Created by root on 10/7/19.
 */

public class SalesRecordPullerModal {

    String pKey;
    String routename;
    String outletName;
    String productName;
    String quantity;
    String unit;
    double price;
    int productID;
    int categoryID;

    public SalesRecordPullerModal(String pKey,String routename,String outletName, String productName, String quantity, String unit,double price,int productID,int categoryID) {
        this.pKey = pKey;
        this.routename = routename;
        this.outletName = outletName;
        this.productName = productName;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
        this.productID = productID;
        this.categoryID = categoryID;
    }

    public String getRoutename() {
        return routename;
    }

    public String getpKey(){
        return pKey;
    }

    public String getOutletName() {
        return outletName;
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

    public double getPrice(){
        return price;
    }

    public int getProductID(){
        return productID;
    }

    public int getCategoryID() {
        return categoryID;
    }
}
