package com.example.root.kfilsecondarysales.Modal;

/**
 * Created by root on 12/29/19.
 */

public class DiscountCalculationModal {

    double  m_cash_discount_f_total_purchase = 0;
    double  m_percentage_discount_f_total_purchase = 0;
    int m_rewd_productID_f_total_purchase = 0 ;
    int m_rewd_product_qty_f_total_purchase = 0;

    double  m_cash_discount_f_target_product = 0;
    double  m_percentage_discount_f_target_product = 0;
    int m_rewd_productID_f_target_product = 0 ;
    int m_rewd_product_qty_f_target_product = 0;

    double  m_cash_discount_f_target_category = 0;
    double  m_percentage_discount_f_target_category = 0;
    int m_rewd_productID_f_target_category = 0 ;
    int m_rewd_product_qty_f_target_category = 0;

    double m_flat_discount = 0;

    public DiscountCalculationModal(double m_cash_discount_f_total_purchase, double m_percentage_discount_f_total_purchase, int m_rewd_productID_f_total_purchase, int m_rewd_product_qty_f_total_purchase, double m_cash_discount_f_target_product, double m_percentage_discount_f_target_product, int m_rewd_productID_f_target_product, int m_rewd_product_qty_f_target_product, double m_cash_discount_f_target_category, double m_percentage_discount_f_target_category, int m_rewd_productID_f_target_category, int m_rewd_product_qty_f_target_category, double m_flat_discount) {
        this.m_cash_discount_f_total_purchase = m_cash_discount_f_total_purchase;
        this.m_percentage_discount_f_total_purchase = m_percentage_discount_f_total_purchase;
        this.m_rewd_productID_f_total_purchase = m_rewd_productID_f_total_purchase;
        this.m_rewd_product_qty_f_total_purchase = m_rewd_product_qty_f_total_purchase;
        this.m_cash_discount_f_target_product = m_cash_discount_f_target_product;
        this.m_percentage_discount_f_target_product = m_percentage_discount_f_target_product;
        this.m_rewd_productID_f_target_product = m_rewd_productID_f_target_product;
        this.m_rewd_product_qty_f_target_product = m_rewd_product_qty_f_target_product;
        this.m_cash_discount_f_target_category = m_cash_discount_f_target_category;
        this.m_percentage_discount_f_target_category = m_percentage_discount_f_target_category;
        this.m_rewd_productID_f_target_category = m_rewd_productID_f_target_category;
        this.m_rewd_product_qty_f_target_category = m_rewd_product_qty_f_target_category;
        this.m_flat_discount = m_flat_discount;
    }

    public double getM_cash_discount_f_total_purchase() {
        return m_cash_discount_f_total_purchase;
    }

    public double getM_percentage_discount_f_total_purchase() {
        return m_percentage_discount_f_total_purchase;
    }

    public int getM_rewd_productID_f_total_purchase() {
        return m_rewd_productID_f_total_purchase;
    }

    public int getM_rewd_product_qty_f_total_purchase() {
        return m_rewd_product_qty_f_total_purchase;
    }

    public double getM_cash_discount_f_target_product() {
        return m_cash_discount_f_target_product;
    }

    public double getM_percentage_discount_f_target_product() {
        return m_percentage_discount_f_target_product;
    }

    public int getM_rewd_productID_f_target_product() {
        return m_rewd_productID_f_target_product;
    }

    public int getM_rewd_product_qty_f_target_product() {
        return m_rewd_product_qty_f_target_product;
    }

    public double getM_cash_discount_f_target_category() {
        return m_cash_discount_f_target_category;
    }

    public double getM_percentage_discount_f_target_category() {
        return m_percentage_discount_f_target_category;
    }

    public int getM_rewd_productID_f_target_category() {
        return m_rewd_productID_f_target_category;
    }

    public int getM_rewd_product_qty_f_target_category() {
        return m_rewd_product_qty_f_target_category;
    }

    public double getM_flat_discount() {
        return m_flat_discount;
    }

    public void setM_cash_discount_f_total_purchase(double m_cash_discount_f_total_purchase) {
        this.m_cash_discount_f_total_purchase = m_cash_discount_f_total_purchase;
    }

    public void setM_percentage_discount_f_total_purchase(double m_percentage_discount_f_total_purchase) {
        this.m_percentage_discount_f_total_purchase = m_percentage_discount_f_total_purchase;
    }

    public void setM_rewd_productID_f_total_purchase(int m_rewd_productID_f_total_purchase) {
        this.m_rewd_productID_f_total_purchase = m_rewd_productID_f_total_purchase;
    }

    public void setM_rewd_product_qty_f_total_purchase(int m_rewd_product_qty_f_total_purchase) {
        this.m_rewd_product_qty_f_total_purchase = m_rewd_product_qty_f_total_purchase;
    }

    public void setM_cash_discount_f_target_product(double m_cash_discount_f_target_product) {
        this.m_cash_discount_f_target_product = m_cash_discount_f_target_product;
    }

    public void setM_percentage_discount_f_target_product(double m_percentage_discount_f_target_product) {
        this.m_percentage_discount_f_target_product = m_percentage_discount_f_target_product;
    }

    public void setM_rewd_productID_f_target_product(int m_rewd_productID_f_target_product) {
        this.m_rewd_productID_f_target_product = m_rewd_productID_f_target_product;
    }

    public void setM_rewd_product_qty_f_target_product(int m_rewd_product_qty_f_target_product) {
        this.m_rewd_product_qty_f_target_product = m_rewd_product_qty_f_target_product;
    }

    public void setM_cash_discount_f_target_category(double m_cash_discount_f_target_category) {
        this.m_cash_discount_f_target_category = m_cash_discount_f_target_category;
    }

    public void setM_percentage_discount_f_target_category(double m_percentage_discount_f_target_category) {
        this.m_percentage_discount_f_target_category = m_percentage_discount_f_target_category;
    }

    public void setM_rewd_productID_f_target_category(int m_rewd_productID_f_target_category) {
        this.m_rewd_productID_f_target_category = m_rewd_productID_f_target_category;
    }

    public void setM_rewd_product_qty_f_target_category(int m_rewd_product_qty_f_target_category) {
        this.m_rewd_product_qty_f_target_category = m_rewd_product_qty_f_target_category;
    }

    public void setM_flat_discount(double m_flat_discount) {
        this.m_flat_discount = m_flat_discount;
    }
}
