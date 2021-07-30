package com.example.root.kfilsecondarysales.Modal;

/**
 * Created by root on 11/3/19.
 */

public class ProductModalOfOrderActivity {

    boolean isChecked;
    String p_name;
    String p_code;
    String p_unit;
    String p_price;
    String qty;
    int productID;
    double totalPrice;

    public ProductModalOfOrderActivity(String p_name, String p_code, String p_unit, String p_price,int productID) {
        this.p_name = p_name;
        this.p_code = p_code;
        this.p_unit = p_unit;
        this.p_price = p_price;
        this.productID = productID;
        isChecked = false;
    }

    public boolean getCheckStatus(){
        return isChecked;
    }

    public String getP_name() {
        return p_name;
    }

    public String getP_code() {
        return p_code;
    }

    public String getP_unit() {
        return p_unit;
    }

    public String getP_price() {
        return p_price;
    }

    public int getProductID() {
        return productID;
    }

    public String getQty() {
        return qty;
    }

    public double getTotalPrice() {
        try {
            if (!qty.equals("") && p_price != null) {
                totalPrice = Double.parseDouble(qty) * Double.parseDouble(p_price);
                return totalPrice;
            }else{
                totalPrice = 0;
            }
        }catch (NumberFormatException ex){

        }

        return totalPrice;
    }


    public void setQty(String qty) {
        this.qty = qty;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setCheckStatus(boolean isChecked){
        this.isChecked = isChecked;
    }
}
