//package com.example.root.kfilsecondarysales.Activity;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.AsyncTask;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.SearchView;
//import android.support.v7.widget.Toolbar;
//import android.text.TextUtils;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.example.root.kfilsecondarysales.Adapter.OrderRecordDealerRecyclerViewAdapter;
//import com.example.root.kfilsecondarysales.Database.PostgresqlConnection;
//import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
//import com.example.root.kfilsecondarysales.Modal.OrderModal;
//import com.example.root.kfilsecondarysales.R;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//
//public class DealerOrderList extends AppCompatActivity {
//
//    RecyclerView orderListRecyclerView;
//    Toolbar toolbar;
//    Button confirmOrderBtn;
//    OrderRecordDealerRecyclerViewAdapter adapter;
//
//    OrderModal orderModal;
//    SqliteDbHelper dbHelper = new SqliteDbHelper(this);
//    ArrayList<OrderModal> orderModalArrayList = new ArrayList<>();
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dealer_order_list);
//
//        toolbar = findViewById(R.id.toolbar);
//
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DealerOrderList.this,DealerOrder.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        confirmOrderBtn = findViewById(R.id.order_btn);
//        orderListRecyclerView = findViewById(R.id.dealer_order_recyclerview);
//        confirmOrderBtn.setEnabled(false);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//
//        loadOrderList();
//        confirmOrderAndSendToServer();
//    }
//
//    private void loadOrderList(){
//        Cursor result = dbHelper.getOrderList();
//        if(result.moveToFirst()){
//            do{
//                String p_key       = result.getString(0);
//                String productCode = result.getString(1);
//                String productName = result.getString(2);
//                String quantity    = result.getString(3);
//                String unit        = result.getString(4);
//                String orderNo     = result.getString(5);
//
//                orderModal = new OrderModal(p_key,productCode,productName,quantity,unit,orderNo);
//                orderModalArrayList.add(orderModal);
//            }while(result.moveToNext());
//        }
//
//        if(!orderModalArrayList.isEmpty()){
//            confirmOrderBtn.setEnabled(true);
//        }
//        initRecyclerView();
//    }
//
//    private void initRecyclerView(){
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
//        orderListRecyclerView.setLayoutManager(linearLayoutManager);
//        adapter = new OrderRecordDealerRecyclerViewAdapter(DealerOrderList.this,orderModalArrayList);
//        orderListRecyclerView.setAdapter(adapter);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_menu,menu);
//        MenuItem mySearch = menu.findItem(R.id.search_view);
//
//        SearchView searchView = (SearchView) mySearch.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                if(TextUtils.isEmpty(newText)){
//                    adapter.searchFilter("");
//                    orderListRecyclerView.removeAllViews();
//                }else{
//                    adapter.searchFilter(newText);
//                }
//                return true;
//            }
//        });
//
//        return true;
//    }
//
//
//    private void confirmOrderAndSendToServer(){
//        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
////                for (OrderModal modal: orderModalArrayList) {
////
////                }
//                AsyncSendToServer task = new AsyncSendToServer();
//                task.execute();
//            }
//        });
//    }
//
//    private final class AsyncSendToServer extends AsyncTask<String,String,String>{
//        PostgresqlConnection postgresqlConnection;
//        int resultSet;
//        String result;
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            try {
//                if (s.equals("Success")) {
//                    dbHelper.deleteAllOrderRecord();
//                    Toast.makeText(DealerOrderList.this, s, Toast.LENGTH_SHORT).show();
//                    orderModalArrayList.clear();
//                    confirmOrderBtn.setEnabled(false);
//                    initRecyclerView();
//                } else {
//                    Toast.makeText(DealerOrderList.this, s, Toast.LENGTH_SHORT).show();
//                }
//            }catch (Exception ex){
//
//            }
//        }
//
//        @Override
//        protected String doInBackground(String... strings) {
//
//
//            postgresqlConnection = new PostgresqlConnection();
//            Connection conn = postgresqlConnection.getConn();
//            try{
//
//                for(OrderModal modal: orderModalArrayList) {
//                    Statement statement = conn.createStatement();
//                    String sql_query = "INSERT INTO dealer_order\n" +
//                            " (order_code, product_code,quantity,unit,date)  \n" +
//                            "VALUES ('" + modal.getOrderCode() + "','" + modal.getProductCode() + "', '" + modal.getQuantity() + "','" + modal.getUnit() + "',now()); ";
//
//                    resultSet = statement.executeUpdate(sql_query);
//
//                }
//
//                if(resultSet == 1){
//                    result = "Success";
//                }else{
//                    result = "Insertion failed";
//                }
//            }catch (SQLException ex){
//                result = ex.getMessage();
//            }finally {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            return result;
//        }
//    }
//
//}
