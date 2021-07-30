package com.example.root.kfilsecondarysales.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.root.kfilsecondarysales.Database.PostgresqlConnection;
import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
import com.example.root.kfilsecondarysales.Modal.ProductModal;
import com.example.root.kfilsecondarysales.R;

import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.example.root.kfilsecondarysales.Activity.LoginActivity.userCode;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    static String roleName;
    String dealerCode;

    ProgressDialog pd;
    private CardView asoLogout,issueProductToDealer,asoSyncProducts,asoOrder;
    private CardView seeDealerInventory,updateDealerInventory,dealerLogout,seeRoute,dbSync,order;
    private CardView pullerOutletSales, pullerLogout,pullerStatus;

    List<Integer> routedataList = new ArrayList<>();
    List<Integer> outletList = new ArrayList<>();

    PostgresqlConnection postgresqlConnection = new PostgresqlConnection();
    SqliteDbHelper dbHelper = new SqliteDbHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null)
        {
            roleName =(String) bundle.get("roleName");

        }
        try {
            if (roleName.equals("dealer")) {
                setContentView(R.layout.activity_home_dealer);
                defineDealerHomeUi();
            } else if (roleName.equals("puller")) {
                setContentView(R.layout.activity_home_puller);
                definePullerHomeUi();
            } else if (roleName.equals("aso")) {
                setContentView(R.layout.activity_home_aso);
                defineAsoHomeUi();
            }
        }catch (NullPointerException ex){

        }

        dbHelper = new SqliteDbHelper(this);
    }

    private void defineDealerHomeUi(){
        dealerLogout = findViewById(R.id.logoutdealercardId);
        seeDealerInventory = findViewById(R.id.inventorycardId);
        updateDealerInventory = findViewById(R.id.inventoryupdatecardId);
        seeRoute              = findViewById(R.id.outlet_routelist_cardId);
        dbSync                = findViewById(R.id.dbsynccardId);
       // order                 = findViewById(R.id.dealerordercardId);

        dealerLogout.setOnClickListener(this);
        seeDealerInventory.setOnClickListener(this);
        updateDealerInventory.setOnClickListener(this);
        seeRoute.setOnClickListener(this);
        dbSync.setOnClickListener(this);
        //order.setOnClickListener(this);
    }

    private void definePullerHomeUi(){
        pullerLogout = findViewById(R.id.pullerlogoutcardId);
        pullerOutletSales = findViewById(R.id.outletsalescardId);
        pullerStatus      = findViewById(R.id.pullerstatuscardId);

        pullerLogout.setOnClickListener(this);
        pullerOutletSales.setOnClickListener(this);
        pullerStatus.setOnClickListener(this);
    }

    private void defineAsoHomeUi(){
        asoLogout = findViewById(R.id.asologoutcardId);
        issueProductToDealer = findViewById(R.id.insertproductcardId);
        asoSyncProducts      = findViewById(R.id.syncproductcardId);
        asoOrder             = findViewById(R.id.asoOrdercardId);

        asoOrder.setOnClickListener(this);
        asoLogout.setOnClickListener(this);
        issueProductToDealer.setOnClickListener(this);
        asoSyncProducts.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.pullerstatuscardId:
                i = new Intent(this,PullerSalesStatus.class);
                startActivity(i);
                finish();
                break;

            case R.id.inventoryupdatecardId:
                i = new Intent(this,ConfirmSalesToOutlets.class);
                startActivity(i);
                finish();
                break;

            case R.id.outlet_routelist_cardId:
                i = new Intent(this,RouteListOfOutlets.class);
                startActivity(i);
                finish();
                break;

            case R.id.logoutdealercardId:
                i = new Intent(this,MainActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.pullerlogoutcardId:
                i = new Intent(this,MainActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.asologoutcardId:
                i = new Intent(this,MainActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.inventorycardId:
                i = new Intent(this,DealerInventory.class);
                startActivity(i);
                finish();
                break;
            case R.id.insertproductcardId:
                i = new Intent(this,InsertProductToDealer.class);
                startActivity(i);
                finish();
                break;
            case R.id.outletsalescardId:
                i = new Intent(this,SaleProductToOutlet.class);
                startActivity(i);
                finish();
                break;
            case R.id.dbsynccardId:
//                boolean vpn = checkVPN();
//                if(vpn){
                    dbSync.setEnabled(false);
                    AsyncData task = new AsyncData();
                    task.execute();
//                }else {
//                    showAlert("Warning","Connect Vpn");
//                }
                break;
            case R.id.asoOrdercardId:
                i = new Intent(this,DealerOrder.class);
                startActivity(i);
                finish();
                break;

            case R.id.syncproductcardId:
                AsyncLoadProductList task1 = new AsyncLoadProductList();
                task1.execute();
                break;
            default:

        }
    }

    private final class AsyncData extends AsyncTask<String,String,String>{

        String result = "";
        @Override
        protected String doInBackground(String... strings) {

            deletePreviousData();
            result = syncPuller();
            if(result.equals("Sync Successful")) {
                result = syncDealerInventory();
            }
            return result;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s.equals("Sync Successful")){
                    //Toast.makeText(HomeActivity.this, "DBSync Successful", Toast.LENGTH_SHORT).show();
                    AsyncLoadRoute task = new AsyncLoadRoute();
                    task.execute();
                }else{
                    showAlert("Error",s);
                    dismissProgressDialog();
                }
            }catch (NullPointerException ex){
                showAlert("Error",ex.getMessage());
            }
        }
    }

    private String syncDealerInventory(){
        Connection connection = postgresqlConnection.getConn();
        ResultSet resultSet;
        String result = "";
        if(connection != null) {
            try {
                Statement statement = connection.createStatement();
                String sql_query = "select name,value,m_product_id,qty,unit,dateconfirm,m_product_category_id from t_dist_open_inventory where c_bpartner_id = '" + Integer.parseInt(userCode) + "' order by qty";
                resultSet = statement.executeQuery(sql_query);
                if (resultSet != null ) {
                    while (resultSet.next()) {

                        String productName = resultSet.getString("name");
                        String productCode = resultSet.getString("value");
                        int productId = resultSet.getInt("m_product_id");
                        int quantity = resultSet.getInt("qty");
                        String unit = resultSet.getString("unit");
                        String date = resultSet.getString("dateconfirm");
                        int categoryID = resultSet.getInt("m_product_category_id");
//               Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                String time = formatter.format(date);

                        boolean res = dbHelper.insertIntoDealerInventory(productName, productCode, productId, quantity, unit, date,categoryID);
                        //boolean res1 = dbHelper.insertIntoPullerInventory(productName,Integer.parseInt(quantity));
                        if (res) {
                            result = "Sync Successful";
                        } else {
                            result = "No no!!";
                        }

                    }
                }else{
                    result = "no inventory found tell aso to fill inventory";
                }

            } catch (SQLException ex) {
                result = ex.getMessage();
            } finally {
                try {
                    if(connection != null) {
                        connection.close();
                    }else{
                        result = "Connection Failed";
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }else{
            return result = "No Connection";
        }
    }

    private String syncPuller(){
        Connection connection = postgresqlConnection.getConn();
        ResultSet resultSet;
        String result = "";
       // try{
//            Statement statement = connection.createStatement();
//            String sql_query = "select puller_code from van_puller where dealer_code ='"+userCode+"'";
//            resultSet = statement.executeQuery(sql_query);
           // while(resultSet.next()){

               //  pullerCode = resultSet.getString("puller_code");
            if(connection != null) {
                try {
                    Statement statement1 = connection.createStatement();
                    ResultSet resultSet1;
                    String sql_query1 = "select value,username,password,target_outlet_number from t_vandetails where c_bpartner_id = '" + Integer.parseInt(userCode) + "'";
                    resultSet1 = statement1.executeQuery(sql_query1);
                    while (resultSet1.next()) {
                        String name = resultSet1.getString("username");
                        String password = resultSet1.getString("password");
                        String pullerCode = resultSet1.getString("value");
                        int targetOutletNumber = resultSet1.getInt("target_outlet_number");

                        boolean res = dbHelper.insertPuller(pullerCode, name, password,targetOutletNumber);
                        if (res) {
                           result = "Sync Successful";
                        }
                    }
                } catch (SQLException ex) {

                } finally {
                    try {
                        if(connection != null) {
                            connection.close();
                        }else{
                            result = "Connection failed";
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                return result;
            }else {
                return result = "No Connection";
            }

          //  }

//        }catch (SQLException ex){
//            toastMessage(ex.getMessage());
//        }finally {
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }


    private final class AsyncLoadRoute extends AsyncTask<String,String,String> {



        String result;
        @Override
        protected String doInBackground(String... strings) {
            //pullerInventorySync();
            routedataList.clear();
            result = getRouteListFromServer();

            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //dismissProgressDialog();
            try{
                if (s.equals("Success")) {
                    AsyncLoadOutlet task = new AsyncLoadOutlet();
                    task.execute();
                } else {
                    showAlert("Error",s);
                }

            }catch (NullPointerException ex){

            }
        }
    }


    private String getRouteListFromServer() {


        PostgresqlConnection postgresqlConnection;
        ResultSet resultSet;
        String result = "";
        postgresqlConnection = new PostgresqlConnection();
        Connection conn = postgresqlConnection.getConn();

        if(conn != null) {
            try {

                Statement statement = conn.createStatement();
                String sql_query = "select t_routdetails_id,name from t_routdetails where c_bpartner_id = '" + Integer.parseInt(userCode) + "'";

                resultSet = statement.executeQuery(sql_query);
                if (resultSet != null) {
                    while (resultSet.next()) {
                        int routeCode = resultSet.getInt("t_routdetails_id");
                        String routeName = resultSet.getString("name");
                        routedataList.add(routeCode);
                        dbHelper.insertIntoRoute(routeCode, routeName);
                    }
                    result = "Success";
                } else {
                    result = " No route found Please insert route in route table in server";
                }
            } catch (SQLException ex) {
                result = ex.getMessage();
            } finally {
                try {
                    if(conn != null) {
                        conn.close();
                    }else{
                        result = "Connection failed";
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }else{
            result = "No Connection";
        }
        return result;
    }

    private final class AsyncLoadOutlet extends AsyncTask<String,String,String>{

        String result = "";

        @Override
        protected String doInBackground(String... strings) {
            outletList.clear();
           result = getOutletFromServer();
           if(result.equals("Sync Successful")) {
               result = getOutletPriceFromServer();
           }
           return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
           // dismissProgressDialog();
            try{
                if(s.equals("Sync Successful")){
                    //Toast.makeText(HomeActivity.this, "Dealer DBSync Successful", Toast.LENGTH_SHORT).show();
                    AsyncLoadOutletDiscount task = new AsyncLoadOutletDiscount();
                    task.execute();
                   // dbSync.setEnabled(true);
                }else{
                    showAlert("Error",s);
                }
            }catch (NullPointerException ex){
                showAlert("Error",ex.getMessage());
            }
        }
    }


    private String getOutletFromServer(){
        PostgresqlConnection postgresqlConnection;
        ResultSet resultSet;
        String result = "";
        postgresqlConnection = new PostgresqlConnection();
        Connection conn = postgresqlConnection.getConn();
        if(conn != null) {
            try {

                Statement statement = conn.createStatement();
                for (int i = 0; i < routedataList.size(); i++) {
                    String sql_query = "select name,t_outlet_info_id,discountamt,amount from t_outlet_info where t_routdetails_id = '" + routedataList.get(i) + "'";

                    resultSet = statement.executeQuery(sql_query);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            String outletName = resultSet.getString("name");
                            int outletCode = resultSet.getInt("t_outlet_info_id");
                            double discount = resultSet.getDouble("discountamt");
                            double dealerOwnedFromOutlets = resultSet.getDouble("amount");
                            outletList.add(outletCode);

                            dbHelper.insertIntoOutlet(outletCode, outletName, routedataList.get(i), discount, dealerOwnedFromOutlets);
                        }
                        result = "Sync Successful";
                    } else {
                        result = "Nothing found in outlet Table";
                    }
                }

            } catch (SQLException ex) {
                result = ex.getMessage();
            } finally {
                try {
                    if(conn != null) {
                        conn.close();
                    }else{
                        result = "Connection failed";
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }else {
            result = "No Connection";
        }
        return result;
    }


    private String getOutletPriceFromServer(){
        PostgresqlConnection postgresqlConnection;
        ResultSet resultSet;
        String result = "";
        postgresqlConnection = new PostgresqlConnection();
        Connection conn = postgresqlConnection.getConn();
        if(conn != null) {
            try {

                Statement statement = conn.createStatement();
                for (int i = 0; i < outletList.size(); i++) {
                    String sql_query = "select m_product_id,price from t_outlet_price where t_outlet_info_id = '" + outletList.get(i) + "'";

                    resultSet = statement.executeQuery(sql_query);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            int productId = resultSet.getInt("m_product_id");
                            double productPrice = resultSet.getDouble("price");

                            dbHelper.insertIntoOutletProductPrice(outletList.get(i), productId, productPrice);
                        }
                        result = "Sync Successful";
                    } else {
                        result = "Nothing found in outlet Table";
                    }


                }

            } catch (SQLException ex) {
                result = ex.getMessage();
            } finally {
                try {
                    if(conn != null) {
                        conn.close();
                    }else{
                        result = "Connection Failed";
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return result;
        }else {
            result = "No Connection";
        }
        return result;
    }


    private final class AsyncLoadOutletDiscount extends AsyncTask<String,String,String>{

        String result = "";

        @Override
        protected String doInBackground(String... strings) {
            result = getOutletDiscountFromServer();
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissProgressDialog();
            try{
                if(s.equals("Sync Successful")){
                    Toast.makeText(HomeActivity.this, "Dealer DBSync Successful", Toast.LENGTH_SHORT).show();
                    dbSync.setEnabled(true);
                }else{
                    showAlert("Error",s);
                }
            }catch (NullPointerException ex){
                showAlert("Error",ex.getMessage());
            }
        }
    }


    private String getOutletDiscountFromServer(){
        PostgresqlConnection postgresqlConnection;
        ResultSet resultSet,resultSet1;
        String result = "";
        postgresqlConnection = new PostgresqlConnection();
        Connection conn = postgresqlConnection.getConn();
        if(conn != null) {
            try {

                Statement statement = conn.createStatement();
                Statement statement1 = conn.createStatement();
                for (int i = 0; i < outletList.size(); i++) {
                    String sql_query = "select t_discount_id from t_outlet_discount where t_outlet_info_id = '" + outletList.get(i) + "'";

                    resultSet = statement.executeQuery(sql_query);
                    if (resultSet != null) {
                        while (resultSet.next()) {
                            int discountID = resultSet.getInt("t_discount_id");

                            String getDiscountQuery = "select name,m_product_id,m_product_category_id,target_qty,target_purchase,rewd_productid,rewd_categoryid,rewdqty,cash_discount,percentage_discount,flat_discount from t_discount where t_discount_id = '"+discountID+"' ";
                            resultSet1 = statement1.executeQuery(getDiscountQuery);
                            if (resultSet1 != null) {
                                while (resultSet1.next()) {
                                    String discountName = resultSet1.getString("name");
                                    int targetProductID = resultSet1.getInt("m_product_id");
                                    int targetCategoryID = resultSet1.getInt("m_product_category_id");
                                    int targetQty               = resultSet1.getInt("target_qty");
                                    double targetPurchase       = resultSet1.getDouble("target_purchase");
                                    int rewardProductID         = resultSet1.getInt("rewd_productid");
                                    int rewardCategoryID = resultSet1.getInt("rewd_categoryid");
                                    int rewardQty               = resultSet1.getInt("rewdqty");
                                    double cashDiscount         = resultSet1.getDouble("cash_discount");
                                    double percentageDiscount   = resultSet1.getDouble("percentage_discount");
                                    double flatDiscount         = resultSet1.getDouble("flat_discount");

                                    dbHelper.insertIntoOutletDiscount(outletList.get(i),discountID,discountName,targetProductID,targetCategoryID,targetQty,targetPurchase,rewardProductID,rewardCategoryID,rewardQty,cashDiscount,percentageDiscount,flatDiscount);

                                }
                            }
                        }
                        result = "Sync Successful";
                    } else {
                        result = "Nothing found in outlet Table";
                    }
                }

            } catch (SQLException ex) {
                result = ex.getMessage();
            } finally {
                try {
                    if(conn != null) {
                        conn.close();
                    }else{
                        result = "Connection failed";
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }else {
            result = "No Connection";
        }
        return result;
    }


    private final class AsyncLoadProductList extends AsyncTask<String,String,String>{
        PostgresqlConnection postgresqlConnection;
        ResultSet resultSet;
        ArrayList<String> brandArrayList = new ArrayList<>();


        @Override
        protected String doInBackground(String... strings) {
            dbHelper.deleteProductList();
            postgresqlConnection = new PostgresqlConnection();
            Connection conn = postgresqlConnection.getConn();
            String result = "";
            if(conn != null) {
                try {
                    Statement statement = conn.createStatement();
                    String query = "select distinct split_part(group1,',',5) as brand from adempiere.m_product where split_part(group1,',',5) is not null";

                    resultSet = statement.executeQuery(query);
                    if (resultSet != null) {
                        while (resultSet.next()) {

                            String brand = resultSet.getString("brand");
                            //brandArrayList.add(brand);
                            Statement stmt = conn.createStatement();
                            String queryProduct = "select name,value,m_product_id,c_uom_id,split_part(group1,',',3) as pamount,m_product_category_id from m_product where split_part(group1,',',5) = '" + brand + "' order by name";
                            ResultSet resultSet1 = stmt.executeQuery(queryProduct);
                            while (resultSet1.next()) {
                                String productName = resultSet1.getString("name");
                                String product_code = resultSet1.getString("value");
                                int m_product_id = resultSet1.getInt("m_product_id");
                                int unit_id = resultSet1.getInt("c_uom_id");
                                String pAmount = resultSet1.getString("pamount");
                                int productCategory_id = resultSet1.getInt("m_product_category_id");
                                String unitName = "";
                                Statement stmt1 = conn.createStatement();
                                String queryUnit = "select uomsymbol from c_uom where c_uom_id = '" + unit_id + "'";
                                ResultSet resultSet2 = stmt1.executeQuery(queryUnit);
                                while (resultSet2.next()) {
                                    unitName = resultSet2.getString("uomsymbol");
                                }

                                Statement stmt2 = conn.createStatement();
                                String q1 = "select plv.m_pricelist_version_id from m_pricelist pl\n" +
                                        "join m_pricelist_version plv on pl.m_pricelist_id = plv.m_pricelist_id \n" +
                                        "where  plv.validfrom <=now() \n" +
                                        "and pl.name like '%Distributor PriceList%' order by plv.validfrom desc limit 1";
                                ResultSet resultSet3 = stmt2.executeQuery(q1);
                                while (resultSet3.next()) {
                                    int priceListId = resultSet3.getInt("m_pricelist_version_id");
                                    Statement stmt3 = conn.createStatement();
                                    String q2 = "select pricestd from m_productprice where m_pricelist_version_id = '" + priceListId + "' and m_product_id = '" + m_product_id + "'";
                                    ResultSet resultSet4 = stmt3.executeQuery(q2);
                                    while (resultSet4.next()) {
                                        double productPrice = resultSet4.getDouble("pricestd");
                                        dbHelper.insertIntoProductList(productName, product_code, brand, unitName, productPrice, m_product_id, pAmount,productCategory_id);
                                    }
                                }

                            }
                        }
                        result = "product sync done";
                    } else {
                        result = "nothing found in product Table";
                    }

                } catch (SQLException ex) {
                    result = ex.getMessage();
                } finally {
                    try {
                        if(conn != null) {
                            conn.close();
                        }else{
                            result = "Connection failed";
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                return result;
            }else{
                result = "No Connection";
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissProgressDialog();
            try{
                if(s.equals("product sync done")){
                    Toast.makeText(HomeActivity.this, "Aso DBSync Successfully Complete", Toast.LENGTH_SHORT).show();
                    //dbSync.setEnabled(true);
                }else{
                    showAlert("Error",s);
                }
            }catch (NullPointerException ex){
                showAlert("Error",ex.getMessage());
            }
        }
    }



//    private void pullerInventorySync(){
//
//            Cursor result = dbHelper.getAvailableProductAmount();
//            if(result.moveToFirst()){
//                String availableProduct  = result.getString(0);
//                String availableQuantity = result.getString(1);
//                dbHelper.insertIntoPullerInventory(availableProduct,Integer.parseInt(availableQuantity));
//
//            }
//
//    }

    private void deletePreviousData(){
        SqliteDbHelper dbHelper = new SqliteDbHelper(this);
        dbHelper.deletedealerInventory();
        dbHelper.deleteOutlet();
        dbHelper.deleteRoute();
        dbHelper.deletePuller();
        dbHelper.deletePullerInventory();
        dbHelper.deleteOutletProductPrice();
        dbHelper.deleteOutletCashCollection();
        dbHelper.deleteOutletDiscount();
    }


    private void toastMessage(String msg){
        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showAlert(String title,String message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(HomeActivity.this);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public boolean checkVPN(){


        List<String> networkList = new ArrayList<>();
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp())
                    networkList.add(networkInterface.getName());
            }
        } catch (Exception ex) {
            //Timber.d("isVpnUsing Network List didn't received");
        }

        return networkList.contains("tun0");
    }


    private void showProgressDialog(){
        if(pd == null){
            pd = new ProgressDialog(HomeActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("DBSync Loading.....");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        }
        pd.show();
    }

    private void dismissProgressDialog(){
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }
}
