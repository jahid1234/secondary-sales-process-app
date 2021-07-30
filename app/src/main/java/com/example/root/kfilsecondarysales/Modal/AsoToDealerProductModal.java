package com.example.root.kfilsecondarysales.Modal;

/**
 * Created by root on 10/28/19.
 */

public class AsoToDealerProductModal {

    boolean isChecked;
    String product_name;
    String product_code;
    int carton;
    int pice;
    String unit;
    int m_product_id;
    String totalPice;
    double price;
    String pAmount;
    int productCategoryID;
    public AsoToDealerProductModal(String product_name, String product_code,int m_product_id,String unit,double price,String pAmount,int productCategoryID) {
        this.product_name = product_name;
        this.product_code = product_code;
        this.m_product_id = m_product_id;
        this.unit         = unit;
        this.price        = price;
        this.pAmount      = pAmount;
        this.productCategoryID = productCategoryID;
        isChecked = false;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setTotalPice(String totalPice) {
        this.totalPice = totalPice;
    }

    public void setCarton(int carton) {
        this.carton = carton;
    }

    public void setPice(int pice) {
        this.pice = pice;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getProduct_code() {
        return product_code;
    }

    public String getTotalPice() {
        if(pAmount.equals("") || pAmount == null || pAmount.isEmpty()){
            int total_Pice = (carton * 1)+ pice;
            totalPice = String.valueOf(total_Pice);
            return totalPice;
        }else {
            int amountPerUnit = Integer.parseInt(pAmount);
            int total_Pice = (carton *amountPerUnit)+ pice;
            totalPice = String.valueOf(total_Pice);
            return totalPice;
        }

    }

    public int getCarton() {
        return carton;
    }

    public int getPice() {
        return pice;
    }

    public int getM_product_id(){
        return m_product_id;
    }

    public String getUnit(){
        return unit;
    }

    public double getPrice(){
        return price;
    }

    public int getProductCategoryID() {
        return productCategoryID;
    }

    public boolean isChecked() {
        return isChecked;
    }


}

