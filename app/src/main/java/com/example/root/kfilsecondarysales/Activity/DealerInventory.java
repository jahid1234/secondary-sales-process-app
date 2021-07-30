package com.example.root.kfilsecondarysales.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
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
import android.widget.Adapter;
import android.widget.Toolbar;

import com.example.root.kfilsecondarysales.Adapter.ProductRecyclerViewAdapter;
import com.example.root.kfilsecondarysales.Database.PostgresqlConnection;
import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
import com.example.root.kfilsecondarysales.Modal.ProductModal;
import com.example.root.kfilsecondarysales.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static com.example.root.kfilsecondarysales.Activity.LoginActivity.userCode;

public class DealerInventory extends AppCompatActivity {
    android.support.v7.widget.Toolbar toolbar;
    RecyclerView productRecyclerView;
    ProductRecyclerViewAdapter adapter;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_inventory);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DealerInventory.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

//         AsyncProduct task = new AsyncProduct();
//         task.execute();
         getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
         loadDealerInventory();
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

//    private final class AsyncProduct extends AsyncTask<String,String,ArrayList>{
//
//        ProductModal productModal;
//
//        ResultSet resultSet;
//        PostgresqlConnection postgresqlConnection;
//        ArrayList<ProductModal> productModalArrayList = new ArrayList();
//        String productName;
//        String quantity;
//        String unit;
//        String date;
//        String result;
//        @Override
//        protected ArrayList doInBackground(String... strings) {
//
//            postgresqlConnection = new PostgresqlConnection();
//            Connection conn = postgresqlConnection.getConn();
//
//            try {
//
//                Statement statement = conn.createStatement();
//                String sql_query = "select * from dealer_inventory_balance where dealer_code = '"+userCode+"' order by product_name";
//                resultSet = statement.executeQuery(sql_query);
//                while(resultSet.next()){
//
//                   productName = resultSet.getString("product_name");
//                   quantity    = resultSet.getString("quantity");
//                   unit        = resultSet.getString("unit");
//                   date        = resultSet.getString("date");
//                   productModal = new ProductModal(productName,quantity,unit,date);
//                    productModalArrayList.add(productModal);
//                   result = "Success";
//
//                }
//
//
//            }catch (SQLException ex){
//                    result = ex.getMessage();
//            }finally {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            return productModalArrayList;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            if(pd == null) {
//                pd = new ProgressDialog(DealerInventory.this);
//                pd.setTitle("Please Wait");
//                pd.setMessage("Loading ....");
//                pd.setCancelable(false);
//                pd.setIndeterminate(true);
//                pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            }
//            pd.show();
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList s) {
//
//            super.onPostExecute(s);
//            dismissProgressDialog();
//            try{
//                if(result.equals("Success")){
//                    initRecyclerView(s);
//                }else{
//                    showAlert("Login error",result);
//                }
//            }catch (NullPointerException ex){
//                showAlert("Error","No Product Found!!");
//            }
//        }
//    }


    private void loadDealerInventory(){
        ProductModal productModal;
        SqliteDbHelper sqliteDbHelper = new SqliteDbHelper(this);
        Cursor cursor = sqliteDbHelper.getDealerInventory();
        ArrayList<ProductModal> productModalArrayList = new ArrayList();
        if(cursor.moveToFirst()){
            do{
                String productName = cursor.getString(1);
                String quantity = cursor.getString(4);
                String unit     = cursor.getString(5);
                String date     = cursor.getString(6);
                productModal = new ProductModal(productName,quantity,unit,date);
                productModalArrayList.add(productModal);

            }while(cursor.moveToNext());
        }

        initRecyclerView(productModalArrayList);
    }


    public void showAlert(String title,String message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(DealerInventory.this);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public void initRecyclerView(ArrayList productmodal){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        productRecyclerView = findViewById(R.id.product_recyclerview);
        productRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ProductRecyclerViewAdapter(productmodal,DealerInventory.this);
        productRecyclerView.setAdapter(adapter);
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
                    productRecyclerView.removeAllViews();
                }else{
                    adapter.searchFilter(newText);
                }
                return true;
            }
        });

        return true;
    }
}
