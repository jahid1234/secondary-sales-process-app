package com.example.root.kfilsecondarysales.Modal;

/**
 * Created by root on 1/12/20.
 */

public class OutletReceivedDiscount {

    int outlet_id = 0;
    int discount_id = 0;
    int reward_product_id = 0;
    int reward_qty = 0;
    double cash_discount = 0 ;
    double percentage_discount     = 0;
    double flat_discount = 0;

    public OutletReceivedDiscount(int outlet_id, int discount_id, int reward_product_id, int reward_qty, double cash_discount, double percentage_discount, double flat_discount) {
        this.outlet_id = outlet_id;
        this.discount_id = discount_id;
        this.reward_product_id = reward_product_id;
        this.reward_qty = reward_qty;
        this.cash_discount = cash_discount;
        this.percentage_discount = percentage_discount;
        this.flat_discount = flat_discount;
    }

    public int getOutlet_id() {
        return outlet_id;
    }

    public int getDiscount_id() {
        return discount_id;
    }

    public int getReward_product_id() {
        return reward_product_id;
    }

    public int getReward_qty() {
        return reward_qty;
    }

    public double getCash_discount() {
        return cash_discount;
    }

    public double getPercentage_discount() {
        return percentage_discount;
    }

    public double getFlat_discount() {
        return flat_discount;
    }
}
