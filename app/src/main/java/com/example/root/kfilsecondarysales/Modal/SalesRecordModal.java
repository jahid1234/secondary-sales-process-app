package com.example.root.kfilsecondarysales.Modal;

/**
 * Created by root on 9/24/19.
 */

public class SalesRecordModal {
    String outletName;
    String productName;
    String quantity;
    String unit;

    public SalesRecordModal(String outletName,String productName,String quantity,String unit) {
        this.outletName = outletName;
        this.productName = productName;
        this.quantity = quantity;
        this.unit = unit;
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
}
