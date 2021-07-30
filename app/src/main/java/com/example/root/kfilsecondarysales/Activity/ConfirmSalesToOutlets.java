package com.example.root.kfilsecondarysales.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.root.kfilsecondarysales.Database.PostgresqlConnection;
import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
import com.example.root.kfilsecondarysales.Modal.OutletReceivedDiscount;
import com.example.root.kfilsecondarysales.Modal.ProductModal;
import com.example.root.kfilsecondarysales.R;

import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.root.kfilsecondarysales.Activity.LoginActivity.userCode;

public class ConfirmSalesToOutlets extends AppCompatActivity {
    CardView updateDealerInventory;
    Toolbar toolbar;
    ProgressDialog pd;

    SqliteDbHelper dbHelper = new SqliteDbHelper(this);
    List<OutletReceivedDiscount> outletReceivedDiscountsArrayList = new ArrayList<>();

    List<String> productArrayList = new ArrayList<String>();
    List<String> quantityArrayList = new ArrayList<String>();
    List<String> routeNameArrayList = new ArrayList<String>();
    List<String> outletNameArrayList = new ArrayList<String>();
    List<Integer> outletCodeArrayList = new ArrayList<Integer>();
    List<String> unitArrayList = new ArrayList<String>();
    List<String> pullerCodeArrayList = new ArrayList<String>();
    List<Double> priceArrayList = new ArrayList<Double>();
    List<Double> outletAmtArrayList = new ArrayList<>();
    List<Integer> outletCodeOfUpdatedAmountStatus = new ArrayList<>();
    List<Integer> productIdArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_sales_to_outlets);

        updateDealerInventory = findViewById(R.id.updateInventoryDealerCard);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmSalesToOutlets.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loadProductIdFromInventory();
        loadAmountFromOutlet();
        confirm();
       // System.out.println(products[0]);
        //System.out.println(quantityOfProducts[0]);
    }



    private void loadProductIdFromInventory(){
        Cursor result = dbHelper.getProductIdFromInventory();
        int i = 0;
        if(result.moveToFirst()){
            do{
                int productID = result.getInt(0);
                productIdArrayList.add(i,productID);
            }while(result.moveToNext());
        }
    }

    private void loadAmountFromOutlet(){
        Cursor result = dbHelper.getOutletAmount();
        int i = 0;
        if(result.moveToFirst()){
            do{
                int outlet_code = result.getInt(0);
                double outletAmount      = result.getDouble(1);
                outletCodeOfUpdatedAmountStatus.add(i,outlet_code);
                outletAmtArrayList.add(i,outletAmount);
                i++;
            }while(result.moveToNext());
        }
    }

    private void loadFromOutletSalesTable(){
        Cursor result = dbHelper.getProductFromOutletSales();
        int i = 0;
        if(result.moveToFirst()){
            do{
                String routeName = result.getString(1);
                String outletName = result.getString(2);
                int outletCode = result.getInt(3);
                String product = result.getString(4);
                String quantity = result.getString(5);
                String unit     = result.getString(6);
                String pullerCode = result.getString(7);
                double price      = result.getDouble(9);
               // int productID     = result.getInt(11);
                routeNameArrayList.add(i,routeName);
                outletNameArrayList.add(i,outletName);
                outletCodeArrayList.add(i,outletCode);
                productArrayList.add(i,product);
                quantityArrayList.add(i,quantity);
                unitArrayList.add(i,unit);
                pullerCodeArrayList.add(i,pullerCode);
                priceArrayList.add(i,price);
              //  productIdArrayList.add(i,productID);
                i++;
            }while(result.moveToNext());
        }
    }

    private  void loadReceivedDiscount(){
        Cursor result = dbHelper.getReceivedDiscount();
       // int i = 0;
        if(result.moveToFirst()){
            do{
                int outlet_id = result.getInt(1);
                int discount_id = result.getInt(2);
                int reward_product_id = result.getInt(3);
                int reward_qty = result.getInt(4);
                double cash_discount = result.getDouble(5);
                double percentage_discount     = result.getDouble(6);
                double flat_discount = result.getDouble(7);

                OutletReceivedDiscount  discount = new OutletReceivedDiscount(outlet_id,discount_id,reward_product_id,reward_qty,cash_discount,percentage_discount,flat_discount);
                outletReceivedDiscountsArrayList.add(discount);
                //i++;
            }while(result.moveToNext());
        }
    }


    private void confirm(){
        updateDealerInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(ConfirmSalesToOutlets.this, "Confirm", Toast.LENGTH_SHORT).show();
                boolean vpn = checkVPN();
               // if(vpn){
                    updateDealerInventory.setEnabled(false);
                    AsyncSaveOutletSales task = new AsyncSaveOutletSales();
                    task.execute();

//                }else {
//                    showAlert("Warning","Please Connect vpn");
//                }

            }
        });
    }


    private final class AsyncSaveOutletSales extends AsyncTask<String,String,String>{
        PostgresqlConnection postgresqlConnection;
        //int resultSet;
        String result = "";
       // ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if(s.equals("Success")) {
                    updateDealerInventory.setEnabled(true);
                    dbHelper.deleteSalesProducts();
                    dbHelper.deleteReceivedDiscount();
                    routeNameArrayList.clear();
                    outletNameArrayList.clear();
                    outletCodeArrayList.clear();
                    productArrayList.clear();
                    quantityArrayList.clear();
                    unitArrayList.clear();
                    pullerCodeArrayList.clear();
                    outletReceivedDiscountsArrayList.clear();
                    productIdArrayList.clear();
                    dismissProgressDialog();
                    Toast.makeText(ConfirmSalesToOutlets.this, "All Update Successfull", Toast.LENGTH_SHORT).show();

                }else {
                    showAlert("Error",s);
                    dismissProgressDialog();
                }
            }catch (NullPointerException ex){

            }
        }

        @Override
        protected String doInBackground(String... strings) {
            loadReceivedDiscount();
            loadFromOutletSalesTable();

            postgresqlConnection = new PostgresqlConnection();
            Connection conn = postgresqlConnection.getConn();
            boolean isCommit = true;
            if(conn != null) {
                if (!productArrayList.isEmpty()) {
                    try {
                        //int p_quantity = Integer.parseInt(product_quantity);
                            conn.setAutoCommit(false);
                            for (int i = 0; i < routeNameArrayList.size(); i++) {

                                    int resultSet;
                                    Statement stmt = conn.createStatement();
                                    String sql_pKey = "SELECT t_outlet_sales_id FROM t_outlet_sales ORDER BY t_outlet_sales_id DESC limit 1";

                                    ResultSet resultSetPkey = stmt.executeQuery(sql_pKey);
                                    if(resultSetPkey != null) {
                                        if(resultSetPkey.next()) {
                                            Statement statement = conn.createStatement();
                                            int pKey = resultSetPkey.getInt("t_outlet_sales_id");
                                             String sql_query = "INSERT INTO t_outlet_sales\n" +
                                            " (ad_client_id,ad_org_id,created,createdby,name,outlet_name,t_outlet_sales_id,updated,updatedby,value,rout_name,t_outlet_info_id,productname,qty,unit,pullercode,c_bpartner_id,price)  \n" +
                                            "VALUES (1000000,1000000,now(),1000000,'OutSale','" + outletNameArrayList.get(i) + "','" + (pKey+1) + "',now(),'" + Integer.parseInt(userCode) + "','sale','" + routeNameArrayList.get(i) + "','" + outletCodeArrayList.get(i) + "','" + productArrayList.get(i) + "','" + Integer.parseInt(quantityArrayList.get(i)) + "','" + unitArrayList.get(i) + "','" + pullerCodeArrayList.get(i) + "','" + Integer.parseInt(userCode) + "','"+priceArrayList.get(i)+"'); ";

                                            resultSet = statement.executeUpdate(sql_query);
                                                if (resultSet == 1) {
                                                    result = "Success";
                                                } else {
                                                    result = "Failed";
                                                    isCommit = false;
                                                }

                                        }else{
                                            Statement statement = conn.createStatement();
                                            String sql_query = "INSERT INTO t_outlet_sales\n" +
                                                        " (ad_client_id,ad_org_id,created,createdby,name,outlet_name,t_outlet_sales_id,updated,updatedby,value,rout_name,t_outlet_info_id,productname,qty,unit,pullercode,c_bpartner_id,price)  \n" +
                                                        "VALUES (1000000,1000000,now(),1000000,'OutSale','" + outletNameArrayList.get(i) + "','" + 1 + "',now(),'" + Integer.parseInt(userCode) + "','sale','" + routeNameArrayList.get(i) + "','" + outletCodeArrayList.get(i) + "','" + productArrayList.get(i) + "','" + Integer.parseInt(quantityArrayList.get(i)) + "','" + unitArrayList.get(i) + "','" + pullerCodeArrayList.get(i) + "','" + Integer.parseInt(userCode) + "','"+priceArrayList.get(i)+"'); ";

                                            resultSet = statement.executeUpdate(sql_query);
                                                if (resultSet == 1) {
                                                    result = "Success";
                                                } else {
                                                    result = "Failed";
                                                    isCommit = false;
                                                }
                                        }
                                    }else {
                                         Toast.makeText(ConfirmSalesToOutlets.this, "No Table t_outlet_sales found", Toast.LENGTH_SHORT).show();
                                    }
                            }


                            int quantity = 0;
                            for (int i = 0; i < productArrayList.size(); i++) {
                                Statement statement1 = conn.createStatement();
                                String sql_query1 = "select sum(qty) as t_qty from t_dist_open_inventory where name ='" + productArrayList.get(i) + "'  and c_bpartner_id = '" + Integer.parseInt(userCode) + "'";
                                ResultSet resultSet1 = statement1.executeQuery(sql_query1);
                                while (resultSet1.next()) {
                                    quantity = resultSet1.getInt("t_qty");
                                    int remainingQuantity = quantity - Integer.parseInt(quantityArrayList.get(i));

                                    for(int j = 0; j < productIdArrayList.size();j++) {
                                        Cursor rwdQtyResult = dbHelper.getRewardQty(productIdArrayList.get(j), outletCodeArrayList.get(i));
                                        if (rwdQtyResult.moveToFirst()) {
                                            do {
                                                int rewardQty = rwdQtyResult.getInt(0);
                                                remainingQuantity = remainingQuantity - rewardQty;
                                            } while (rwdQtyResult.moveToNext());
                                        }
                                    }

                                    int resultSetUpdate;
                                    Statement statement2 = conn.createStatement();
                                    String sql_query2 = "UPDATE t_dist_open_inventory set qty = '" + Integer.toString(remainingQuantity) + "' where name = '" + productArrayList.get(i) + "' and c_bpartner_id = '" + Integer.parseInt(userCode) + "'";
                                    resultSetUpdate = statement2.executeUpdate(sql_query2);
                                    if(resultSetUpdate != 0){
                                        result = "Success";
                                    }else{
                                        result = "Failed";
                                        isCommit = false;
                                    }
                                }
                            }

                            for (int i = 0; i < outletCodeOfUpdatedAmountStatus.size(); i++) {
                                Statement statement3 = conn.createStatement();
                                String sql_query3 = "UPDATE t_outlet_info set amount = '" + outletAmtArrayList.get(i) + "' where t_outlet_info_id = '" + outletCodeOfUpdatedAmountStatus.get(i) + "'";

                                int resultSet3 = statement3.executeUpdate(sql_query3);
                                if (resultSet3 != 0) {
                                    boolean res = dbHelper.updateOutletBalanceStatus(String.valueOf(outletCodeOfUpdatedAmountStatus.get(i)));
                                    if(res) {
                                        result = "Success";
                                    }else {
                                        result = "Failed";
                                        isCommit = false;
                                    }

                                } else {
                                    result = "Failed";
                                    isCommit = false;
                                }

                            }


                            for(OutletReceivedDiscount modalDiscount : outletReceivedDiscountsArrayList){
                                Statement statement4 = conn.createStatement();
                                String sql_pKey = "SELECT t_receiveddiscount_id FROM t_receiveddiscount ORDER BY t_receiveddiscount_id DESC limit 1";
                                ResultSet resultSetPkey = statement4.executeQuery(sql_pKey);

                                if(resultSetPkey != null){
                                    if(resultSetPkey.next()){
                                        Statement statement5 = conn.createStatement();
                                        int pKey = resultSetPkey.getInt("t_receiveddiscount_id");

                                        String sql_query = "INSERT INTO t_receiveddiscount\n" +
                                                " (ad_client_id,ad_org_id,created,createdby,name,t_receiveddiscount_id,updated,updatedby,value,t_outlet_info_id,t_discount_id,m_product_id,reward_qty,flat_discount,percentage_discount,cash_discount)  \n" +
                                                "VALUES (1000000,1000000,now(),'"+Integer.parseInt(userCode)+"','ReceivedDiscount','" + (pKey+1) + "',now(),'" + Integer.parseInt(userCode) + "','saleDiscount','" + modalDiscount.getOutlet_id() + "','" + modalDiscount.getDiscount_id() + "','" + modalDiscount.getReward_product_id() + "','" + modalDiscount.getReward_qty() + "','" + modalDiscount.getFlat_discount() + "','" + modalDiscount.getPercentage_discount() + "','"+modalDiscount.getCash_discount()+"'); ";

                                        int resultSet = statement5.executeUpdate(sql_query);
                                        if (resultSet == 1) {
                                            result = "Success";
                                        } else {
                                            result = "Failed";
                                            isCommit = false;
                                        }
                                    }else{
                                        Statement statement5 = conn.createStatement();
                                        String sql_query = "INSERT INTO t_receiveddiscount\n" +
                                                " (ad_client_id,ad_org_id,created,createdby,name,t_receiveddiscount_id,updated,updatedby,value,t_outlet_info_id,t_discount_id,m_product_id,reward_qty,flat_discount,percentage_discount,cash_discount)  \n" +
                                                "VALUES (1000000,1000000,now(),'"+Integer.parseInt(userCode)+"','ReceivedDiscount','" + 1 + "',now(),'" + Integer.parseInt(userCode) + "','saleDiscount','" + modalDiscount.getOutlet_id() + "','" + modalDiscount.getDiscount_id() + "','" + modalDiscount.getReward_product_id() + "','" + modalDiscount.getReward_qty() + "','" + modalDiscount.getFlat_discount() + "','" + modalDiscount.getPercentage_discount() + "','"+modalDiscount.getCash_discount()+"'); ";

                                        int resultSet = statement5.executeUpdate(sql_query);
                                        if (resultSet == 1) {
                                            result = "Success";
                                        } else {
                                            result = "Failed";
                                            isCommit = false;
                                        }
                                    }

                                }else{
                                    Toast.makeText(ConfirmSalesToOutlets.this, "No Table t_receiveddiscount found", Toast.LENGTH_SHORT).show();
                                    isCommit = false;
                                }
                            }


                            if( isCommit){
                                conn.commit();
                            }
                            else{
                                try {
                                    conn.rollback();
                                } catch (SQLException e) {
                                    conn.rollback();
                                }
                            }
                    } catch (SQLException ex) {
                        result = ex.getMessage();
                        try{
                            if(conn != null){
                                conn.rollback();
                            }
                        }catch (SQLException ex2){
                            ex2.printStackTrace();
                        }
                    } finally {
                        try {
                            if(conn != null) {
                                conn.close();
                            }else{
                                result = "Connection Failed";
                            }
                        } catch (SQLException e) {
                            result = e.getMessage();
                        }
                    }

                    return result;
                }else{
                    return result = "Empty Sales List";
                }
            } else {
                return result = "No Connection";
            }
        }

    }

    public void showAlert(String title,String message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ConfirmSalesToOutlets.this);

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
            pd = new ProgressDialog(ConfirmSalesToOutlets.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Sales&Inventory Update going.....");
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
