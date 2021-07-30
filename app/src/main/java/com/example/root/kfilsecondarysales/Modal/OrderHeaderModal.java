package com.example.root.kfilsecondarysales.Modal;

/**
 * Created by root on 11/7/19.
 */

public class OrderHeaderModal {
    String orderNo;
    String grandTotal;
    String dealerCode;

    public OrderHeaderModal(String orderNo, String grandTotal, String dealerCode) {
        this.orderNo = orderNo;
        this.grandTotal = grandTotal;
        this.dealerCode = dealerCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public String getDealerCode() {
        return dealerCode;
    }
}
