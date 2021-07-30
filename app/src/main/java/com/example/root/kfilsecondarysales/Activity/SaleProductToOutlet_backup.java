//package com.example.root.kfilsecondarysales.Activity;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.PorterDuff;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Spinner;
//import android.widget.Toast;
//
//import com.example.root.kfilsecondarysales.Database.PostgresqlConnection;
//import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
//import com.example.root.kfilsecondarysales.R;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import static com.example.root.kfilsecondarysales.Activity.LoginActivity.userCode;
//
//public class SaleProductToOutlet_backup extends AppCompatActivity {
//
//    Spinner routeListSpinner,outletListSpinner,productListSpinner,unitListSpinner;
//    EditText quantity_editText;
//    Button addSales_btn,viewSales_btn,confirmSales_btn;
//
//    SqliteDbHelper dbHelper;
//    Toolbar toolbar;
//    ProgressDialog pd;
//
//    String dealerCode,route_code,route_name,outlet_name,outlet_code,product_name,unit,puller_code;
//    String product_quantity;
//
//    List<String> routeList = new ArrayList<String>();
//    List<String> outletList = new ArrayList<>();
//    List<String> productList = new ArrayList<>();
//    List<String> productUnitList = new ArrayList<>();
//    HashMap<String, String> routedataList = new HashMap<String, String>();
//    HashMap<String, String> outletdataList = new HashMap<String, String>();
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sale_product_to_outlet);
//
//        dbHelper = new SqliteDbHelper(this);
//
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SaleProductToOutlet_backup.this,HomeActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        initializeUi();
//        getSelectedSpinnerItem();
//        saveAndSendToServer();
////        unitListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
////        ArrayAdapter adapter4 = ArrayAdapter.createFromResource(this,R.array.unit_arrays,R.layout.color_spinner_layout);
////        adapter4.setDropDownViewResource(R.layout.spinner_dropdown_layout);
////        unitListSpinner.setAdapter(adapter4);
//
//        AsyncLoadSpinner asyncLoadSpinner = new AsyncLoadSpinner();
//        asyncLoadSpinner.execute();
//
//    }
//
//    private void initializeUi(){
//        routeListSpinner   = findViewById(R.id.route_list_spinner);
//        outletListSpinner  = findViewById(R.id.outlet_list_spinner);
//        productListSpinner = findViewById(R.id.product_list_spinner);
//        unitListSpinner    = findViewById(R.id.unit_list_spinner);
//
//        quantity_editText  = findViewById(R.id.editText_quantity);
//        addSales_btn           = findViewById(R.id.add_btn);
//        viewSales_btn          = findViewById(R.id.view_btn);
//        confirmSales_btn       = findViewById(R.id.confirm_btn);
//
//    }
//
//
//    private void getSelectedSpinnerItem(){
//
//        routeListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String selectedRoute = parent.getSelectedItem().toString();
//                route_name = selectedRoute;
//                route_code = routedataList.get(selectedRoute);
//                AsyncLoadOutletSpinner task = new AsyncLoadOutletSpinner();
//                task.execute(route_code);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
//        outletListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                outlet_name = parent.getSelectedItem().toString();
//                outlet_code = outletdataList.get(outlet_name);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        productListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                product_name = parent.getSelectedItem().toString();
//                productUnitList.clear();
//                loadUnitSpinner(product_name);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        unitListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                unit = parent.getSelectedItem().toString();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//
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
//
//
//
//    private final class AsyncLoadSpinner extends AsyncTask<String,String,String> {
//
//
//
//        String result;
//        @Override
//        protected String doInBackground(String... strings) {
//
//            getDealerCode();
//            getRouteListFromServer();
//           // getOutletFromServer();
//            result = "Success";
//            return result;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pd = new ProgressDialog(SaleProductToOutlet_backup.this);
//            pd.setTitle("Please Wait");
//            pd.setMessage("Loading ....");
//            pd.setCancelable(false);
//            pd.setIndeterminate(true);
//            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            pd.show();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            dismissProgressDialog();
//            if(s.equals("Success")){
//                toastMessage(s);
//                loadRouteSpinner();
//            }else{
//                toastMessage(s);
//            }
//        }
//    }
//
//
//    private final class AsyncLoadOutletSpinner extends AsyncTask<String,String,String> {
//
//
//
//        String result;
//        @Override
//        protected String doInBackground(String... strings) {
//
//            outletList.clear();
//            getOutletFromServer();
//            productList.clear();
//            getProductFromServer();
//            result = "Success";
//            return result;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
////            pd = new ProgressDialog(SaleProductToOutlet.this);
////            pd.setTitle("Please Wait");
////            pd.setMessage("Loading ....");
////            pd.setCancelable(false);
////            pd.setIndeterminate(true);
////            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
////            pd.show();
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
////            dismissProgressDialog();
//            if(s.equals("Success")){
//                toastMessage(s);
//                loadOutletSpinner();
//                loadProductSpinner();
//            }else{
//                toastMessage(s);
//            }
//        }
//    }
//
//    public void getDealerCode(){
//        PostgresqlConnection postgresqlConnection;
//        ResultSet resultSet;
//        String result;
//        postgresqlConnection = new PostgresqlConnection();
//        Connection conn = postgresqlConnection.getConn();
//        try{
//
//            Statement statement = conn.createStatement();
//            String sql_query = "select dealer_code from van_puller where puller_code = '"+userCode+"'";
//
//            resultSet = statement.executeQuery(sql_query);
//            while(resultSet.next()){
//                dealerCode = resultSet.getString("dealer_code");
//            }
//        }catch (SQLException ex){
//            result = ex.getMessage();
//        }finally {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    private void getRouteListFromServer() {
//
//
//        PostgresqlConnection postgresqlConnection;
//        ResultSet resultSet;
//        String result;
//        postgresqlConnection = new PostgresqlConnection();
//        Connection conn = postgresqlConnection.getConn();
//        try{
//
//            Statement statement = conn.createStatement();
//            String sql_query = "select route_code,route_name from route where dealer_code = '"+dealerCode+"'";
//
//            resultSet = statement.executeQuery(sql_query);
//            while(resultSet.next()){
//                String routeCode = resultSet.getString("route_code");
//                String routeName = resultSet.getString("route_name");
//                routedataList.put(routeName,routeCode);
//                routeList.add(routeName);
//            }
//        }catch (SQLException ex){
//            result = ex.getMessage();
//        }finally {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if(routeList.isEmpty()){
//            toastMessage("No routes found");
//        }
//    }
//
//    private void getProductFromServer(){
//        PostgresqlConnection postgresqlConnection;
//        ResultSet resultSet;
//        String result;
//        postgresqlConnection = new PostgresqlConnection();
//        Connection conn = postgresqlConnection.getConn();
//        try{
//
//            Statement statement = conn.createStatement();
//            String sql_query = "select product_name from dealer_inventory_balance where dealer_code = '"+dealerCode+"' order by product_name";
//
//            resultSet = statement.executeQuery(sql_query);
//            while(resultSet.next()){
//                String productName = resultSet.getString("product_name");
//
//                productList.add(productName);
//            }
//        }catch (SQLException ex){
//            result = ex.getMessage();
//        }finally {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if(productList.isEmpty()){
//            toastMessage("No products found");
//        }
//    }
//
//    private void getOutletFromServer(){
//        PostgresqlConnection postgresqlConnection;
//        ResultSet resultSet;
//        String result;
//        postgresqlConnection = new PostgresqlConnection();
//        Connection conn = postgresqlConnection.getConn();
//        try{
//
//            Statement statement = conn.createStatement();
//            String sql_query = "select outlet_name,outlet_code from outlet where route_code = '"+route_code+"'";
//
//            resultSet = statement.executeQuery(sql_query);
//            while(resultSet.next()){
//                String outletName  = resultSet.getString("outlet_name");
//                String outletCode  = resultSet.getString("outlet_code");
//                outletdataList.put(outletName,outletCode);
//                outletList.add(outletName);
//            }
//        }catch (SQLException ex){
//            result = ex.getMessage();
//        }finally {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//
//        if(outletList.isEmpty()){
//            toastMessage("No Outlets found");
//        }
//    }
//
//    private void loadProductSpinner(){
//        productListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
//        //System.out.println(routeList);
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                R.layout.color_spinner_layout,productList);
//
//        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
//        productListSpinner.setAdapter(dataAdapter);
//        dataAdapter.notifyDataSetChanged();
//    }
//
//    private void loadRouteSpinner(){
//        routeListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
//        //System.out.println(routeList);
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                R.layout.color_spinner_layout,routeList);
//
//        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
//        routeListSpinner.setAdapter(dataAdapter);
//        dataAdapter.notifyDataSetChanged();
//
////        String getSpinnerItem = routeListSpinner.getSelectedItem().toString();
////        toastMessage(getSpinnerItem);
//    }
//
//    private void loadOutletSpinner(){
//        outletListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
//        //System.out.println(routeList);
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                R.layout.color_spinner_layout,outletList);
//
//        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
//        outletListSpinner.setAdapter(dataAdapter);
//        dataAdapter.notifyDataSetChanged();
//
////        String getSpinnerItem = outletListSpinner.getSelectedItem().toString();
////        toastMessage(getSpinnerItem);
//        //outletList.clear();
//    }
//
//    private void loadUnitSpinner(String productName){
//        String selectedProductUnit = "";
//        SqliteDbHelper sqliteDbHelper = new SqliteDbHelper(this);
//        Cursor cursor = sqliteDbHelper.getUnitFromDealerInventory(productName);
//        if(cursor.moveToFirst()){
//             selectedProductUnit = cursor.getString(0);
//             productUnitList.add(selectedProductUnit);
//        }
//
//        unitListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
//        //System.out.println(routeList);
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
//                R.layout.color_spinner_layout,productUnitList);
//
//        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
//        unitListSpinner.setAdapter(dataAdapter);
//        dataAdapter.notifyDataSetChanged();
//        unitListSpinner.setEnabled(false);
//    }
//
//
//    private void toastMessage(String msg){
//        Toast.makeText(SaleProductToOutlet_backup.this, msg, Toast.LENGTH_SHORT).show();
//    }
//
//    public void saveAndSendToServer(){
//
//        final SqliteDbHelper dbHelper = new SqliteDbHelper(this);
//
//        addSales_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v){
//                product_quantity = quantity_editText.getText().toString();
//                int p_qnt = Integer.parseInt(product_quantity);
//              boolean res =  dbHelper.insertOutletSales(route_name,outlet_name,outlet_code,product_name,p_qnt,unit,userCode);
//              if(res){
//                  toastMessage("sqlite insertion Successfull");
//              }else{
//                  toastMessage("sqlite Failed");
//              }
//            }
//        });
//
//        confirmSales_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                product_quantity = quantity_editText.getText().toString();
//
//                if(product_quantity.isEmpty()){
//                    quantity_editText.setError("Please add Quantity");
//                }
//
//                AsyncSendSalesToServer salesTask = new AsyncSendSalesToServer();
//                salesTask.execute();
//
//                quantity_editText.setText("");
//            }
//        });
//
//
//    }
//
//
//    private final class AsyncSendSalesToServer extends AsyncTask<String,String,String>{
//
//        String result;
//        @Override
//        protected String doInBackground(String... strings) {
//
//            boolean s = saveToServer();
//            if(s){
//                result = "Success";
//            }else {
//                result = "Failed";
//            }
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
//            try{
//                if(s.equals("Success")){
//                    toastMessage("Successfully Inserted");
//                }else {
//                    toastMessage(s);
//                }
//            }catch(NullPointerException ex){
//                showAlert("Error",ex.getMessage());
//            }
//        }
//    }
//
//    private boolean saveToServer(){
//        PostgresqlConnection postgresqlConnection;
//        int resultSet;
//        boolean result = false;
//        postgresqlConnection = new PostgresqlConnection();
//        Connection conn = postgresqlConnection.getConn();
//
//        try{
//            int p_quantity = Integer.parseInt(product_quantity);
//
//            Statement statement = conn.createStatement();
//            String sql_query = "INSERT INTO outlet_sales\n" +
//                    " (route_name, outlet_name, outlet_code,product_name,quantity,unit,puller_code,dealer_code,date)  \n" +
//                    "VALUES ('"+route_name+"', '"+outlet_name+"','"+outlet_code+"','"+product_name+"','"+p_quantity+"','"+unit+"','"+userCode+"','"+dealerCode+"',now()); ";
//
//            resultSet = statement.executeUpdate(sql_query);
//            if(resultSet == 1){
//                result = true;
//            }else{
//                result = false;
//            }
//        }catch (SQLException ex){
//               String error = ex.getMessage();
//               showAlert("Error",error);
//        }finally {
//            try {
//                conn.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
//    }
//
//    public void showAlert(String title,String message){
//        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SaleProductToOutlet_backup.this);
//
//        dlgAlert.setMessage(message);
//        dlgAlert.setTitle(title);
//        dlgAlert.setPositiveButton("OK", null);
//        dlgAlert.setCancelable(true);
//        dlgAlert.create().show();
//    }
//}
