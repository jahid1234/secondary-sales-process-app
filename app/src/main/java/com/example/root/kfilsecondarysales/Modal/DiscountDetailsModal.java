package com.example.root.kfilsecondarysales.Modal;

/**
 * Created by root on 12/22/19.
 */

public class DiscountDetailsModal {
    int discountID;
    int targetProductID;
    int targetCategoryID;
    int targetQty;
    double targetPurchase;
    int rewardProductID;
    int rewardCategoryID;
    int rewardQty;
    double cashDiscount;
    double percentageDiscount;
    double flatDiscount;

    public DiscountDetailsModal(int discountID, int targetProductID, int targetCategoryID, int targetQty, double targetPurchase, int rewardProductID, int rewardCategoryID, int rewardQty, double cashDiscount, double percentageDiscount, double flatDiscount) {
        this.discountID = discountID;
        this.targetProductID = targetProductID;
        this.targetCategoryID = targetCategoryID;
        this.targetQty = targetQty;
        this.targetPurchase = targetPurchase;
        this.rewardProductID = rewardProductID;
        this.rewardCategoryID = rewardCategoryID;
        this.rewardQty = rewardQty;
        this.cashDiscount = cashDiscount;
        this.percentageDiscount = percentageDiscount;
        this.flatDiscount = flatDiscount;
    }


    public int getDiscountID() {
        return discountID;
    }

    public int getTargetProductID() {
        return targetProductID;
    }

    public int getTargetCategoryID() {
        return targetCategoryID;
    }

    public int getTargetQty() {
        return targetQty;
    }

    public double getTargetPurchase() {
        return targetPurchase;
    }

    public int getRewardProductID() {
        return rewardProductID;
    }

    public int getRewardCategoryID() {
        return rewardCategoryID;
    }

    public int getRewardQty() {
        return rewardQty;
    }

    public double getCashDiscount() {
        return cashDiscount;
    }

    public double getPercentageDiscount() {
        return percentageDiscount;
    }

    public double getFlatDiscount() {
        return flatDiscount;
    }
}
