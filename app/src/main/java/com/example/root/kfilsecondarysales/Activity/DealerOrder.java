package com.example.root.kfilsecondarysales.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.root.kfilsecondarysales.Adapter.DealerOrderOfProductRecyclerViewAdapter;
import com.example.root.kfilsecondarysales.Database.PostgresqlConnection;
import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
import com.example.root.kfilsecondarysales.Modal.OrderHeaderModal;
import com.example.root.kfilsecondarysales.Modal.OrderModal;
import com.example.root.kfilsecondarysales.Modal.ProductModalOfOrderActivity;
import com.example.root.kfilsecondarysales.R;

import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.example.root.kfilsecondarysales.Activity.LoginActivity.userCode;
public class DealerOrder extends AppCompatActivity {

    ProgressDialog pd;
    android.support.v7.widget.Toolbar toolbar;
    Spinner brandSpinner,dealerSpinner;
    RecyclerView orderRecyclerView;

    double dealerbalance = 0;
    double netRemBalance = 0;
    DealerOrderOfProductRecyclerViewAdapter adapter;
    Button orderAddBtn,orderConfirmBtn,sendServer;
    TextView totalOrderText,dealerBalanceText,remainingDealerbalanceText;
    SqliteDbHelper sqliteDbHelper;


    String brand_name,dealerCode,product_name,quantity,product_code,unit;
    double grandTotal = 0;
    List<String> brandList = new ArrayList<String>();
    List<ProductModalOfOrderActivity> productList = new ArrayList<ProductModalOfOrderActivity>();
    List<ProductModalOfOrderActivity> checkedProductList = new ArrayList<ProductModalOfOrderActivity>();
    List<OrderModal> orderModalArrayList = new ArrayList<>();
    List<OrderHeaderModal> orderHeaderModalArrayList = new ArrayList<>();
    ArrayList<String> dealerArrayList = new ArrayList<>();

    HashMap<String, Double> balanceHashMap = new HashMap<String, Double>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_order_new);

        sqliteDbHelper = new SqliteDbHelper(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DealerOrder.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        brandSpinner    = findViewById(R.id.brand_list_spinner_order);
        dealerSpinner   = findViewById(R.id.dealer_list_spinner_order);

        orderRecyclerView = findViewById(R.id.data_table_recyclerview_order);
        orderAddBtn     = findViewById(R.id.add_btn_dlr);
        //orderConfirmBtn    = findViewById(R.id.view_btn_dlr);
        sendServer         = findViewById(R.id.send_to_server_btn_order);

        totalOrderText  = findViewById(R.id.total_purchase);
        dealerBalanceText = findViewById(R.id.dealer_balance);
        remainingDealerbalanceText = findViewById(R.id.remaining_dealer_balance);

        sendServer.setEnabled(false);
        sendServer.setVisibility(View.INVISIBLE);

        AsyncLoadDealer task = new AsyncLoadDealer();
        task.execute();

        AsyncLoadBrand task1 = new AsyncLoadBrand();
        task1.execute();
        addOrder();
    }

    private void getSelectedDealerSpinner(){

        dealerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    dealerCode = parent.getSelectedItem().toString();
                    dealerbalance = balanceHashMap.get(dealerCode);
                    dealerBalanceText.setText(String.valueOf(dealerbalance));
                }catch (Exception ex){

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getSelectedSpinnerItem(){

            brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        brand_name = parent.getSelectedItem().toString();
                        AsyncLoadProduct task = new AsyncLoadProduct();
                        task.execute(brand_name);
                    }catch (Exception ex){

                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

    }


    private final class AsyncLoadDealer extends AsyncTask<String,String,String>{

        ResultSet resultSet;
        //String result = "";
        @Override
        protected String doInBackground(String... strings) {
            Cursor cursor = sqliteDbHelper.getDealer();
            String result = "";
            if(cursor.moveToFirst()){
                do{
                    String dealerCode = cursor.getString(0);
                    double balance    = cursor.getDouble(1);
                    balanceHashMap.put(dealerCode,balance);
                    dealerArrayList.add(dealerCode);
                }while(cursor.moveToNext());
                result = "dealer synced";
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s.equals("dealer synced")){
                    loadDealerSpinner();
                    getSelectedDealerSpinner();

                }else{
                    showAlert("Error","Check Connection");
                }
            }catch (NullPointerException ex){

            }
        }
    }

    private final class AsyncLoadBrand extends AsyncTask<String,String,String> {


        @Override
        protected String doInBackground(String... strings) {

            Cursor cursor = sqliteDbHelper.getBrandList();
            String result = "";
            if(cursor.moveToFirst()){
                do{
                    String brandName = cursor.getString(0);
                    brandList.add(brandName);
                }while(cursor.moveToNext());
                result = "Success";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s.equals("Success")){
                   // Toast.makeText(DealerOrder.this, "", Toast.LENGTH_SHORT).show();
                    loadBrandSpinner(brandList);
                    getSelectedSpinnerItem();
                }
            }catch (NullPointerException ex){
                Toast.makeText(DealerOrder.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void loadBrandSpinner(List<String> bList){
        brandSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
        //System.out.println(routeList);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.color_spinner_layout,bList);

        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        brandSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
    }

    private void loadDealerSpinner(){

        dealerSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
        //System.out.println(routeList);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.color_spinner_layout,dealerArrayList);

        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        dealerSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();

    }

    private final class AsyncLoadProduct extends AsyncTask<String,String,String> {


        @Override
        protected String doInBackground(String... strings) {
            //productHash.clear();
            productList.clear();
            Cursor cursor = sqliteDbHelper.getProductList(strings[0]);
            String result = "";
            if(cursor.moveToFirst()){
                do{
                    String productName = cursor.getString(0);
                    String productCode = cursor.getString(1);
                    String unit        = cursor.getString(2);
                    String price       = cursor.getString(3);
                    int product_id     = cursor.getInt(4);
                    //productHash.put(productName,productCode);

                    ProductModalOfOrderActivity productModalOfOrderActivity = new ProductModalOfOrderActivity(productName,productCode,unit,price,product_id);
                    productList.add(productModalOfOrderActivity);
                }while(cursor.moveToNext());
                result = "Success";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s.equals("Success")){
                     Toast.makeText(DealerOrder.this, s, Toast.LENGTH_SHORT).show();
                     initRecyclerView();
                }
            }catch (NullPointerException ex){
                Toast.makeText(DealerOrder.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        orderRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new DealerOrderOfProductRecyclerViewAdapter(DealerOrder.this,productList);
        orderRecyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu,menu);
        MenuItem mySearch = menu.findItem(R.id.search_view);

        SearchView searchView = (SearchView) mySearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(TextUtils.isEmpty(newText)){
                    adapter.searchFilter("");
                    orderRecyclerView.removeAllViews();
                    initRecyclerView();
                }else{
                    adapter.searchFilter(newText);
                }
                return true;
            }
        });

        return true;
    }

    private void addOrder(){


        orderAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedProductList.clear();
                for(int i=0;i<productList.size();i++){
                    if(productList.get(i).getCheckStatus()){
                        if(productList.get(i).getTotalPrice()!= 0) {
                            checkedProductList.add(productList.get(i));
                           // productList.get(i).setCheckStatus(false);

                        }
                    }
                }

                if(!checkedProductList.isEmpty()){
                    for(int i=0;i<checkedProductList.size();i++){
                        grandTotal = checkedProductList.get(i).getTotalPrice() + grandTotal;

                    }

                   // double remainingBalance = Double.parseDouble(remainingDealerbalanceText.getText().toString());
                    netRemBalance = dealerbalance - grandTotal;
                    if(netRemBalance < 0){
                        //sendServer.setVisibility(View.GONE);
                        totalOrderText.setText(String.valueOf(grandTotal));
                        remainingDealerbalanceText.setText(String.valueOf(netRemBalance));
//                        for(int i=0;i<checkedProductList.size();i++){
//                            checkedProductList.get(i).setCheckStatus(false);
//                            checkedProductList.get(i).setTotalPrice(0);
//                        }

                        grandTotal  = 0;
                        showAlert("Budget Overflow","Please order Products according to your Budget");

                    }else{

                        remainingDealerbalanceText.setText(String.valueOf(netRemBalance));
                        totalOrderText.setText(String.valueOf(grandTotal));
                        sendServer.setEnabled(true);
                        sendServer.setVisibility(View.VISIBLE);
                       // grandTotal = 0;
                    }

                }

            }
        });

//        orderConfirmBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(DealerOrder.this,DealerOrderList.class);
////                startActivity(intent);
////                finish();
//                long now = System.currentTimeMillis();
//                String order_no =userCode +'-'+now;
//                if(!checkedProductList.isEmpty()) {
//                    boolean res = sqliteDbHelper.insertIntoOrderHeader(order_no, grandTotal, userCode);
//                    boolean res1 = false;
//                    if (res) {
//                        for (int i = 0; i < checkedProductList.size(); i++) {
//                            res1 = sqliteDbHelper.insertIntoOrder(order_no, checkedProductList.get(i).getP_code(), Integer.parseInt(checkedProductList.get(i).getQty()), checkedProductList.get(i).getP_unit(), Integer.parseInt(checkedProductList.get(i).getP_price()));
////                            checkedProductList.get(i).setQty("");
////                            checkedProductList.get(i).setTotalPrice(0);
//                              checkedProductList.get(i).setCheckStatus(false);
//                        }
//                        if (res1) {
//                            toastMessage("Insertion Successful");
//                        } else {
//                            toastMessage("insertion failed");
//                        }
//                    }
//                }
//
//            }
//        });

        sendServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkedProductList.isEmpty()){
                    AsyncSendToServer task = new AsyncSendToServer();
                    task.execute();
                }

            }
        });
    }


    private final class AsyncSendToServer extends AsyncTask<String,String,String>{
        PostgresqlConnection postgresqlConnection;
        String result;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissProgressDialog();
            try {
                if (s.equals("Success")) {

                    sqliteDbHelper.deleteAllOrderHeaderRecord();
                    sqliteDbHelper.deleteAllOrderRecord();
                    checkedProductList.clear();
                   // orderModalArrayList.clear();
                    sendServer.setEnabled(false);
                    sendServer.setVisibility(View.INVISIBLE);
                    initRecyclerView();
                    totalOrderText.setText("0");
                    grandTotal = 0;
                    Toast.makeText(DealerOrder.this, s, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DealerOrder.this, s, Toast.LENGTH_SHORT).show();
                }
            }catch (Exception ex){
                    showAlert("Null pointer",s);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            long now = System.currentTimeMillis();
            String order_no =userCode +'-';
            ResultSet pKeyResult,pKeyResult1;
            int resultSet,resultSet1;
            boolean isCommit = true;
            postgresqlConnection = new PostgresqlConnection();
            Connection conn = postgresqlConnection.getConn();
            if(conn !=null) {
                try {
                    conn.setAutoCommit(false);
                    Statement stmtOrder = conn.createStatement();
                    String getPkey = "SELECT t_dealer_order_id FROM t_dealer_order ORDER BY t_dealer_order_id DESC limit 1";
                    pKeyResult = stmtOrder.executeQuery(getPkey);
                    if(pKeyResult !=null) {
                        if(pKeyResult.next()) {
                            int getPrimaryKey = pKeyResult.getInt("t_dealer_order_id");
                            order_no = order_no + String.valueOf(getPrimaryKey+1);
                            Statement statement = conn.createStatement();
                            String sql_query = "INSERT INTO t_dealer_order\n" +
                                    " (ad_client_id,ad_org_id,c_bpartner_id,created,createdby,t_dealer_order_id,name,updated,updatedby,value,order_code,totalorderprice)  \n" +
                                    "VALUES (1000000,1000000,'" + dealerCode + "',now(),'" + Integer.parseInt(userCode) + "','"+(getPrimaryKey +1)+"','order',now(),1000000,'"+'o'+"','"+order_no+"', '" + grandTotal + "'); ";

                            resultSet = statement.executeUpdate(sql_query);


                            if (resultSet == 1) {
                                result = "Success";
                            } else {
                                result = "Insertion failed to header";
                                isCommit = false;
                            }
                            for (int i = 0; i < checkedProductList.size(); i++) {
                                Statement stmt2 = conn.createStatement();
                                String pKey =  "SELECT t_dealerorder_line_id FROM t_dealerorder_line ORDER BY t_dealerorder_line_id DESC limit 1";
                                pKeyResult1 = stmt2.executeQuery(pKey);

                                if(pKeyResult1 !=null) {
                                    if(pKeyResult1.next()) {
                                        int getOrderLinePKey = pKeyResult1.getInt("t_dealerorder_line_id");
                                        Statement statement1 = conn.createStatement();
                                        String sql_query1 = "INSERT INTO t_dealerorder_line\n" +
                                                " (ad_client_id,ad_org_id,created,createdby,name,updated,updatedby,value,order_code,t_dealerorder_line_id,m_product_id,qty,price,unit)  \n" +
                                                "VALUES (1000000,1000000,now(),'"+Integer.parseInt(userCode)+"','orderLine',now(),1000000,'" + checkedProductList.get(i).getP_code() + "','" + order_no + "','" + (getOrderLinePKey+1) + "','" + checkedProductList.get(i).getProductID() + "', '" + checkedProductList.get(i).getQty() + "','" + checkedProductList.get(i).getTotalPrice() + "','" + checkedProductList.get(i).getP_unit() + "'); ";



                                        resultSet1 = statement1.executeUpdate(sql_query1);
                                        if (resultSet1 == 1) {
                                            result = "Success";
                                            checkedProductList.get(i).setQty("");
                                            checkedProductList.get(i).setTotalPrice(0);
                                            checkedProductList.get(i).setCheckStatus(false);
                                        } else {
                                            result = "Insertion failed to order Line";
                                            isCommit = false;
                                        }
                                    }else{
                                        Statement statement1 = conn.createStatement();
                                        String sql_query1 = "INSERT INTO t_dealerorder_line\n" +
                                                " (ad_client_id,ad_org_id,created,createdby,name,updated,updatedby,value,order_code,t_dealerorder_line_id,m_product_id,qty,price,unit)  \n" +
                                                "VALUES (1000000,1000000,now(),'"+Integer.parseInt(userCode)+"','orderLine',now(),1000000,'" + checkedProductList.get(i).getP_code() + "','" + order_no + "','" + 1 + "','" + checkedProductList.get(i).getProductID() + "', '" + checkedProductList.get(i).getQty() + "','" + checkedProductList.get(i).getTotalPrice() + "','" + checkedProductList.get(i).getP_unit() + "'); ";


                                        resultSet1 = statement1.executeUpdate(sql_query1);
                                        if (resultSet1 == 1) {
                                            result = "Success";
                                            checkedProductList.get(i).setQty("");
                                            checkedProductList.get(i).setTotalPrice(0);
                                            checkedProductList.get(i).setCheckStatus(false);

                                        } else {
                                            result = "Insertion failed to order Line";
                                            isCommit = false;
                                        }
                                    }
                                }else{
                                    toastMessage("table t_dealerorder_line does not exits ");
                                    isCommit = false;
                                }
                            }
                        }else{

                            order_no = order_no + String.valueOf(1);
                            Statement statement = conn.createStatement();
                            String sql_query = "INSERT INTO t_dealer_order\n" +
                                    " (ad_client_id,ad_org_id,c_bpartner_id,created,createdby,t_dealer_order_id,name,updated,updatedby,value,order_code,totalorderprice)  \n" +
                                    "VALUES (1000000,1000000,'" + dealerCode + "',now(),'" + Integer.parseInt(userCode) + "','"+1+"','order',now(),1000000,'O','"+order_no+"', '" + grandTotal + "'); ";

                            resultSet = statement.executeUpdate(sql_query);


                            if (resultSet == 1) {
                                result = "Success";
                            } else {
                                result = "Insertion failed to header";
                                isCommit = false;
                            }
                            for (int i = 0; i < checkedProductList.size(); i++) {

                                Statement stmt2 = conn.createStatement();
                                String pKey =  "SELECT t_dealerorder_line_id FROM t_dealerorder_line ORDER BY t_dealerorder_line_id DESC limit 1";
                                pKeyResult1 = stmt2.executeQuery(pKey);

                                if(pKeyResult1 !=null) {
                                    if(pKeyResult1.next()) {
                                        int getOrderLinePKey = pKeyResult1.getInt("t_dealerorder_line_id");
                                        Statement statement1 = conn.createStatement();
                                        String sql_query1 = "INSERT INTO t_dealerorder_line\n" +
                                                " (ad_client_id,ad_org_id,created,createdby,name,updated,updatedby,value,order_code,t_dealerorder_line_id,m_product_id,qty,price,unit)  \n" +
                                                "VALUES (1000000,1000000,now(),'"+Integer.parseInt(userCode)+"','orderLine',now(),1000000,'" + checkedProductList.get(i).getP_code() + "','" + order_no + "','" + (getOrderLinePKey+1) + "','" + checkedProductList.get(i).getProductID() + "', '" + checkedProductList.get(i).getQty() + "','" + checkedProductList.get(i).getTotalPrice() + "','" + checkedProductList.get(i).getP_unit() + "'); ";



                                        resultSet1 = statement1.executeUpdate(sql_query1);
                                        if (resultSet1 == 1) {
                                            result = "Success";
                                            checkedProductList.get(i).setQty("");
                                            checkedProductList.get(i).setTotalPrice(0);
                                            checkedProductList.get(i).setCheckStatus(false);
                                        } else {
                                            result = "Insertion failed to order Line";
                                            isCommit = false;
                                        }
                                    }else{
                                        Statement statement1 = conn.createStatement();
                                        String sql_query1 = "INSERT INTO t_dealerorder_line\n" +
                                                " (ad_client_id,ad_org_id,created,createdby,name,updated,updatedby,value,order_code,t_dealerorder_line_id,m_product_id,qty,price,unit)  \n" +
                                                "VALUES (1000000,1000000,now(),'"+Integer.parseInt(userCode)+"','orderLine',now(),1000000,'" + checkedProductList.get(i).getP_code() + "','" + order_no + "','" + 1 + "','" + checkedProductList.get(i).getProductID() + "', '" + checkedProductList.get(i).getQty() + "','" + checkedProductList.get(i).getTotalPrice() + "','" + checkedProductList.get(i).getP_unit() + "'); ";



                                        resultSet1 = statement1.executeUpdate(sql_query1);
                                        if (resultSet1 == 1) {
                                            result = "Success";
                                            checkedProductList.get(i).setQty("");
                                            checkedProductList.get(i).setTotalPrice(0);
                                            checkedProductList.get(i).setCheckStatus(false);
                                        } else {
                                            result = "Insertion failed to order Line";
                                            isCommit = false;
                                        }
                                    }
                                }else{
                                    toastMessage("table t_dealerorder_line does not exits ");
                                    isCommit = false;
                                }
                            }
                        }
                    }else {
                        toastMessage("No table Called t_dealer_order available");
                        isCommit = false;
                    }

                    if(netRemBalance > 0 ){
                        int resultSetUpdate;
                        Statement statement6 = conn.createStatement();
                        String sql_query6 = "UPDATE t_aso_distributorassign set dealer_balance = '" + netRemBalance + "' where t_asoinfo_id = '" + Integer.parseInt(userCode) + "' and c_bpartner_id = '"+Integer.parseInt(dealerCode)+"'";
                        resultSetUpdate = statement6.executeUpdate(sql_query6);
                        if(resultSetUpdate != 0){
                            result = "Success";
                        }else{
                            result = "Failed";
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
                        e.printStackTrace();
                    }
                }
                return result;
            }else{
                return "connection failed";
            }
        }
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


    private void toastMessage(String msg){
        Toast.makeText(DealerOrder.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showProgressDialog(){
        if(pd == null){
            pd = new ProgressDialog(DealerOrder.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Sending To Server.....");
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

    public void showAlert(String title,String message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(DealerOrder.this);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}
