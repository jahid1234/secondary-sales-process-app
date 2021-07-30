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
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.kfilsecondarysales.Adapter.DiscountNameRecyclerViewAdapter;
import com.example.root.kfilsecondarysales.Database.PostgresqlConnection;
import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
import com.example.root.kfilsecondarysales.Modal.OutletDiscountNameModal;
import com.example.root.kfilsecondarysales.R;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.root.kfilsecondarysales.Activity.LoginActivity.userCode;

public class SaleProductToOutlet extends AppCompatActivity {

    Spinner routeListSpinner,outletListSpinner,productListSpinner,unitListSpinner;
    EditText quantity_editText,price_editText,piece_quantity_editText;
    Button addSales_btn,viewSales_btn,confirmSales_btn;

    TextView totalQuantity,totalBoxPrice,totalPiecePrice;
    RecyclerView discountRecyclerView;
   // TextView balanceTextview;
    SqliteDbHelper dbHelper;
    Toolbar toolbar;
    ProgressDialog pd;

    String dealerCode,route_code,route_name,outlet_name,outlet_code,product_name,unit,puller_code;
    String product_quantity;

    int aPerUnit = 1;
    int ID = 0,categoryID = 0;

    double piecePrice = 0;
    double price = 0;
    double t_price = 0;
    double boxQty = 0,pieceQty = 0;
    double totalQty = 0;
    int t_quantity = 0;
    double t_piecePrice = 0,t_boxPrice = 0;
    List<String> routeList = new ArrayList<String>();
    List<String> outletList = new ArrayList<>();
    List<String> productList = new ArrayList<>();
    List<String> productUnitList = new ArrayList<>();
    ArrayList<OutletDiscountNameModal> discountNameArrayList = new ArrayList<>();
    HashMap<String, String> routedataList  = new HashMap<String, String>();
    HashMap<String, String> outletdataList = new HashMap<String, String>();
    HashMap<String,Integer> hashProductId  = new HashMap<>();
    HashMap<String,Integer> hashCategoryId = new HashMap<>();
   // HashMap<String, String> availableProductQuantityHash = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_product_to_outlet);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle!=null){

            outlet_name = (String) bundle.get("selectedoutletName");
            route_name   = (String) bundle.get("selectedroutName");
        }


        dbHelper = new SqliteDbHelper(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaleProductToOutlet.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        initializeUi();


//        routeListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
//        ArrayAdapter adapter1 = ArrayAdapter.createFromResource(this,R.array.route_arrays,R.layout.color_spinner_layout);
//        adapter1.setDropDownViewResource(R.layout.spinner_dropdown_layout);
//        routeListSpinner.setAdapter(adapter1);
//
//
//
//        outletListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
//        ArrayAdapter adapter2 = ArrayAdapter.createFromResource(this,R.array.outlet_arrays,R.layout.color_spinner_layout);
//        adapter2.setDropDownViewResource(R.layout.spinner_dropdown_layout);
//        outletListSpinner.setAdapter(adapter2);
//
//
//
//        productListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
//        ArrayAdapter adapter3 = ArrayAdapter.createFromResource(this,R.array.product_name_arrays,R.layout.color_spinner_layout);
//        adapter3.setDropDownViewResource(R.layout.spinner_dropdown_layout);
//        productListSpinner.setAdapter(adapter3);
//
//
//
//
//        unitListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
//        ArrayAdapter adapter4 = ArrayAdapter.createFromResource(this,R.array.unit_arrays,R.layout.color_spinner_layout);
//        adapter4.setDropDownViewResource(R.layout.spinner_dropdown_layout);
//        unitListSpinner.setAdapter(adapter4);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //loadAvailableProduct();
        AsyncLoadSpinner asyncLoadSpinner = new AsyncLoadSpinner();
        asyncLoadSpinner.execute();
        onQuantityEditTextFocusChange();
        getSelectedSpinnerItem();

        saveAndSendToServer();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle!=null){

            outlet_name = (String) bundle.get("selectedoutletName");
            route_name   = (String) bundle.get("selectedroutName");
        }

    }

    private void initializeUi(){
        routeListSpinner   = findViewById(R.id.route_list_spinner);
        outletListSpinner  = findViewById(R.id.outlet_list_spinner);
        productListSpinner = findViewById(R.id.product_list_spinner);
        unitListSpinner    = findViewById(R.id.unit_list_spinner);

        discountRecyclerView = findViewById(R.id.discount_recyclerview);

        //balanceTextview        = findViewById(R.id.outlet_balance_textView);
        totalBoxPrice          = findViewById(R.id.total_box_price);
        totalPiecePrice        = findViewById(R.id.total_piece_price);
        totalQuantity          = findViewById(R.id.total_quantity_textview);
        piece_quantity_editText = findViewById(R.id.editText_quantity_piece);
        quantity_editText      = findViewById(R.id.editText_quantity);
        price_editText         = findViewById(R.id.editText_price);
        addSales_btn           = findViewById(R.id.add_btn);
        viewSales_btn          = findViewById(R.id.view_btn);
        //confirmSales_btn       = findViewById(R.id.confirm_btn);

        routeListSpinner.setEnabled(true);
        outletListSpinner.setEnabled(true);

        price_editText.setText("0");
    }


        private void onQuantityEditTextFocusChange() {


        quantity_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

               // if(quantity_editText.isActivated()) {
                    if (!quantity_editText.getText().toString().equals("")) {
                        boxQty = Double.parseDouble(quantity_editText.getText().toString());
                        t_boxPrice = boxQty * price;
                        totalBoxPrice.setText(String.valueOf(t_boxPrice));
                        totalQty = pieceQty + (boxQty*aPerUnit);
                        t_quantity = (int) Math.round(totalQty);
                        t_price = t_boxPrice + t_piecePrice;
                        totalQuantity.setText(String.valueOf(t_quantity));
                        price_editText.setText(String.valueOf(t_price));
                    } else {
                        t_boxPrice = 0;
                        boxQty     = 0;
                        totalQty = pieceQty + boxQty;
                        t_quantity = (int) Math.round(totalQty);
                        t_price = t_boxPrice + t_piecePrice;
                        totalBoxPrice.setText(String.valueOf(t_boxPrice));
                        totalQuantity.setText(String.valueOf(t_quantity));
                        price_editText.setText(String.valueOf(t_price));
                    }
               // }else{
//                    t_boxPrice = 0;
//                    totalBoxPrice.setText(String.valueOf(t_boxPrice));
              //  }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

            piece_quantity_editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(!piece_quantity_editText.getText().toString().equals("")){
                        pieceQty = Double.parseDouble(piece_quantity_editText.getText().toString());
                        t_piecePrice = pieceQty * piecePrice;
                        totalPiecePrice.setText(String.valueOf(t_piecePrice));
                        totalQty = pieceQty + boxQty;
                        t_quantity = (int) Math.round(totalQty);
                        totalQuantity.setText(String.valueOf(t_quantity));
                        t_price = t_boxPrice + t_piecePrice;
                        price_editText.setText(String.valueOf(t_price));
                    }else {
                        pieceQty = 0;
                        totalQty = pieceQty + boxQty;
                        t_quantity = (int) Math.round(totalQty);
                        t_piecePrice = 0;
                        t_price = t_boxPrice + t_piecePrice;
                        totalPiecePrice.setText(String.valueOf(t_piecePrice));
                        totalQuantity.setText(String.valueOf(t_quantity));
                        price_editText.setText(String.valueOf(t_price));
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
    }

//    private void loadAvailableProduct(){
//        Cursor result = dbHelper.getAvailableProductAmount();
//        if(result.moveToFirst()){
//            do {
//                String availableProduct = result.getString(0);
//                String availableQuantity = result.getString(1);
//                //dbHelper.insertIntoPullerInventory(availableProduct,Integer.parseInt(availableQuantity));
//                availableProductQuantityHash.put(availableProduct, availableQuantity);
//            }while (result.moveToNext());
//        }
//    }


    private void getSelectedSpinnerItem(){

            routeListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        String selectedRoute = parent.getSelectedItem().toString();
                        route_name = selectedRoute;
                        route_code = routedataList.get(selectedRoute);
                        AsyncLoadOutletSpinner task = new AsyncLoadOutletSpinner();
                        task.execute(route_code);
                    }catch (NullPointerException ex){

                    }catch (ArrayIndexOutOfBoundsException ex){

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


    }

    private void getSelectedOutletSpinner(){

            outletListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        outlet_name = parent.getSelectedItem().toString();
                        outlet_code = outletdataList.get(outlet_name);

                        AsyncLoadOutletDiscount task = new AsyncLoadOutletDiscount();
                        task.execute(outlet_code);
                    }catch(NullPointerException ex){

                    }catch(ArrayIndexOutOfBoundsException ex){

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

    }

    private void getSelectedProductspinner(){

            productListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        product_name = parent.getSelectedItem().toString();
                        ID  = hashProductId.get(product_name);
                        categoryID = hashCategoryId.get(product_name);
                        AsyncLoadPriceSpinner task = new AsyncLoadPriceSpinner();
                        task.execute();
                        productUnitList.clear();
                        loadUnitSpinner(product_name);

                    }catch (NullPointerException ex){

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

    }

    private void getSelectedUnitSpinner(){

           unitListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
               @Override
               public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                   try {
                        unit = parent.getSelectedItem().toString();
                   }catch (Exception ex){

                   }
               }

               @Override
               public void onNothingSelected(AdapterView<?> parent) {

               }
           });

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



    private final class AsyncLoadSpinner extends AsyncTask<String,String,String> {



        String result;
        @Override
        protected String doInBackground(String... strings) {

           // getDealerCode();
            getRouteListFromSqlite();
           // getOutletFromServer();
            result = "Success";
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(SaleProductToOutlet.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Loading ....");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dismissProgressDialog();
            if(s.equals("Success")){
                toastMessage(s);
                loadRouteSpinner();
                getSelectedSpinnerItem();
            }else{
                toastMessage(s);
            }
        }
    }


    private final class AsyncLoadOutletSpinner extends AsyncTask<String,String,String> {



        String result;
        @Override
        protected String doInBackground(String... strings) {

            outletList.clear();
            getOutletFromSqlite();
            productList.clear();
            getProductFromSqlite();
            result = "Success";
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("Success")){
                AsyncLoadOutletSelectedSpinner task = new AsyncLoadOutletSelectedSpinner();
                task.execute();
            }else{
                toastMessage(s);
            }
        }
    }


    private final class AsyncLoadOutletSelectedSpinner extends AsyncTask<String,String,String> {



        String result;
        @Override
        protected String doInBackground(String... strings) {

            result = "Success";
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("Success")){
                // toastMessage(s);
                loadOutletSpinner();
                getSelectedOutletSpinner();
                AsyncLoadProductSelectedSpinner task = new AsyncLoadProductSelectedSpinner();
                task.execute();


                //CalculateTotalOnQuantityInput();
            }else{
                toastMessage(s);
            }
        }
    }


    private final class AsyncLoadProductSelectedSpinner extends AsyncTask<String,String,String> {



        String result;
        @Override
        protected String doInBackground(String... strings) {

            result = "Success";
            return result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equals("Success")){
                loadProductSpinner();
                getSelectedProductspinner();
            }else{
                toastMessage(s);
            }
        }
    }

    private final class AsyncLoadPriceSpinner extends AsyncTask<String,String,String> {
        String r_result;
        @Override
        protected String doInBackground(String... strings) {
            //AclearAll();
            loadUnitAmount();
            Cursor result = dbHelper.getOutletWiseProductPrice(outlet_code,ID);
            if(result.moveToFirst()){
                do{
                    price = result.getDouble(0);
                }while(result.moveToNext());
                return r_result = "Success";
            }
           return r_result = "fail no price found";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s.equals("Success")) {
                    // toastMessage(s);
                  //  addSales_btn.setEnabled(true);
                   // price_editText.setText(String.valueOf(price));
                    AclearAll();
                    if(aPerUnit > 1){
                       // piece_quantity_editText.setEnabled(true);
                         quantity_editText.setEnabled(true);
                         piece_quantity_editText.setEnabled(true);
                         piecePrice = price/aPerUnit;
                         totalBoxPrice.setText(String.valueOf(price));
                         totalPiecePrice.setText(String.valueOf(piecePrice));
                    }else if(aPerUnit == 1){
                       // piece_quantity_editText.setEnabled(true);
                        piecePrice = price;
                        quantity_editText.setEnabled(false);
                        piece_quantity_editText.setEnabled(true);
                        totalPiecePrice.setText(String.valueOf(piecePrice));
                    }
                    //CalculateTotalOnQuantityInput();

                } else {
                    price_editText.setText(String.valueOf('0'));
                    quantity_editText.setEnabled(false);
                    piece_quantity_editText.setEnabled(false);
                    //addSales_btn.setEnabled(false);
                    //toastMessage(s);
                }
            }catch (NullPointerException ex){
                toastMessage(ex.getMessage());
            }
        }
    }
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

    private void AclearAll(){
        quantity_editText.setText("0");
        piece_quantity_editText.setText("0");
        totalBoxPrice.setText("0");
        totalPiecePrice.setText("0");
        totalQuantity.setText("0");
        price_editText.setText("0");
    }

    private void loadUnitAmount(){
        Cursor result = dbHelper.getProductUnitAmount(ID);
        if(result.moveToFirst()){
            do{
                String amountPerUnit = result.getString(0);
                aPerUnit = Integer.parseInt(amountPerUnit);
            }while(result.moveToNext());

        }
    }

    private void getRouteListFromSqlite() {

        Cursor result = dbHelper.getAllRoutes();
        if(result.moveToFirst()){
            do{
                String routeName = result.getString(2);
                String routeCode = result.getString(1);
                routedataList.put(routeName,routeCode);
                routeList.add(routeName);
            }while(result.moveToNext());
        }

    }

    private void getProductFromSqlite(){
        Cursor result = dbHelper.getAllProductsName();
        if(result.moveToFirst()){
            do{
                String productName = result.getString(0);
                int productId  = result.getInt(1);
                int categoryId = result.getInt(2);
                productList.add(productName);
                hashProductId.put(productName,productId);
                hashCategoryId.put(productName,categoryId);
            }while(result.moveToNext());
        }

    }

    private void getOutletFromSqlite(){
        Cursor result = dbHelper.getAllOutlets(route_code);
        if(result.moveToFirst()){
            do{
                String outletName = result.getString(3);
                String outletCode = result.getString(2);
                outletdataList.put(outletName,outletCode);
                outletList.add(outletName);
            }while(result.moveToNext());
        }

    }

    private void loadProductSpinner(){
        productListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
        //System.out.println(routeList);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.color_spinner_layout,productList);

        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        productListSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
    }

    private void loadRouteSpinner(){
        routeListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
        //System.out.println(routeList);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.color_spinner_layout,routeList);

        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        routeListSpinner.setAdapter(dataAdapter);
        if (route_name != null) {
            int spinnerPosition = dataAdapter.getPosition(route_name);
            routeListSpinner.setSelection(spinnerPosition);
        }

        dataAdapter.notifyDataSetChanged();

//        String getSpinnerItem = routeListSpinner.getSelectedItem().toString();
//        toastMessage(getSpinnerItem);
    }

    private void loadOutletSpinner(){
        outletListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
        //System.out.println(routeList);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.color_spinner_layout,outletList);

        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        outletListSpinner.setAdapter(dataAdapter);
        if (outlet_name != null) {
            int spinnerPosition = dataAdapter.getPosition(outlet_name);
            outletListSpinner.setSelection(spinnerPosition);
        }

        dataAdapter.notifyDataSetChanged();

//        String getSpinnerItem = outletListSpinner.getSelectedItem().toString();
//        toastMessage(getSpinnerItem);
        //outletList.clear();
    }

    private void loadUnitSpinner(String productName){
        String selectedProductUnit = "";
        SqliteDbHelper sqliteDbHelper = new SqliteDbHelper(this);
        Cursor cursor = sqliteDbHelper.getUnitFromDealerInventory(productName);
        if(cursor.moveToFirst()){
             selectedProductUnit = cursor.getString(0);
             productUnitList.add(selectedProductUnit);
        }

        unitListSpinner.getBackground().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
        //System.out.println(routeList);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.color_spinner_layout,productUnitList);

        dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        unitListSpinner.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        unitListSpinner.setEnabled(false);
        getSelectedUnitSpinner();
    }


    private void toastMessage(String msg){
        Toast.makeText(SaleProductToOutlet.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void saveAndSendToServer(){

        final SqliteDbHelper dbHelper = new SqliteDbHelper(this);

        addSales_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                //checkBalance();
                product_quantity = totalQuantity.getText().toString();
                if(product_quantity.isEmpty()|| product_quantity.equals("") && price >0|| product_quantity.equals("0")){
                    quantity_editText.setError("Please add Quantity or No price Added");
                }else{

                    Cursor result = dbHelper.searchDuplicateProduct(ID);
                    if(result.moveToFirst()){
                        showAlert("Duplicate Product","You Try To Add Same Product Again Please Delete The Product From List And Add Again");
                    }else{
                        int p_qnt = Integer.parseInt(product_quantity);
                        //boolean res =  dbHelper.insertOutletSales(route_name,outlet_name,outlet_code,product_name,p_qnt,unit,userCode,t_price);
                        boolean res1 =  dbHelper.insertSalesRecordPuller(route_name,outlet_name,outlet_code,product_name,p_qnt,unit,userCode,t_price,ID,categoryID);
                        if(res1){
                            toastMessage("sqlite insertion Successfull");
                            routeListSpinner.setEnabled(false);
                            outletListSpinner.setEnabled(false);
                        }else{
                            toastMessage("sqlite Failed");
                        }

                    }


                    quantity_editText.setText("");

                }


            }
        });

//        confirmSales_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                product_quantity = quantity_editText.getText().toString();
//
////                if(product_quantity.isEmpty()){
////                    quantity_editText.setError("Please add Quantity");
////                }
////
////               // AsyncSendSalesToServer salesTask = new AsyncSendSalesToServer();
////                //salesTask.execute();
////
////                quantity_editText.setText("");
//            }
//        });


        viewSales_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaleProductToOutlet.this,SalesRecordOfPuller.class);
               // intent.putExtra("map", availableProductQuantityHash);
                intent.putExtra("outletID",outlet_code);
                intent.putExtra("outletName",outlet_name);
                intent.putExtra("routName",route_name);
                startActivity(intent);
                finish();
            }
        });


    }


//    private void checkBalance(){
//        int availableQuantity = 0;
//        String inputQuantity = quantity_editText.getText().toString();
//
//        if(inputQuantity.equals("")){
//
//        }else {
//            try{
//                    availableQuantity = Integer.parseInt(availableProductQuantityHash.get(product_name));
//                    int inputProductQuantity = Integer.parseInt(inputQuantity);
//
//
//                    int remainingQuantity = availableQuantity-inputProductQuantity;
//
//                    if(remainingQuantity < 0){
//                        quantity_editText.setText("");
//                        toastMessage("Does not have enough product to sale");
//                    }else {
//                        String remqty  = String.valueOf(remainingQuantity);
//                        boolean res = dbHelper.upadtePullerInventory(product_name,remainingQuantity);
//                        availableProductQuantityHash.put(product_name,remqty);
//                    }
//            }catch (ArithmeticException ex){
//                toastMessage("Arithmetic Exception");
//            }catch (Exception ex){
//                toastMessage("Exception");
//            }
//
//        }
//    }


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



//    public void CalculateTotalOnQuantityInput(){
//        quantity_editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//    }


    private final class AsyncLoadOutletDiscount extends AsyncTask<String,String,String> {


        @Override
        protected String doInBackground(String... strings) {
            //productHash.clear();
            discountNameArrayList.clear();
            Cursor cursor = dbHelper.getOutletDiscountName(strings[0]);
            String result = "";
            if(cursor.moveToFirst()){
                do{
                    String discountName = cursor.getString(0);

                    OutletDiscountNameModal discountNameModal = new OutletDiscountNameModal(discountName);
                    discountNameArrayList.add(discountNameModal);
                    //productHash.put(productName,productCode);
                }while(cursor.moveToNext());
                result = "Success";
            }else{
                OutletDiscountNameModal discountNameModal = new OutletDiscountNameModal("No Discount Found");
                discountNameArrayList.add(discountNameModal);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                if(s.equals("Success")){
                    Toast.makeText(SaleProductToOutlet.this, s, Toast.LENGTH_SHORT).show();
                    initRecyclerView();
                }else{
                    initRecyclerView();
                }
            }catch (NullPointerException ex){
                Toast.makeText(SaleProductToOutlet.this, "Error", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        discountRecyclerView.setLayoutManager(linearLayoutManager);
        DiscountNameRecyclerViewAdapter adapter = new DiscountNameRecyclerViewAdapter(SaleProductToOutlet.this,discountNameArrayList);
        discountRecyclerView.setAdapter(adapter);
    }
    public void showAlert(String title,String message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SaleProductToOutlet.this);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}
