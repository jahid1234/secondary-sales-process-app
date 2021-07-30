package com.example.root.kfilsecondarysales.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

/**
 * Created by root on 9/18/19.
 */

public class SqliteDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "kfilsales";

//    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    java.util.Date date = new java.util.Date();
//    java.util.Date todayWithZeroTime = dateFormat.parse(dateFormat.format(date));

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat dateOnly = new SimpleDateFormat("MM/dd/yyyy");

    public SqliteDbHelper(Context context) {
        super(context,DATABASE_NAME,null,1);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createDealerTable = "CREATE TABLE dealer(dealer_code Integer,dealer_balance Integer Default 0)";
        String createOutletSales = "CREATE TABLE outletSales(outletSales_id INTEGER PRIMARY KEY AUTOINCREMENT,route_name TEXT,outlet_name TEXT,outlet_code Integer,product_name TEXT,quantity INTEGER,unit TEXT,puller_code TEXT,date DATE,saleprice Integer Default 0,collectedcash Integer Default 0,m_product_id Integer Default 0)";
        String createInventory   = "CREATE TABLE dealerInventory(inventory_id INTEGER PRIMARY KEY AUTOINCREMENT,product_name TEXT,product_code TEXT,product_id Integer,quantity INTEGER,unit TEXT,date TEXT,category_id Integer)";
        String createRoute       = "CREATE TABLE route(route_id INTEGER PRIMARY KEY AUTOINCREMENT,route_code INTEGER,route_name TEXT)";
        String createOutlet      = "CREATE TABLE outlet(outlet_id INTEGER PRIMARY KEY AUTOINCREMENT,route_code INTEGER,outlet_code INTEGER,outlet_name TEXT,discount Integer Default 0,amount Integer Default 0,updateAmtStatus TEXT DEFAULT 'N')";
        String createProduct     = "CREATE TABLE product(product_id INTEGER PRIMARY KEY AUTOINCREMENT,product_code TEXT,product_name TEXT)";
        String createPuller      = "CREATE TABLE puller(puller_id INTEGER PRIMARY KEY AUTOINCREMENT,puller_code TEXT,puller_name TEXT,password TEXT,target_outlet Integer Default 0)";
        String createRecordSales = "CREATE TABLE salesRecord(salesRecord_id INTEGER PRIMARY KEY AUTOINCREMENT,route_name TEXT,outlet_name TEXT,outlet_code TEXT,product_name TEXT,quantity INTEGER,unit TEXT,puller_code TEXT,print_status TEXT DEFAULT 'N',date DATE,saleprice Integer Default 0,product_id Integer,category_id Integer)";
        String createProductList = "CREATE TABLE product_list(productlist_id INTEGER PRIMARY KEY AUTOINCREMENT,product_name TEXT,product_code TEXT,brand TEXT,product_category_id Integer,unit TEXT,price Integer Default 0,m_product_id Integer,a_perUnit Text)";

        String createPullerInventory     = "CREATE TABLE pullerInventory(product_name TEXT,quantity INTEGER)";
        //String createEmployeeTable="CREATE TABLE employee(id INTEGER PRIMARY KEY AUTOINCREMENT,user_id TEXT)";
        String createOrderHeaderTable    = "CREATE TABLE orderHeader(orderheader_id INTEGER PRIMARY KEY AUTOINCREMENT,order_no TEXT,dealer_code TEXT,total_purchase_amount Integer)";
        String createOrderTable          = "CREATE TABLE dealerorder(order_id INTEGER PRIMARY KEY AUTOINCREMENT,order_no TEXT,product_code TEXT,quantity INTEGER,unit TEXT,price Integer)";
        String createOutletProductPrice  = "CREATE TABLE outletProductPrice(outletProductPrice_id INTEGER PRIMARY KEY AUTOINCREMENT,outlet_code Integer,m_product_id Integer,price INTEGER Default 0)";
        String createOutletCollectedCash = "CREATE TABLE outletCashCollection(outletCashCollection_id INTEGER PRIMARY KEY AUTOINCREMENT,puller_code Text,outlet_code Text,cash INTEGER Default 0)";
        String createOutletDiscount      = "CREATE TABLE ouletDiscount(ouletDiscount_id INTEGER PRIMARY KEY AUTOINCREMENT,t_outlet_info_id Integer,t_discount_id Integer,discount_name Text,target_product_id Integer,target_category_id Integer,target_qty Integer,target_purchase Integer,reward_product_id Integer,reward_category_id Integer,reward_qty Integer,cash_discount Integer,percentage_discount Integer,flat_discount INTEGER)";
        String createReceivedDiscount    = "CREATE TABLE receivedDiscount(receivedDiscount_id INTEGER PRIMARY KEY AUTOINCREMENT,t_outlet_info_id Integer,discount_id Integer,reward_product_id Integer,reward_qty Integer,cash_discount Integer,percentage_discount Integer,flat_discount Integer)";


        db.execSQL(createDealerTable);
        db.execSQL(createOutletSales);
        db.execSQL(createInventory);
        db.execSQL(createRoute);
        db.execSQL(createOutlet);
        db.execSQL(createProduct);
        db.execSQL(createPuller);
        db.execSQL(createRecordSales);
        db.execSQL(createProductList);
        db.execSQL(createOrderTable);
        db.execSQL(createOrderHeaderTable);
        db.execSQL(createPullerInventory);
        db.execSQL(createOutletProductPrice);
        db.execSQL(createOutletCollectedCash);
        db.execSQL(createOutletDiscount);
        db.execSQL(createReceivedDiscount);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS dealer");
        db.execSQL("DROP TABLE IF EXISTS outletSales");
        db.execSQL("DROP TABLE IF EXISTS dealerInventory");
        db.execSQL("DROP TABLE IF EXISTS route");
        db.execSQL("DROP TABLE IF EXISTS outlet");
        db.execSQL("DROP TABLE IF EXISTS product");
        db.execSQL("DROP TABLE IF EXISTS puller");
        db.execSQL("DROP TABLE IF EXISTS salesRecord");
        db.execSQL("DROP TABLE IF EXISTS product_list");
        db.execSQL("DROP TABLE IF EXISTS dealerorder");
        db.execSQL("DROP TABLE IF EXISTS pullerInventory");
        db.execSQL("DROP TABLE IF EXISTS createOrderHeaderTable");
        db.execSQL("DROP TABLE IF EXISTS outletProductPrice");
        db.execSQL("DROP TABLE IF EXISTS outletCashCollection");
        db.execSQL("DROP TABLE IF EXISTS ouletDiscount");
        db.execSQL("DROP TABLE IF EXISTS receivedDiscount");
        onCreate(db);
    }

    public boolean insertIntoOrderHeader(String orderNo,int totalPrice,String dealerCode){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("order_no", orderNo);
        contentValues.put("total_purchase_amount", totalPrice);
        contentValues.put("dealer_code", dealerCode);

        long res = db.insert("orderHeader", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean insertIntoOrder(String orderNo,String productCode,int quantity,String unit,int price){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("order_no", orderNo);
        contentValues.put("product_code", productCode);
        contentValues.put("quantity", quantity);
        contentValues.put("unit", unit);
        contentValues.put("price",price);

        long res = db.insert("dealerorder", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean insertIntoReceivedDiscount(int outletId,int discountId,int rwd_productId,int rewdQty,double cashDiscount,double percentageDiscount,double flatDiscount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("t_outlet_info_id", outletId);
        contentValues.put("discount_id",discountId);
        contentValues.put("reward_product_id",rwd_productId);
        contentValues.put("reward_qty",rewdQty);
        contentValues.put("cash_discount",cashDiscount);
        contentValues.put("percentage_discount",percentageDiscount);
        contentValues.put("flat_discount",flatDiscount);
        long res = db.insert("receivedDiscount", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }
    }

    public Cursor getRewardQty(int productID,int outletID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select reward_qty from receivedDiscount where t_outlet_info_id = '"+outletID+"' and reward_product_id = '"+productID+"' ",null);
        return res;
    }

    public Cursor getReceivedDiscount(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from receivedDiscount ",null);
        return res;
    }


    public boolean insertIntoOutletDiscount(int outletId,int discountId,String discountName,int t_produtc_id,int t_category_id,int targetQty,double targetPurchase,int rwd_productId,int rwd_categoryId,int rewdQty,double cashDiscount,double percentageDiscount,double flatDiscount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("t_outlet_info_id", outletId);
        contentValues.put("t_discount_id",discountId);
        contentValues.put("discount_name",discountName);
        contentValues.put("target_product_id",t_produtc_id);
        contentValues.put("target_category_id",t_category_id);
        contentValues.put("target_qty",targetQty);
        contentValues.put("target_purchase",targetPurchase);
        contentValues.put("reward_product_id",rwd_productId);
        contentValues.put("reward_category_id",rwd_categoryId);
        contentValues.put("reward_qty",rewdQty);
        contentValues.put("cash_discount",cashDiscount);
        contentValues.put("percentage_discount",percentageDiscount);
        contentValues.put("flat_discount",flatDiscount);
        long res = db.insert("ouletDiscount", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }
    }


    public boolean insertIntoOutletCashCollection(String pullerCode,String outletCode, double cash){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("puller_code",pullerCode);
        contentValues.put("outlet_code", outletCode);
        contentValues.put("cash", cash);
        long res = db.insert("outletCashCollection", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean insertIntoProductList(String productName,String productCode,String brand,String unit,double price,int productId,String amountPerUnit,int productCategoryID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("product_name", productName);
        contentValues.put("product_code", productCode);
        contentValues.put("brand", brand);
        contentValues.put("product_category_id",productCategoryID);
        contentValues.put("unit", unit);
        contentValues.put("price",price);
        contentValues.put("m_product_id",productId);
        contentValues.put("a_perUnit",amountPerUnit);
        long res = db.insert("product_list", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }
    }

    public boolean insertDealer (String dealerCode,double balance) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("dealer_code", dealerCode);
        contentValues.put("dealer_balance",balance);

        long res = db.insert("dealer", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }

    }

    public boolean insertPuller(String pullerCode,String pullerName,String password,int targetOutlet){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("puller_code", pullerCode);
        contentValues.put("puller_name", pullerName);
        contentValues.put("password", password);
        contentValues.put("target_outlet",targetOutlet);

        long res = db.insert("puller", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }
    }


    public Cursor getDealerCode(String dealerCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select dealer_code from dealer where dealer_code ='"+dealerCode+"'", null );
        return res;
    }

    public boolean insertOutletSales(String routeName,String outletName,String outletCode,String productName,int quantity,String unit,String puller_code,double price,int productID){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("route_name", routeName);
        contentValues.put("outlet_name",outletName);
        contentValues.put("outlet_code",outletCode);
        contentValues.put("product_name",productName);
        contentValues.put("quantity",quantity);
        contentValues.put("unit",unit);
        contentValues.put("puller_code",puller_code);
        contentValues.put("date", dateOnly.format(cal.getTime()));
        contentValues.put("saleprice",price);
        contentValues.put("m_product_id",productID);

        long res = db.insert("outletSales", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }
    }


    public boolean insertSalesRecordPuller(String routeName,String outletName,String outletCode,String productName,int quantity,String unit,String puller_code,double price,int productID,int categoryID){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("route_name", routeName);
        contentValues.put("outlet_name",outletName);
        contentValues.put("outlet_code",outletCode);
        contentValues.put("product_name",productName);
        contentValues.put("quantity",quantity);
        contentValues.put("unit",unit);
        contentValues.put("puller_code",puller_code);
        contentValues.put("date", dateOnly.format(cal.getTime()));
        contentValues.put("saleprice",price);
        contentValues.put("product_id",productID);
        contentValues.put("category_id",categoryID);
        long res = db.insert("salesRecord", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }
    }


    public boolean insertIntoDealerInventory(String productName, String productCode,int productId, int quantity, String unit,String date,int categoryID){


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("product_name",productName);
        contentValues.put("product_code",productCode);
        contentValues.put("product_id",productId);
        contentValues.put("quantity",quantity);
        contentValues.put("unit",unit);
        contentValues.put("date",date);
        contentValues.put("category_id",categoryID);

        long res = db.insert("dealerInventory", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }
    }


    public boolean insertIntoRoute(int routeCode,String routeName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("route_code",routeCode);
        contentValues.put("route_name",routeName);

        long res = db.insert("route", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }

    }

    public boolean insertIntoOutlet(int outletCode,String outletName,int routeCode,double discount,double amount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("route_code",routeCode);
        contentValues.put("outlet_code",outletCode);
        contentValues.put("outlet_name",outletName);
        contentValues.put("discount",discount);
        contentValues.put("amount",amount);

        long res = db.insert("outlet", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }

    }


    public boolean insertIntoOutletProductPrice(int outletCode,int productId,double price){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("outlet_code",outletCode);
        contentValues.put("m_product_id",productId);
        contentValues.put("price",price);

        long res = db.insert("outletProductPrice", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }

    }

    public boolean insertIntoPullerInventory(String productName,int quantity){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("product_name",productName);
        contentValues.put("quantity",quantity);

        long res = db.insert("pullerInventory", null, contentValues);
        if(res == -1){
            return false;
        }else{
            return true;
        }

    }



//    public boolean insertIntoProduct(String productCode,String productName){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put("route_code",productCode);
//        contentValues.put("route_name",productName);
//
//        long res = db.insert("route", null, contentValues);
//        if(res == -1){
//            return false;
//        }else{
//            return true;
//        }
//
//    }

    public Cursor getProductIdFromInventory(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select product_id from dealerInventory", null );
        return res;
    }


    public Cursor getDiscountDetails(String outletID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from ouletDiscount where t_outlet_info_id ='"+outletID+"'", null );
        return res;
    }

    public Cursor getOutletDiscountName(String outletID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select discount_name from ouletDiscount where t_outlet_info_id ='"+outletID+"'", null );
        return res;
    }


    public Cursor getPullerInventory(String productName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select quantity from pullerInventory where product_name = '"+productName+"'",null);
        return res;
    }



    public Cursor getBrandList(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select DISTINCT brand from product_list",null);
        return res;
    }

    public Cursor getCategoryList(String brandName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select DISTINCT product_category from product_list where brand ='"+brandName+"' order by product_category",null);
        return res;
    }

    public Cursor getProductList(String brandName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select product_name,product_code,unit,price,m_product_id,a_perUnit,product_category_id from product_list where brand ='"+brandName+"' order by product_name",null);
        return res;
    }

    public Cursor getProductUnit(String productName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select unit from product_list where product_name ='"+productName+"'",null);
        return res;
    }

    public Cursor getSalesRecordPuller(String outletId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from salesRecord where outlet_code = '"+outletId+"' ",null);
        return res;
    }

    public Cursor getOutletSales(String routeName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from outletSales where route_name = '"+routeName+"' and date = '"+dateOnly.format(cal.getTime())+"' ",null);
        return res;
    }

    public Cursor getProductFromOutletSales(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from outletSales where date = '"+dateOnly.format(cal.getTime())+"' ",null);
        return res;
    }

    public Cursor getRouteList(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select DISTINCT route_name from outletSales where date = '"+dateOnly.format(cal.getTime())+"' ",null);
        return res;
    }

    public Cursor getDealerInventory(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from dealerInventory",null);
        return res;
    }

    public Cursor getUnitFromDealerInventory(String productName){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select unit from dealerInventory where product_name = '"+productName+"'",null);
        return res;
    }

    public Cursor getAllRoutes(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from route",null);
        return res;
    }

    public Cursor getAllOutlets(String routeCode){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from outlet where route_code = '"+routeCode+"'",null);
        return res;
    }

    public Cursor getAllProductsName(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select product_name,product_id,category_id from dealerInventory",null);
        return res;
    }

    public Cursor getOutletWiseProductPrice(String outletCode,int productId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select price from outletProductPrice where outlet_code = '"+outletCode+"' and m_product_id = '"+productId+"' ",null);
        return res;
    }

    public Cursor getProductUnitAmount(int productId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select a_perUnit from product_list where m_product_id = '"+productId+"' ",null);
        return res;
    }

    public Cursor getOutletBalance(String outletId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select amount from outlet where outlet_code = '"+outletId+"' ",null);
        return res;
    }

    public Cursor getDealer(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select dealer_code,dealer_balance from dealer",null);
        return res;
    }

    public Cursor getBrand(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select distinct brand from product_list",null);
        return res;
    }

    public Cursor getOutletAmount(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select outlet_code,amount from outlet where updateAmtStatus = 'Y'",null);
        return res;
    }

    public Cursor searchDuplicateProduct(int productID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select product_id from salesRecord where product_id = '"+productID+"'",null);
        return res;
    }

    public Cursor getAvailableProductAmount(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from pullerInventory",null);
        return res;
    }

    public void deleteSalesProducts(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM outletSales ";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deleteDealer(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM dealer ";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deletedealerInventory(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM dealerInventory ";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deleteOutlet(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM outlet ";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deleteRoute(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM route ";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deletePuller(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM puller ";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deleteSalesRecord(String pKey){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM salesRecord where salesRecord_id = '"+Integer.parseInt(pKey)+"'";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deleteProductList(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM product_list";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public Cursor pullerLogin(String userName,String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select puller_code from puller where puller_name = '"+userName+"' and password = '"+password+"'  ",null);
        return res;
    }

    public Cursor getTargetOutletNumber(String user_code){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select target_outlet from puller where puller_code = '"+user_code+"'",null);
        return res;
    }


    public Cursor getOrderList(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from dealerorder",null);
        return res;
    }

    public Cursor getOrderHeader(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from orderHeader",null);
        return res;
    }

    public void deleteOrderRecord(String pKey){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM dealerorder where order_id = '"+Integer.parseInt(pKey)+"'";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deleteAllOrderRecord(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM dealerorder";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deleteAllOrderHeaderRecord(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM orderHeader";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deletePullerInventory(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM pullerInventory";
        //Log.d(TAG, "deleteName: query: " + query);
        db.execSQL(query);
    }

    public void deleteSalesRecordOfPuller(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query      = "DELETE FROM salesRecord";
        db.execSQL(query);
    }

    public void deleteOutletProductPrice(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query      = "DELETE FROM outletProductPrice";
        db.execSQL(query);
    }

    public void deleteOutletCashCollection(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query      = "DELETE FROM outletCashCollection";
        db.execSQL(query);
    }

    public void deleteOutletDiscount(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query      = "DELETE FROM ouletDiscount";
        db.execSQL(query);
    }

    public void deleteReceivedDiscount(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query      = "DELETE FROM receivedDiscount";
        db.execSQL(query);
    }

    public Cursor getOrder(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from dealerorder",null);
        return res;
    }


    public Cursor getPullerState(String pullerCode){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT  COUNT(DISTINCT outlet_code) as visitedoutlet,COUNT(outlet_code) as lpc,SUM(saleprice) as totalsale FROM outletSales WHERE puller_code = '"+pullerCode+"'",null);
        return res;
    }

    public Cursor getPullerCashCollection(String pullerCode){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT SUM(cash) as totalcollected FROM outletCashCollection WHERE puller_code = '"+pullerCode+"'",null);
        return res;
    }




    public boolean upadtePullerInventory(String productName,int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("quantity", quantity);

        // Cursor result = db.rawQuery("update sales_order set discount = '"+discount+"' where docNo = '"+docNo+"'",null);
        long result = db.update("pullerInventory",contentValues,"product_name = ?",new String[]{productName});
        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateOutletBalance(String outletCode,double balanceAmount){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("amount", balanceAmount);
        contentValues.put("updateAmtStatus","Y");
        // Cursor result = db.rawQuery("update sales_order set discount = '"+discount+"' where docNo = '"+docNo+"'",null);
        long result = db.update("outlet",contentValues,"outlet_code = ?",new String[]{outletCode});
        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean updateOutletBalanceStatus(String outletCode){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("updateAmtStatus","N");
        // Cursor result = db.rawQuery("update sales_order set discount = '"+discount+"' where docNo = '"+docNo+"'",null);
        long result = db.update("outlet",contentValues,"outlet_code = ?",new String[]{outletCode});
        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
}
