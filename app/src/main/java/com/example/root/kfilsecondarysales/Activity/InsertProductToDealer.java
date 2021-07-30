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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.root.kfilsecondarysales.Adapter.InsertProductToDealerRecyclerViewAdapter;
import com.example.root.kfilsecondarysales.Database.PostgresqlConnection;
import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
import com.example.root.kfilsecondarysales.Modal.AsoToDealerProductModal;
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

public class InsertProductToDealer extends AppCompatActivity {
    ProgressDialog pd;
    Spinner brandSpinner,dealerSpinner;
    android.support.v7.widget.Toolbar toolbar;
    Button sendBtn;
    String dealerCode,productName,productCode,brandName;
    SqliteDbHelper dbHelper = new SqliteDbHelper(this);
    AsoToDealerProductModal asoToDealerProductModal;

    InsertProductToDealerRecyclerViewAdapter adapter;
    RecyclerView productListRecyclerView;

    PostgresqlConnection postgresqlConnection = new PostgresqlConnection();

    HashMap<String, String> productCodeHash = new HashMap<String, String>();
    ArrayList<String> brandArrayList = new ArrayList<>();
    ArrayList<String> dealerArrayList = new ArrayList<>();
    ArrayList<AsoToDealerProductModal> productArrayList = new ArrayList<>();
    final ArrayList<AsoToDealerProductModal> productCheckedList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product_to_dealer_new);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InsertProductToDealer.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sendBtn    = findViewById(R.id.confirm_btn);



        dealerSpinner = findViewById(R.id.dealer_list_spinner);
        brandSpinner = findViewById(R.id.brand_list_spinner);
        productListRecyclerView = findViewById(R.id.data_table_recyclerview);

//        boolean vpn = checkVPN();
//        if(vpn){
            AsyncLoadDealer task = new AsyncLoadDealer();
            task.execute();
//        }else {
//            showAlert("Warning","Connect Vpn");
//        }

        //onDealerCodeEditTextFocusChange();
        sendToServerBtnClick();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

//    private void onDealerCodeEditTextFocusChange() {
//
//
//        dealerCode_editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                String code = dealerCode_editText.getText().toString();
//                Cursor result = dbHelper.getDealerCode(code);
//                if(result.moveToFirst()){
//                    dealerCode_editText.setText(result.getString(0));
//                }else{
//                    dealerCode_editText.setText("");
//                    Toast.makeText(InsertProductToDealer.this, "Dealer Not Found", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
//        });
//    }



    private void getSelectedDealerSpinner(){

            dealerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        dealerCode = parent.getSelectedItem().toString();

                    }catch (Exception ex){

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

    }

    private void getSelectedBrandSpinnerItem(){


            brandSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        brandName = parent.getSelectedItem().toString();
                        AsyncLoadProduct task = new AsyncLoadProduct();
                        task.execute(brandName);

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
            Cursor cursor = dbHelper.getDealer();
            String result = "";
            if(cursor.moveToFirst()){
                do{
                    String dealerCode = cursor.getString(0);
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
                    AsyncLoadBrand task = new AsyncLoadBrand();
                    task.execute();
                    //getSelectedCategorySpinnerItem();
                }else{
                    showAlert("Error","Check Connection");
                }
            }catch (NullPointerException ex){

            }
        }
    }




    private final class AsyncLoadBrand extends AsyncTask<String,String,String>{

       // String result = "";
        @Override
        protected String doInBackground(String... strings) {

            brandArrayList.clear();
            Cursor cursor = dbHelper.getBrand();
            String result = "";
            if(cursor.moveToFirst()){
                do{
                    String brandName = cursor.getString(0);
                    brandArrayList.add(brandName);
                }while(cursor.moveToNext());
                result = "brand synced";
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
                if(s.equals("brand synced")){

                    loadBrandSpinner();
                    getSelectedBrandSpinnerItem();
                }else {
                    showAlert("Error","Check Connection");
                }
            }catch (NullPointerException ex){

            }
        }
    }



    private final class AsyncLoadProduct extends AsyncTask<String,String,String>{

        ResultSet resultSet;
        //String result = "";
        @Override
        protected String doInBackground(String... strings) {

            productArrayList.clear();
            Cursor cursor = dbHelper.getProductList(strings[0]);
            String result = "";
            if(cursor.moveToFirst()){
                do{
                    String productName = cursor.getString(0);
                    String productCode = cursor.getString(1);
                    String unit        = cursor.getString(2);
                    double price       = cursor.getDouble(3);
                    int product_id     = cursor.getInt(4);
                    String pAmount     = cursor.getString(5);
                    int productCategoryID = cursor.getInt(6);
                    //productHash.put(productName,productCode);

                    asoToDealerProductModal = new AsoToDealerProductModal(productName,productCode,product_id,unit,price,pAmount,productCategoryID);
                    //productCodeHash.put(product_name, product_code);
                    productArrayList.add(asoToDealerProductModal);
                }while(cursor.moveToNext());
                result = "Success";
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
                if(s.equals("Success")){
                    //loadProductSpinner();
                    //getSelectedProductSpinnerItem();
                    initRecyclerView();
                }else {
                    showAlert("Error","Check Connection");
                }
            }catch (NullPointerException ex){

            }
        }
    }


    void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        productListRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new InsertProductToDealerRecyclerViewAdapter(InsertProductToDealer.this,productArrayList);
        productListRecyclerView.setAdapter(adapter);
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
                    productListRecyclerView.removeAllViews();
                    initRecyclerView();
                }else{
                    adapter.searchFilter(newText);
                }
                return true;
            }
        });

        return true;
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

    private void loadBrandSpinner(){

        brandSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
        //System.out.println(routeList);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.color_spinner_layout,brandArrayList);

        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        brandSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();

    }


    private void sendToServerBtnClick(){

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  for(int i = 0;i<productArrayList.size();i++){
                      if(productArrayList.get(i).isChecked() && Integer.parseInt( productArrayList.get(i).getTotalPice()) != 0){
                          productCheckedList.add(productArrayList.get(i));
                          productArrayList.get(i).setChecked(false);
                      }
                  }
//                boolean vpn = checkVPN();
//                if(vpn){
                    AsyncSendToServer task = new AsyncSendToServer();
                    task.execute();
//                }else {
//                    showAlert("Warning","Connect Vpn");
//                }


            }
        });



    }

    private final class AsyncSendToServer extends AsyncTask<String,String,String>{

        PostgresqlConnection postgresqlConnection;
        int resultSet;
        ResultSet resultSet1;
        String result;
        boolean isCommit = true;
        @Override
        protected String doInBackground(String... strings) {
            postgresqlConnection = new PostgresqlConnection();
            Connection conn = postgresqlConnection.getConn();
            if(conn != null) {
                if (!productCheckedList.isEmpty()) {
                    try {
                        conn.setAutoCommit(false);
                        for (int i = 0; i < productCheckedList.size(); i++) {

                            Statement stmt = conn.createStatement();
                            String sql = "SELECT t_dist_open_inventory_id FROM t_dist_open_inventory ORDER BY t_dist_open_inventory_id DESC limit 1";

                            resultSet1 = stmt.executeQuery(sql);
                            if(resultSet1 !=null) {
                                if(resultSet1.next()) {
                                    int getPrimaryKey = resultSet1.getInt("t_dist_open_inventory_id");

                                    if(getPrimaryKey != 0) {
                                        Statement statement1 = conn.createStatement();
                                        String searchProductQuery = "select sum(qty) as t_qty from t_dist_open_inventory where m_product_id = '"+productCheckedList.get(i).getM_product_id()+"' and c_bpartner_id = '" + dealerCode + "' ";
                                        ResultSet productResultSet =  statement1.executeQuery(searchProductQuery);
                                        if(productResultSet.next()){
                                            int qty = productResultSet.getInt("t_qty");

                                            if(qty > 0){
                                                int total_qty  = qty + Integer.parseInt( productCheckedList.get(i).getTotalPice());
                                                Statement statement2 = conn.createStatement();
                                                String update_query = "update t_dist_open_inventory set qty =  '"+total_qty+"' where m_product_id = '"+productCheckedList.get(i).getM_product_id()+"' and c_bpartner_id = '" + dealerCode + "' ";

                                                int update_qty = statement2.executeUpdate(update_query);
                                                if(update_qty !=0){
                                                    result = "Success";
                                                }else{
                                                    result = "Update failed";
                                                    isCommit = false;
                                                }
                                            }else{
                                                Statement statement = conn.createStatement();
                                                String sql_query = "INSERT INTO t_dist_open_inventory\n" +
                                                        " (ad_client_id,ad_org_id,created,createdby,name,t_dist_open_inventory_id,updated,updatedby,c_bpartner_id,value,m_product_id,qty,unit,unitprice,dateconfirm,m_product_category_id)  \n" +
                                                        "VALUES (1000000,1000000,now(),'" + Integer.parseInt(userCode) + "','" + productCheckedList.get(i).getProduct_name() + "','" + (getPrimaryKey + 1) + "',now(),1000,'" + dealerCode + "','" + productCheckedList.get(i).getProduct_code() + "','" + productCheckedList.get(i).getM_product_id() + "','" + productCheckedList.get(i).getTotalPice() + "','"+productCheckedList.get(i).getUnit()+"','"+productCheckedList.get(i).getPrice()+"',now(),'"+productCheckedList.get(i).getProductCategoryID()+"'); ";

                                                resultSet = statement.executeUpdate(sql_query);
                                                if (resultSet == 1) {
                                                    result = "Success";
                                                } else {
                                                    result = "Insertion failed";
                                                    isCommit = false;
                                                }
                                            }

                                        }

                                    }
                                }else {
                                    Statement statement = conn.createStatement();
                                    String sql_query = "INSERT INTO t_dist_open_inventory\n" +
                                            " (ad_client_id,ad_org_id,created,createdby,name,t_dist_open_inventory_id,updated,updatedby,c_bpartner_id,value,m_product_id,qty,unit,unitprice,dateconfirm,m_product_category_id)  \n" +
                                            "VALUES (1000000,1000000,now(),'"+Integer.parseInt(userCode)+"','" + productCheckedList.get(i).getProduct_name() + "','" + 1 + "',now(),1000,'" + dealerCode + "','" + productCheckedList.get(i).getProduct_code() + "','" + productCheckedList.get(i).getM_product_id() + "','" + productCheckedList.get(i).getTotalPice() + "','"+productCheckedList.get(i).getUnit()+"','"+productCheckedList.get(i).getPrice()+"',now(),'"+productCheckedList.get(i).getProductCategoryID()+"'); ";

                                    resultSet = statement.executeUpdate(sql_query);
                                    if (resultSet == 1) {
                                        result = "Success";
                                    } else {
                                        result = "Insertion failed";
                                        isCommit = false;
                                    }
                                }
                            }else{
                               toastMessage("No table Called t_dist_open_inventory available");
                               isCommit = false;
                            }

                        }

                        if(isCommit){
                            conn.commit();
                        }else {
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
                }else {
                    return result = "No product Selected or 0 number of items";
                }
            }
            else {
                return result = "No Connection";
            }
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
            try {
                if (s.equals("Success")) {
                    productCheckedList.clear();
                    Toast.makeText(InsertProductToDealer.this, s, Toast.LENGTH_SHORT).show();
                    initRecyclerView();
                } else {
                    Toast.makeText(InsertProductToDealer.this, s, Toast.LENGTH_SHORT).show();
                }
            }catch (NullPointerException ex){
                toastMessage(ex.getMessage());
            }
        }
    }

    private void toastMessage(String msg){
        Toast.makeText(InsertProductToDealer.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showAlert(String title,String message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(InsertProductToDealer.this);

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
            pd = new ProgressDialog(InsertProductToDealer.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Sales Status Loading.....");
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
