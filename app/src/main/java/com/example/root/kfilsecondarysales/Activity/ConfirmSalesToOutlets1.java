//package com.example.root.kfilsecondarysales.Activity;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.CardView;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.widget.Toast;
//
//import com.example.root.kfilsecondarysales.Database.PostgresqlConnection;
//import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
//import com.example.root.kfilsecondarysales.R;
//
//import java.net.NetworkInterface;
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static com.example.root.kfilsecondarysales.Activity.LoginActivity.userCode;
//
//public class ConfirmSalesToOutlets1 extends AppCompatActivity {
//    CardView updateDealerInventory;
//    Toolbar toolbar;
//    ProgressDialog pd;
//
//    SqliteDbHelper dbHelper = new SqliteDbHelper(this);
//    List<String> productArrayList = new ArrayList<String>();
//    List<String> quantityArrayList = new ArrayList<String>();
//    List<String> routeNameArrayList = new ArrayList<String>();
//    List<String> outletNameArrayList = new ArrayList<String>();
//    List<String> outletCodeArrayList = new ArrayList<String>();
//    List<String> unitArrayList = new ArrayList<String>();
//    List<String> pullerCodeArrayList = new ArrayList<String>();
//    List<Double> priceArrayList = new ArrayList<Double>();
//    List<Double> outletAmtArrayList = new ArrayList<>();
//    List<Integer> outletCodeOfUpdatedAmountStatus = new ArrayList<>();
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_confirm_sales_to_outlets);
//
//        updateDealerInventory = findViewById(R.id.updateInventoryDealerCard);
//
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ConfirmSalesToOutlets1.this,HomeActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        loadAmountFromOutlet();
//        confirm();
//       // System.out.println(products[0]);
//        //System.out.println(quantityOfProducts[0]);
//    }
//
//    private void loadFromOutletSalesTable(){
//        Cursor result = dbHelper.getProductFromOutletSales();
//        int i = 0;
//        if(result.moveToFirst()){
//            do{
//                String routeName = result.getString(1);
//                String outletName = result.getString(2);
//                String outletCode = result.getString(3);
//                String product = result.getString(4);
//                String quantity = result.getString(5);
//                String unit     = result.getString(6);
//                String pullerCode = result.getString(7);
//                double price      = result.getDouble(9);
//                routeNameArrayList.add(i,routeName);
//                outletNameArrayList.add(i,outletName);
//                outletCodeArrayList.add(i,outletCode);
//                productArrayList.add(i,product);
//                quantityArrayList.add(i,quantity);
//                unitArrayList.add(i,unit);
//                pullerCodeArrayList.add(i,pullerCode);
//                priceArrayList.add(i,price);
//                i++;
//            }while(result.moveToNext());
//        }
//    }
//
//    private void confirm(){
//        updateDealerInventory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // Toast.makeText(ConfirmSalesToOutlets.this, "Confirm", Toast.LENGTH_SHORT).show();
//                boolean vpn = checkVPN();
//               // if(vpn){
//                    updateDealerInventory.setEnabled(false);
//                    AsyncSaveOutletSales task = new AsyncSaveOutletSales();
//                    task.execute();
//
////                }else {
////                    showAlert("Warning","Please Connect vpn");
////                }
//
//            }
//        });
//    }
//
//
//    private final class AsyncSaveOutletSales extends AsyncTask<String,String,String>{
//        PostgresqlConnection postgresqlConnection;
//        int resultSet;
//        String result = "";
//       // ProgressDialog pd;
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            showProgressDialog();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            try {
//                if(s.equals("Success")) {
//                    AsyncUpdateProductInventory task = new AsyncUpdateProductInventory();
//                    task.execute();
//                }else {
//                    showAlert("Error",s);
//                    dismissProgressDialog();
//                }
//            }catch (NullPointerException ex){
//
//            }
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//            loadFromOutletSalesTable();
//
//            postgresqlConnection = new PostgresqlConnection();
//            Connection conn = postgresqlConnection.getConn();
//            if(conn != null) {
//                if (!productArrayList.isEmpty()) {
//                    try {
//                        //int p_quantity = Integer.parseInt(product_quantity);
//
//                            for (int i = 0; i < routeNameArrayList.size(); i++) {
//
//                                    Statement stmt = conn.createStatement();
//                                    String sql_pKey = "SELECT t_outlet_sales_id FROM t_outlet_sales ORDER BY t_outlet_sales_id DESC limit 1";
//
//                                    ResultSet resultSetPkey = stmt.executeQuery(sql_pKey);
//                                    if(resultSetPkey != null) {
//                                        if(resultSetPkey.next()) {
//                                            Statement statement = conn.createStatement();
//                                            int pKey = resultSetPkey.getInt("t_outlet_sales_id");
//                                             String sql_query = "INSERT INTO t_outlet_sales\n" +
//                                            " (ad_client_id,ad_org_id,created,createdby,name,outlet_name,t_outlet_sales_id,updated,updatedby,value,rout_name,t_outlet_info_id,productname,qty,unit,pullercode,c_bpartner_id,price)  \n" +
//                                            "VALUES (1000000,1000000,now(),1000000,'OutSale','" + outletNameArrayList.get(i) + "','" + (pKey+1) + "',now(),'" + Integer.parseInt(userCode) + "','sale','" + routeNameArrayList.get(i) + "','" + outletCodeArrayList.get(i) + "','" + productArrayList.get(i) + "','" + Integer.parseInt(quantityArrayList.get(i)) + "','" + unitArrayList.get(i) + "','" + pullerCodeArrayList.get(i) + "','" + Integer.parseInt(userCode) + "','"+priceArrayList.get(i)+"'); ";
//
//                                             resultSet = statement.executeUpdate(sql_query);
//
//                                        }else{
//                                            Statement statement = conn.createStatement();
//                                            String sql_query = "INSERT INTO t_outlet_sales\n" +
//                                                        " (ad_client_id,ad_org_id,created,createdby,name,outlet_name,t_outlet_sales_id,updated,updatedby,value,rout_name,t_outlet_info_id,productname,qty,unit,pullercode,c_bpartner_id,price)  \n" +
//                                                        "VALUES (1000000,1000000,now(),1000000,'OutSale','" + outletNameArrayList.get(i) + "','" + 1 + "',now(),'" + Integer.parseInt(userCode) + "','sale','" + routeNameArrayList.get(i) + "','" + outletCodeArrayList.get(i) + "','" + productArrayList.get(i) + "','" + Integer.parseInt(quantityArrayList.get(i)) + "','" + unitArrayList.get(i) + "','" + pullerCodeArrayList.get(i) + "','" + Integer.parseInt(userCode) + "','"+priceArrayList.get(i)+"'); ";
//
//                                            resultSet = statement.executeUpdate(sql_query);
//                                        }
//                                    }else {
//                                         Toast.makeText(ConfirmSalesToOutlets1.this, "No Table t_outlet_sales found", Toast.LENGTH_SHORT).show();
//                                    }
//                            }
//
//                            if (resultSet == 1) {
//                                return result = "Success";
//                            } else {
//                                return result = "Failed";
//                            }
//                    } catch (SQLException ex) {
//                        result = ex.getMessage();
//                        //showAlert("Error", error);
//                    } finally {
//                        try {
//                            if(conn != null) {
//                                conn.close();
//                            }else{
//                                result = "Connection Failed";
//                            }
//                        } catch (SQLException e) {
//                            result = e.getMessage();
//                        }
//                    }
//
//                    return result;
//                }else{
//                    return result = "Empty Sales List";
//                }
//            }
//            else {
//                return result = "No Connection";
//            }
//        }
//
//    }
//
//
//
//
//
//    private final class AsyncUpdateProductInventory extends AsyncTask<String,String,String>{
//        String result = "";
//        @Override
//        protected String doInBackground(String... strings) {
//            try{
//                if(!productArrayList.isEmpty()) {
//                    result = updateMethod();
//                    return result;
//                }else{
//                    result = "Empty Sales List";
//                }
//            }catch (NullPointerException ex){
//                result  = "No products sold today";
//            }
//
//
//            return result;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            dismissProgressDialog();
//            try{
//                if(s.equals("Success")){
//                    //Toast.makeText(ConfirmSalesToOutlets.this,s, Toast.LENGTH_SHORT).show();
//                    updateDealerInventory.setEnabled(true);
//                    dbHelper.deleteSalesProducts();
//                    routeNameArrayList.clear();
//                    outletNameArrayList.clear();
//                    outletCodeArrayList.clear();
//                    productArrayList.clear();
//                    quantityArrayList.clear();
//                    unitArrayList.clear();
//                    pullerCodeArrayList.clear();
//
//                    AsyncUpdateOutletBalance task = new AsyncUpdateOutletBalance();
//                    task.execute();
//                }else {
//                    showAlert("Connection",s);
//                }
//            }catch (NullPointerException ex){
//                showAlert("Update Error",ex.getMessage());
//            }
//        }
//    }
//
//    private String updateMethod(){
//        ResultSet resultSet;
//        String quantity = "";
//        PostgresqlConnection postgresqlConnection = new PostgresqlConnection();
//        Connection conn = postgresqlConnection.getConn();
//        boolean isUpdated = false;
//        String updateResult = "";
//        if(conn != null) {
//            try {
//
//                Statement statement = conn.createStatement();
//                for (int i = 0; i < productArrayList.size(); i++) {
//                    String sql_query = "select qty from t_dist_open_inventory where name ='" + productArrayList.get(i) + "'  and c_bpartner_id = '" + Integer.parseInt(userCode) + "'";
//                    resultSet = statement.executeQuery(sql_query);
//                    while (resultSet.next()) {
//
//
//                        quantity = resultSet.getString("qty");
//                        isUpdated = updateQuantity(quantity, i);
////                result = "Success";
//
//                    }
//                }
//                if (isUpdated) {
//                    updateResult = "Success";
//                } else {
//                    updateResult = "Failed";
//                }
//
//            } catch (SQLException ex) {
//                //result = ex.getMessage();
//            } finally {
//                try {
//                    if(conn != null) {
//                        conn.close();
//                    }else{
//                        updateResult = "Connection Failed";
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }else {
//            updateResult = "No Connection";
//        }
//        return updateResult;
//    }
//
//    private boolean updateQuantity(String stockQuantity,int i){
//        int remainingQuantity = Integer.parseInt(stockQuantity) - Integer.parseInt(quantityArrayList.get(i));
//        PostgresqlConnection postgresqlConnection1 = new PostgresqlConnection();
//        Connection conn1 = postgresqlConnection1.getConn();
//        int resultSet;
//        boolean isUpdated = false;
//        if(conn1 != null) {
//            try {
//
//                Statement statement = conn1.createStatement();
//                String sql_query = "UPDATE t_dist_open_inventory set qty = '" + Integer.toString(remainingQuantity) + "' where name = '" + productArrayList.get(i) + "' and c_bpartner_id = '" + Integer.parseInt(userCode) + "'";
//
//
//                resultSet = statement.executeUpdate(sql_query);
//                if (resultSet != 0) {
//                    isUpdated = true;
//                } else {
//                    isUpdated = false;
//                }
//            } catch (SQLException ex) {
//                //result = ex.getMessage();
//            } finally {
//                try {
//                    if(conn1 !=null) {
//                        conn1.close();
//                    }else {
//                       // showAlert("Connection","connection Closed");
//                        isUpdated = false;
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            return isUpdated;
//        }else {
//            isUpdated = false;
//        }
//        return isUpdated;
//    }
//
//
//    private final class AsyncUpdateOutletBalance extends AsyncTask<String,String,String>{
//        String result = "";
//        @Override
//        protected String doInBackground(String... strings) {
//            result = updateAmount();
//            return result;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            dismissProgressDialog();
//            try{
//                if(s.equals("Success")){
//
//                    Toast.makeText(ConfirmSalesToOutlets1.this, "All Update Successful", Toast.LENGTH_SHORT).show();
//                }else {
//                    showAlert("Connection",s);
//                }
//            }catch (NullPointerException ex){
//                showAlert("Update Error",ex.getMessage());
//            }
//        }
//    }
//
//    private void loadAmountFromOutlet(){
//        Cursor result = dbHelper.getOutletAmount();
//        int i = 0;
//        if(result.moveToFirst()){
//            do{
//                int outlet_code = result.getInt(0);
//                double outletAmount      = result.getDouble(1);
//                outletCodeOfUpdatedAmountStatus.add(i,outlet_code);
//                outletAmtArrayList.add(i,outletAmount);
//                i++;
//            }while(result.moveToNext());
//        }
//    }
//
//    private String updateAmount(){
//        int resultSet;
//        PostgresqlConnection postgresqlConnection = new PostgresqlConnection();
//        Connection conn = postgresqlConnection.getConn();
//        String updateResult = "";
//        if(conn != null) {
//            try {
//
//                Statement statement = conn.createStatement();
//                for (int i = 0; i < outletCodeOfUpdatedAmountStatus.size(); i++) {
//                    String sql_query = "UPDATE t_outlet_info set amount = '" + outletAmtArrayList.get(i) + "' where t_outlet_info_id = '" + outletCodeOfUpdatedAmountStatus.get(i) + "'";
//
//
//                    resultSet = statement.executeUpdate(sql_query);
//                    if (resultSet != 0) {
//                        boolean res = dbHelper.updateOutletBalanceStatus(String.valueOf(outletCodeOfUpdatedAmountStatus.get(i)));
//                        if(res) {
//                            updateResult = "Success";
//                        }else {
//                            updateResult = "Failed";
//                        }
//
//                    } else {
//                        updateResult = "Failed";
//                    }
//
//                }
//            } catch (SQLException ex) {
//                //result = ex.getMessage();
//            } finally {
//                try {
//                    if(conn != null) {
//                        conn.close();
//                    }else{
//                        updateResult = "Connection Failed";
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }else {
//            updateResult = "No Connection";
//        }
//        return updateResult;
//    }
//
//    public void showAlert(String title,String message){
//        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ConfirmSalesToOutlets1.this);
//
//        dlgAlert.setMessage(message);
//        dlgAlert.setTitle(title);
//        dlgAlert.setPositiveButton("OK", null);
//        dlgAlert.setCancelable(true);
//        dlgAlert.create().show();
//    }
//
//
//    public boolean checkVPN(){
//
//
//        List<String> networkList = new ArrayList<>();
//        try {
//            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
//                if (networkInterface.isUp())
//                    networkList.add(networkInterface.getName());
//            }
//        } catch (Exception ex) {
//            //Timber.d("isVpnUsing Network List didn't received");
//        }
//
//        return networkList.contains("tun0");
//    }
//
//
//    private void showProgressDialog(){
//        if(pd == null){
//            pd = new ProgressDialog(ConfirmSalesToOutlets1.this);
//            pd.setTitle("Please Wait");
//            pd.setMessage("Sales&Inventory Update going.....");
//            pd.setCancelable(false);
//            pd.setIndeterminate(true);
//            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        }
//        pd.show();
//    }
//
//    private void dismissProgressDialog(){
//        if (pd != null && pd.isShowing()) {
//            pd.dismiss();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        dismissProgressDialog();
//        super.onDestroy();
//    }
//}
