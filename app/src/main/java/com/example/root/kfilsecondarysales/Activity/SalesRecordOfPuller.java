package com.example.root.kfilsecondarysales.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.root.kfilsecondarysales.Adapter.OutletRecyclerViewAdapter;
import com.example.root.kfilsecondarysales.Adapter.SalesRecordPullerRecyclerViewAdapter;
import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
import com.example.root.kfilsecondarysales.Modal.DiscountCalculationModal;
import com.example.root.kfilsecondarysales.Modal.DiscountDetailsModal;
import com.example.root.kfilsecondarysales.Modal.SalesRecordModal;
import com.example.root.kfilsecondarysales.Modal.SalesRecordPullerModal;
import com.example.root.kfilsecondarysales.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import static com.example.root.kfilsecondarysales.Activity.LoginActivity.userCode;

public class SalesRecordOfPuller extends AppCompatActivity {

    double amount = 0,t_price = 0;
    Button print_btn;
    Toolbar toolbar;
    TextView balanceTextView,totalPriceTextView;
    EditText outletPaymentEditText;

    RecyclerView salesRecordRecyclerView;
    SalesRecordPullerRecyclerViewAdapter adapter;
    SalesRecordPullerModal salesRecordPullerModal;
    SqliteDbHelper dbHelper = new SqliteDbHelper(this);
    ArrayList<SalesRecordPullerModal> salesRecordPullerModalArrayList = new ArrayList<>();
    ArrayList<DiscountDetailsModal> discountDetailsModalsArrayList = new ArrayList<>();
    ArrayList<DiscountCalculationModal> discountCalculationModalArrayList = new ArrayList<>();
   // HashMap<String, String> availableProductQuantityHash = new HashMap<>();

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    // needed for communication to bluetooth device / network
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    String avlDiscount = "";
    String outletId,outletName,routName;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_record_of_puller);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle!=null){
            outletId   = (String) bundle.get("outletID");
            outletName = (String) bundle.get("outletName");
            routName   = (String) bundle.get("routName");
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalesRecordOfPuller.this,SaleProductToOutlet.class);
                intent.putExtra("selectedoutletName",outletName);
                intent.putExtra("selectedroutName",routName);
                startActivity(intent);
                finish();
            }
        });

        balanceTextView = findViewById(R.id.balance_textView);
        totalPriceTextView = findViewById(R.id.totalPrice_textView);
        outletPaymentEditText = findViewById(R.id.outlet_payment_edittext);
        print_btn = findViewById(R.id.print_btn);
        print_btn.setEnabled(false);
        salesRecordRecyclerView = findViewById(R.id.outlet_sales_record_recyclerview);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getDiscount();
        getSalesRecord();
        getOutletBalance();
        clickPrintBtn();
    }

    private void getDiscount(){
        Cursor result = dbHelper.getDiscountDetails(outletId);
        if(result.moveToFirst()){
            do{
                int  discountID = result.getInt(2);
                int targetProductID = result.getInt(4);
                int targetCategoryID = result.getInt(5);
                int targetQty               = result.getInt(6);
                double targetPurchase       = result.getDouble(7);
                int rewardProductID         = result.getInt(8);
                int rewardCategoryID = result.getInt(9);
                int rewardQty               = result.getInt(10);
                double cashDiscount         = result.getDouble(11);
                double percentageDiscount   = result.getDouble(12);
                double flatDiscount         = result.getDouble(13);

                DiscountDetailsModal discountDetailsModal = new DiscountDetailsModal(discountID,targetProductID,targetCategoryID,targetQty,targetPurchase,rewardProductID,rewardCategoryID,rewardQty,cashDiscount,percentageDiscount,flatDiscount);
                discountDetailsModalsArrayList.add(discountDetailsModal);
            }while(result.moveToNext());
            avlDiscount = "found";

        }else{
            avlDiscount = "not found";
        }
    }

    private void getSalesRecord(){
        Cursor result = dbHelper.getSalesRecordPuller(outletId);
        if(result.moveToFirst()){
            do{
                String p_key       = result.getString(0);
                String routeName   = result.getString(1);
                String outletName  = result.getString(2);
                String productName = result.getString(4);
                String quantity    = result.getString(5);
                String unit        = result.getString(6);
                double price       = result.getDouble(10);
                int productID      = result.getInt(11);
                int categoryID     = result.getInt(12);
                t_price       = price + t_price;
                salesRecordPullerModal = new SalesRecordPullerModal(p_key,routeName,outletName,productName,quantity,unit,price,productID,categoryID);
                salesRecordPullerModalArrayList.add(salesRecordPullerModal);
            }while(result.moveToNext());
        }

        if(!salesRecordPullerModalArrayList.isEmpty()){
            print_btn.setEnabled(true);
//            if(avlDiscount.equals("found")) {
//                AsyncCalculateDiscount task = new AsyncCalculateDiscount();
//                task.execute();
//            }
        }
        initRecyclerView();

    }

    private void getOutletBalance(){
        Cursor result = dbHelper.getOutletBalance(outletId);
        if(result.moveToFirst()){
            do{
              // discount = result.getDouble(0);
               amount   = result.getDouble(0);
            }while(result.moveToNext());
        }

        balanceTextView.setText(String.valueOf(amount));
        totalPriceTextView.setText(String.valueOf(t_price));
    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        salesRecordRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SalesRecordPullerRecyclerViewAdapter(salesRecordPullerModalArrayList,SalesRecordOfPuller.this);
        salesRecordRecyclerView.setAdapter(adapter);
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
                    salesRecordRecyclerView.removeAllViews();
                    initRecyclerView();
                }else{
                    adapter.searchFilter(newText);
                }
                return true;
            }
        });

        return true;
    }

    private void clickPrintBtn(){

        print_btn.setOnClickListener(new View.OnClickListener() {
            String msg = "";
            @Override
            public void onClick(View v) {

                if(avlDiscount.equals("found")){
                    AsyncCalculateDiscount task = new AsyncCalculateDiscount();
                    task.execute();
                }else{
                   AsyncPrint task = new AsyncPrint();
                   task.execute();

//                    AsyncSaveOutletSales task1 = new AsyncSaveOutletSales();
//                    task1.execute();
                }

                print_btn.setEnabled(false);
            }
        });
    }


    private final class AsyncCalculateDiscount extends AsyncTask<String,String,String>{

        @Override protected String doInBackground(String... strings) {
            String result = "";
            double t_cash_discount_f_total_purchase = 0;
            int rewd_productID_f_total_purchase     = 0;
            int rewd_product_qty_f_total_purchase   = 0;
            double percentage_discount_f_total_purchase = 0;
            int rewd_product_id_f_target_product    = 0;
            int rewd_product_qty_f_target_product   = 0;
            int rewd_category_id_f_target_product   = 0;
            double flat_discount_f_outlet            = 0;
            int rewd_product_id_f_target_category    = 0;
            int rewd_product_qty_f_target_category   = 0;
            int rewd_category_id_f_target_category   = 0;
            int total_product_qty_under_target_category     = 0;
            double percentage_discount_f_target_product     = 0;
            double cash_discount_f_target_product           = 0;
            double percentage_discount_f_target_category    = 0;
            double cash_discount_f_target_category          = 0;
            if(!salesRecordPullerModalArrayList.isEmpty()){
                for (DiscountDetailsModal modal: discountDetailsModalsArrayList) {
                    if(modal.getTargetPurchase() > 0 && String.valueOf(modal.getTargetPurchase()) != null){
                            if(t_price >= modal.getTargetPurchase()){
                                if( t_price == modal.getTargetPurchase() ){
                                    if(String.valueOf(modal.getRewardProductID()) != null && modal.getRewardProductID() > 0){
                                        rewd_productID_f_total_purchase   = modal.getRewardProductID();
                                        rewd_product_qty_f_total_purchase = modal.getRewardQty();
                                    }else if(modal.getPercentageDiscount() > 0 && String.valueOf(modal.getPercentageDiscount()) != null){
                                        percentage_discount_f_total_purchase = modal.getPercentageDiscount();
                                    }else if(modal.getCashDiscount() > 0 && String.valueOf(modal.getCashDiscount()) != null){
                                        t_cash_discount_f_total_purchase = modal.getCashDiscount();
                                    }
                                }else{
                                    int mul = (int) Math.floor(t_price / modal.getTargetPurchase());
                                    if(String.valueOf(modal.getRewardProductID()) != null && modal.getRewardProductID() > 0){
                                        rewd_productID_f_total_purchase   = modal.getRewardProductID();
                                        rewd_product_qty_f_total_purchase = modal.getRewardQty() * mul;
                                    }else if(modal.getPercentageDiscount() > 0 && String.valueOf(modal.getPercentageDiscount()) != null){
                                        percentage_discount_f_total_purchase = modal.getPercentageDiscount();
                                    }else if(modal.getCashDiscount() > 0 && String.valueOf(modal.getCashDiscount()) != null){
                                        t_cash_discount_f_total_purchase = mul * modal.getCashDiscount();
                                    }
                                }
                            }
                    }else if(String.valueOf(modal.getTargetProductID()) != null && modal.getTargetProductID() > 0){
                        for(SalesRecordPullerModal recordPullerModal: salesRecordPullerModalArrayList){

                            if(recordPullerModal.getProductID() == modal.getTargetProductID()){
                                if(Integer.parseInt(recordPullerModal.getQuantity()) == modal.getTargetQty()){
                                        if(String.valueOf(modal.getRewardProductID()) != null && modal.getRewardProductID() > 0){
                                            rewd_product_id_f_target_product = modal.getRewardProductID();
                                            rewd_product_qty_f_target_product = modal.getRewardQty();
                                        }else if(modal.getPercentageDiscount() > 0 && String.valueOf(modal.getPercentageDiscount()) != null){
                                            percentage_discount_f_target_product = modal.getPercentageDiscount();
                                        }else if(modal.getCashDiscount() > 0 && String.valueOf(modal.getCashDiscount()) != null){
                                            cash_discount_f_target_product = modal.getCashDiscount();
                                        }
                                }else if(Integer.parseInt(recordPullerModal.getQuantity()) > modal.getTargetQty()){
                                   // if(Integer.parseInt(recordPullerModal.getQuantity()) % modal.getTargetQty() == 0){
                                        int p_multiply = (int) Math.floor(Integer.parseInt(recordPullerModal.getQuantity()) / modal.getTargetQty());
                                        if(String.valueOf(modal.getRewardProductID()) != null && modal.getRewardProductID() > 0){
                                            rewd_product_id_f_target_product = modal.getRewardProductID();
                                            rewd_product_qty_f_target_product = modal.getRewardQty() * p_multiply;
                                        }else if(modal.getPercentageDiscount() > 0 && String.valueOf(modal.getPercentageDiscount()) != null){
                                            percentage_discount_f_target_product = modal.getPercentageDiscount();
                                        }else if(modal.getCashDiscount() > 0 && String.valueOf(modal.getCashDiscount()) != null){
                                            cash_discount_f_target_product = modal.getCashDiscount() * p_multiply;
                                        }
                                    //}
                                }
                            }

                        }
                    }else if(modal.getFlatDiscount() > 0 && String.valueOf(modal.getFlatDiscount()) != null){
                        flat_discount_f_outlet = modal.getFlatDiscount();
                    }else if(String.valueOf(modal.getTargetCategoryID()) != null && modal.getTargetCategoryID() > 0){
                        for(SalesRecordPullerModal recordPullerModal: salesRecordPullerModalArrayList) {
                            if (recordPullerModal.getCategoryID() == modal.getTargetCategoryID()) {
                                total_product_qty_under_target_category++;
                                if (total_product_qty_under_target_category == modal.getTargetQty()) {
                                        if (String.valueOf(modal.getRewardProductID()) != null && modal.getRewardProductID() > 0) {
                                            rewd_product_id_f_target_category = modal.getRewardProductID();
                                            rewd_product_qty_f_target_category = modal.getRewardQty();
                                        } else if (modal.getPercentageDiscount() > 0 && String.valueOf(modal.getPercentageDiscount()) != null) {
                                            percentage_discount_f_target_category = modal.getPercentageDiscount();
                                        } else if (modal.getCashDiscount() > 0 && String.valueOf(modal.getCashDiscount()) != null) {
                                            cash_discount_f_target_category = modal.getCashDiscount();
                                        }
                                }else if(total_product_qty_under_target_category > modal.getTargetQty()){
                                    int c_multiply = (int) Math.floor(total_product_qty_under_target_category / modal.getTargetQty());
                                    if (String.valueOf(modal.getRewardProductID()) != null && modal.getRewardProductID() > 0) {
                                        rewd_product_id_f_target_category = modal.getRewardProductID();
                                        rewd_product_qty_f_target_category = modal.getRewardQty() * c_multiply;
                                    } else if (modal.getPercentageDiscount() > 0 && String.valueOf(modal.getPercentageDiscount()) != null) {
                                        percentage_discount_f_target_category = modal.getPercentageDiscount();
                                    } else if (modal.getCashDiscount() > 0 && String.valueOf(modal.getCashDiscount()) != null) {
                                        cash_discount_f_target_category = modal.getCashDiscount() * c_multiply;
                                    }
                                }
                            }

                        }
                    }

//                    finalDiscountCalculation(t_cash_discount_f_total_purchase,rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase
//                            ,percentage_discount_f_total_purchase,rewd_product_id_f_target_product,rewd_product_qty_f_target_product
//                            ,flat_discount_f_outlet,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,percentage_discount_f_target_product,cash_discount_f_target_product,percentage_discount_f_target_category,cash_discount_f_target_category);
//                    if(t_cash_discount_f_total_purchase > 0 || percentage_discount_f_total_purchase >0 || rewd_productID_f_total_purchase ||){
//                        dbHelper.insertIntoReceivedDiscount();
//                    }
                    if(t_cash_discount_f_total_purchase > 0){

                        DiscountCalculationModal calculationModal = new DiscountCalculationModal(t_cash_discount_f_total_purchase,percentage_discount_f_total_purchase,rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase,cash_discount_f_target_product,percentage_discount_f_target_product,rewd_product_id_f_target_product,rewd_product_qty_f_target_product,cash_discount_f_target_category,percentage_discount_f_target_category,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,flat_discount_f_outlet);
                        discountCalculationModalArrayList.add(calculationModal);

                        dbHelper.insertIntoReceivedDiscount(Integer.parseInt(outletId),modal.getDiscountID(),0,0,t_cash_discount_f_total_purchase,0,0);
                        t_cash_discount_f_total_purchase = 0;
                    }else if(percentage_discount_f_total_purchase > 0){

                        DiscountCalculationModal calculationModal = new DiscountCalculationModal(t_cash_discount_f_total_purchase,percentage_discount_f_total_purchase,rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase,cash_discount_f_target_product,percentage_discount_f_target_product,rewd_product_id_f_target_product,rewd_product_qty_f_target_product,cash_discount_f_target_category,percentage_discount_f_target_category,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,flat_discount_f_outlet);
                        discountCalculationModalArrayList.add(calculationModal);

                        dbHelper.insertIntoReceivedDiscount(Integer.parseInt(outletId),modal.getDiscountID(),0,0,0,percentage_discount_f_total_purchase,0);
                        percentage_discount_f_total_purchase = 0;
                    }else if(rewd_productID_f_total_purchase > 0){

                        DiscountCalculationModal calculationModal = new DiscountCalculationModal(t_cash_discount_f_total_purchase,percentage_discount_f_total_purchase,rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase,cash_discount_f_target_product,percentage_discount_f_target_product,rewd_product_id_f_target_product,rewd_product_qty_f_target_product,cash_discount_f_target_category,percentage_discount_f_target_category,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,flat_discount_f_outlet);
                        discountCalculationModalArrayList.add(calculationModal);

                        dbHelper.insertIntoReceivedDiscount(Integer.parseInt(outletId),modal.getDiscountID(),rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase,0,0,0);
                        rewd_productID_f_total_purchase = 0;
                        rewd_product_qty_f_total_purchase = 0;
                    }else if(cash_discount_f_target_product > 0){

                        DiscountCalculationModal calculationModal = new DiscountCalculationModal(t_cash_discount_f_total_purchase,percentage_discount_f_total_purchase,rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase,cash_discount_f_target_product,percentage_discount_f_target_product,rewd_product_id_f_target_product,rewd_product_qty_f_target_product,cash_discount_f_target_category,percentage_discount_f_target_category,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,flat_discount_f_outlet);
                        discountCalculationModalArrayList.add(calculationModal);

                        dbHelper.insertIntoReceivedDiscount(Integer.parseInt(outletId),modal.getDiscountID(),0,0,cash_discount_f_target_product,0,0);
                        cash_discount_f_target_product = 0;
                    }else if(percentage_discount_f_target_product > 0){

                        DiscountCalculationModal calculationModal = new DiscountCalculationModal(t_cash_discount_f_total_purchase,percentage_discount_f_total_purchase,rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase,cash_discount_f_target_product,percentage_discount_f_target_product,rewd_product_id_f_target_product,rewd_product_qty_f_target_product,cash_discount_f_target_category,percentage_discount_f_target_category,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,flat_discount_f_outlet);
                        discountCalculationModalArrayList.add(calculationModal);

                        dbHelper.insertIntoReceivedDiscount(Integer.parseInt(outletId),modal.getDiscountID(),0,0,0,percentage_discount_f_target_product,0);
                        percentage_discount_f_target_product = 0;
                    }else if(rewd_product_id_f_target_product > 0){

                        DiscountCalculationModal calculationModal = new DiscountCalculationModal(t_cash_discount_f_total_purchase,percentage_discount_f_total_purchase,rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase,cash_discount_f_target_product,percentage_discount_f_target_product,rewd_product_id_f_target_product,rewd_product_qty_f_target_product,cash_discount_f_target_category,percentage_discount_f_target_category,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,flat_discount_f_outlet);
                        discountCalculationModalArrayList.add(calculationModal);

                        dbHelper.insertIntoReceivedDiscount(Integer.parseInt(outletId),modal.getDiscountID(),rewd_product_id_f_target_product,rewd_product_qty_f_target_product,0,0,0);
                        rewd_product_id_f_target_product = 0;
                        rewd_product_qty_f_target_product = 0;
                    }else if(cash_discount_f_target_category > 0){

                        DiscountCalculationModal calculationModal = new DiscountCalculationModal(t_cash_discount_f_total_purchase,percentage_discount_f_total_purchase,rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase,cash_discount_f_target_product,percentage_discount_f_target_product,rewd_product_id_f_target_product,rewd_product_qty_f_target_product,cash_discount_f_target_category,percentage_discount_f_target_category,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,flat_discount_f_outlet);
                        discountCalculationModalArrayList.add(calculationModal);

                        dbHelper.insertIntoReceivedDiscount(Integer.parseInt(outletId),modal.getDiscountID(),0,0,cash_discount_f_target_category,0,0);
                        cash_discount_f_target_category = 0;
                    }else if(percentage_discount_f_target_category > 0){

                        DiscountCalculationModal calculationModal = new DiscountCalculationModal(t_cash_discount_f_total_purchase,percentage_discount_f_total_purchase,rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase,cash_discount_f_target_product,percentage_discount_f_target_product,rewd_product_id_f_target_product,rewd_product_qty_f_target_product,cash_discount_f_target_category,percentage_discount_f_target_category,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,flat_discount_f_outlet);
                        discountCalculationModalArrayList.add(calculationModal);

                        dbHelper.insertIntoReceivedDiscount(Integer.parseInt(outletId),modal.getDiscountID(),0,0,0,percentage_discount_f_target_category,0);
                        percentage_discount_f_target_category = 0;
                    }else if(rewd_product_id_f_target_category > 0){

                        DiscountCalculationModal calculationModal = new DiscountCalculationModal(t_cash_discount_f_total_purchase,percentage_discount_f_total_purchase,rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase,cash_discount_f_target_product,percentage_discount_f_target_product,rewd_product_id_f_target_product,rewd_product_qty_f_target_product,cash_discount_f_target_category,percentage_discount_f_target_category,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,flat_discount_f_outlet);
                        discountCalculationModalArrayList.add(calculationModal);

                        dbHelper.insertIntoReceivedDiscount(Integer.parseInt(outletId),modal.getDiscountID(),rewd_product_id_f_target_category,rewd_product_qty_f_target_category,0,0,0);
                        rewd_product_id_f_target_category = 0;
                        rewd_product_qty_f_target_category = 0;
                    }else if(flat_discount_f_outlet > 0){

                        DiscountCalculationModal calculationModal = new DiscountCalculationModal(t_cash_discount_f_total_purchase,percentage_discount_f_total_purchase,rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase,cash_discount_f_target_product,percentage_discount_f_target_product,rewd_product_id_f_target_product,rewd_product_qty_f_target_product,cash_discount_f_target_category,percentage_discount_f_target_category,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,flat_discount_f_outlet);
                        discountCalculationModalArrayList.add(calculationModal);

                        dbHelper.insertIntoReceivedDiscount(Integer.parseInt(outletId),modal.getDiscountID(),0,0,0,0,flat_discount_f_outlet);
                        flat_discount_f_outlet = 0;
                    }

//                    DiscountCalculationModal calculationModal = new DiscountCalculationModal(t_cash_discount_f_total_purchase,percentage_discount_f_total_purchase,rewd_productID_f_total_purchase,rewd_product_qty_f_total_purchase,cash_discount_f_target_product,percentage_discount_f_target_product,rewd_product_id_f_target_product,rewd_product_qty_f_target_product,cash_discount_f_target_category,percentage_discount_f_target_category,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,flat_discount_f_outlet);
//                    discountCalculationModalArrayList.add(calculationModal);
                }


                result = "calculated";
            }else{
                result = "no calculation";
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
            try {
                if(s.equals("calculated")){
                   AsyncPrint task = new AsyncPrint();
                   task.execute();
//                    AsyncSaveOutletSales task1 = new AsyncSaveOutletSales();
//                    task1.execute();

                }else{
                    showAlert("Warning","no product to calculate Discount");
                }

            } catch (NullPointerException ex) {
                showAlert("Error",ex.getMessage());
            }
        }
    }


//    private void finalDiscountCalculation(double t_cash_discount_f_total_purchase,int rewd_productID_f_total_purchase,int rewd_product_qty_f_total_purchase
//            ,double percentage_discount_f_total_purchase, int rewd_product_id_f_target_product,int rewd_product_qty_f_target_product
//            ,double flat_discount_f_outlet,int rewd_product_id_f_target_category,int rewd_product_qty_f_target_category
//            ,double percentage_discount_f_target_product,double cash_discount_f_target_product,double percentage_discount_f_target_category,double cash_discount_f_target_category){
//
//        double  m_cash_discount_f_total_purchase = 0;
//        double  m_percentage_discount_f_total_purchase = 0;
//        int m_rewd_productID_f_total_purchase = 0 ;
//        int m_rewd_product_qty_f_total_purchase = 0;
//
//        double  m_cash_discount_f_target_product = 0;
//        double  m_percentage_discount_f_target_product = 0;
//        int m_rewd_product_id_f_target_product = 0 ;
//        int m_rewd_product_qty_f_target_product = 0;
//
//        double  m_cash_discount_f_target_category = 0;
//        double  m_percentage_discount_f_target_category = 0;
//        int m_rewd_productID_f_target_category = 0 ;
//        int m_rewd_product_qty_f_target_category = 0;
//
//        double m_flat_discount = 0;
//
//        if(t_cash_discount_f_total_purchase != 0){
//            m_cash_discount_f_total_purchase = t_cash_discount_f_total_purchase;
//        }
//        if(percentage_discount_f_total_purchase !=0){
//            m_percentage_discount_f_total_purchase = percentage_discount_f_total_purchase;
//        }
//        if(rewd_productID_f_total_purchase != 0){
//            m_rewd_productID_f_total_purchase = rewd_productID_f_total_purchase;
//            m_rewd_product_qty_f_total_purchase = rewd_product_qty_f_total_purchase;
//        }
//        if(cash_discount_f_target_product != 0){
//            m_cash_discount_f_target_product = cash_discount_f_target_product;
//        }
//        if(percentage_discount_f_target_product != 0){
//            m_percentage_discount_f_target_product = percentage_discount_f_target_product;
//        }
//        if(rewd_product_id_f_target_product != 0){
//            m_rewd_product_id_f_target_product = rewd_product_id_f_target_product;
//            m_rewd_product_qty_f_target_product = rewd_product_qty_f_target_product;
//        }
//        if(cash_discount_f_target_category != 0){
//            m_cash_discount_f_target_category = cash_discount_f_target_category;
//        }
//        if(percentage_discount_f_target_category != 0){
//            m_percentage_discount_f_target_category = percentage_discount_f_target_category;
//        }
//        if(rewd_product_id_f_target_category != 0){
//            m_rewd_productID_f_target_category = rewd_product_id_f_target_category;
//            m_rewd_product_qty_f_target_category = rewd_product_qty_f_target_category;
//        }
//        if(flat_discount_f_outlet != 0 ){
//            m_flat_discount = flat_discount_f_outlet;
//        }
//
//        DiscountCalculationModal calculationModal = new DiscountCalculationModal(m_cash_discount_f_total_purchase,m_percentage_discount_f_total_purchase,m_rewd_productID_f_total_purchase,m_rewd_product_qty_f_total_purchase,m_cash_discount_f_target_product,m_percentage_discount_f_target_product,m_rewd_product_id_f_target_product,m_rewd_product_qty_f_target_product,m_cash_discount_f_target_category,percentage_discount_f_target_category,rewd_product_id_f_target_category,rewd_product_qty_f_target_category,flat_discount_f_outlet);
//        discountCalculationModalArrayList.add(calculationModal);
//    }

    private final class AsyncPrint extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            String blutoothOn = "";
            try {
                boolean isEnabled = findBT();
                if(isEnabled){
                  String c = openBT();
                  if(c.equals("Got Printer")){
                      sendData();
                      return c;
                  }else {
                      return c;
                  }

                }else {
                    blutoothOn = "No";
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return blutoothOn;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if(s.equals("Got Printer")){
                    closeBT();
                    // salesRecordPullerModalArrayList.clear();
                    AsyncSaveOutletSales task1 = new AsyncSaveOutletSales();
                    task1.execute();
//                    salesRecordPullerModalArrayList.clear();
//                    initRecyclerView();
//                    dbHelper.deleteSalesRecordOfPuller();
                   // print_btn.setEnabled(false);
                }else{
                    showAlert("Warning","Please Connect Printer");
                }

            } catch (IOException ex) {
                showAlert("Error",ex.getMessage());
            }
        }
    }


    private final class AsyncSaveOutletSales extends AsyncTask<String,String,String>{

        @Override protected String doInBackground(String... strings) {
            String result = "";

            if(!salesRecordPullerModalArrayList.isEmpty()){
                for(int i = 0;i<salesRecordPullerModalArrayList.size();i++) {
                    boolean res = dbHelper.insertOutletSales(salesRecordPullerModalArrayList.get(i).getRoutename(), salesRecordPullerModalArrayList.get(i).getOutletName(),outletId, salesRecordPullerModalArrayList.get(i).getProductName(), Integer.parseInt(salesRecordPullerModalArrayList.get(i).getQuantity()), salesRecordPullerModalArrayList.get(i).getUnit(), userCode, salesRecordPullerModalArrayList.get(i).getPrice(),salesRecordPullerModalArrayList.get(i).getProductID());
                    result = "inserted";
                }

                updateOutletBalance();
            }else{
                result = "Error salesRecord Save";
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
            try {
                if(s.equals("inserted")){
                    //closeBT();
                    // salesRecordPullerModalArrayList.clear();
                    salesRecordPullerModalArrayList.clear();
                    initRecyclerView();
                    dbHelper.deleteSalesRecordOfPuller();
                    // print_btn.setEnabled(false);
                }else{
                    showAlert("Warning","Outlet Sales not updated");
                }

            } catch (NullPointerException ex) {
                showAlert("Error",ex.getMessage());
            }
        }
    }


    // this will find a bluetooth printer device
    private boolean findBT() {
        boolean isEnabled = false;
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter == null) {
              //  Toast.makeText(this, "No Device Found", Toast.LENGTH_SHORT).show();
                isEnabled = false;
            }

            if(!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if(pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // RPP300 is the name of the bluetooth printer device
                    // we got this name from the list of paired devices
                    if (device.getName().equals("Qsprinter")) {
                        mmDevice = device;
                        isEnabled = true;
                        return isEnabled;
                        //break;
                    }
                }
            }



        }catch(Exception e){
            e.printStackTrace();
        }
        return isEnabled;
    }

    // tries to open a connection to the bluetooth printer device
    private String openBT() throws IOException {
        String checkPrinter = "";
        try {

            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();
            checkPrinter = "Got Printer";
           // Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            checkPrinter = e.getMessage();
        }
        return checkPrinter;
    }


    /*
* after opening a connection to bluetooth printer device,
* we have to listen and check if a data were sent to be printed.
*/
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // this is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {

                    while (!Thread.currentThread().isInterrupted() && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();

                            if (bytesAvailable > 0) {

                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);

                                for (int i = 0; i < bytesAvailable; i++) {

                                    byte b = packetBytes[i];
                                    if (b == delimiter) {

                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(
                                                readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length
                                        );

                                        // specify US-ASCII encoding
                                        final String data = new String(encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        // tell the user data were sent to bluetooth printer device
                                        handler.post(new Runnable() {
                                            public void run() {
                                              //  myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // this will send text data to be printed by the bluetooth printer
    void sendData() throws IOException {
        try {
           // String upToNCharacters = s.substring(0, Math.min(s.length(), n));
            // the text typed by the user
            double final_price = t_price;
            double percentage_discount_purchase = 0;
            double percentage_discount_product = 0;
            double percentage_discount_category = 0;
            double cash_discount_f_purchase = 0;
            double cash_discount_f_product = 0;
            double cash_discount_f_category = 0;
            double flat_discount = 0;


            String msg = "";
            msg = "RouteName :"+salesRecordPullerModalArrayList.get(0).getRoutename()+"\n Outlet Name : "+ salesRecordPullerModalArrayList.get(0).getOutletName()+"\n\n\n\n";
            msg += "Pro.Na    Qty   unit    pri \n";

            for (SalesRecordPullerModal modal: salesRecordPullerModalArrayList) {
                msg += ""+modal.getProductName().substring(0,Math.min(modal.getProductName().length(),4))+"     "+modal.getQuantity()+"     "+modal.getUnit()+"    "+modal.getPrice()+"\n";
            }

            msg +="\n\n";
            msg += "Total Price :"+t_price+"Taka \n";
           // msg += "Discount per  :"+discount+" %\n";
            //msg += "Total Dis :"+t_price *(discount/100)+" Taka \n";
            for(DiscountCalculationModal modal: discountCalculationModalArrayList){
                if(modal.getM_cash_discount_f_total_purchase() > 0){

                    cash_discount_f_purchase += modal.getM_cash_discount_f_total_purchase();
                    msg += "Cash Dis on Purchase:"+modal.getM_cash_discount_f_total_purchase()+"Taka \n";
                   // modal.setM_cash_discount_f_total_purchase(0);
                }else if(modal.getM_percentage_discount_f_total_purchase() >0){
                    percentage_discount_purchase += modal.getM_percentage_discount_f_total_purchase()/100;
                    msg += "Percnt. Dis on Purchase:"+modal.getM_percentage_discount_f_total_purchase()+"% "+final_price * percentage_discount_purchase+" Taka \n";

                    //modal.setM_percentage_discount_f_total_purchase(0);
                }else if(modal.getM_rewd_productID_f_total_purchase() > 0){
                    msg += "Rwd pro on Purchase:"+modal.getM_rewd_productID_f_total_purchase()+" qty: "+modal.getM_rewd_product_qty_f_total_purchase()+" \n";
                   // msg += "Pro qty:"+modal.getM_rewd_product_qty_f_total_purchase()+" \n";
                   // modal.setM_rewd_productID_f_total_purchase(0);
                }else if(modal.getM_cash_discount_f_target_product() > 0){
                    cash_discount_f_product += modal.getM_cash_discount_f_target_product();
                    msg += "Cash Dis on Product:"+modal.getM_cash_discount_f_target_product()+"Taka \n";

                    //modal.setM_cash_discount_f_target_product(0);
                }else if(modal.getM_percentage_discount_f_target_product() > 0){
                    percentage_discount_product += modal.getM_percentage_discount_f_target_product()/100;
                    msg += "Percnt. Dis on Product:"+modal.getM_percentage_discount_f_target_product()+"% "+final_price * percentage_discount_product+" Taka\n";

                   // modal.setM_percentage_discount_f_target_product(0);
                }else if(modal.getM_rewd_productID_f_target_product() > 0){
                    msg += "Rwd pro on Product:"+modal.getM_rewd_productID_f_target_product()+" qty: "+modal.getM_rewd_product_qty_f_target_product()+" \n";
                    //msg += "Pro qty:"+modal.getM_rewd_product_qty_f_target_product()+" \n";
                    //modal.setM_rewd_productID_f_target_product(0);
                }else if(modal.getM_cash_discount_f_target_category() > 0){
                    cash_discount_f_category += modal.getM_cash_discount_f_target_category();
                    msg += "Cash Dis on Category:"+modal.getM_cash_discount_f_target_category()+"Taka \n";

                   // modal.setM_cash_discount_f_target_category(0);
                }else if(modal.getM_percentage_discount_f_target_category() > 0){
                    percentage_discount_category += modal.getM_percentage_discount_f_target_category()/100;
                    msg += "Percnt. Dis on Category:"+modal.getM_percentage_discount_f_target_category()+"% "+final_price * percentage_discount_category+" Taka \n";

                   // modal.setM_percentage_discount_f_target_category(0);
                }else if(modal.getM_rewd_productID_f_target_category() > 0){
                    msg += "Rwd pro on category:"+modal.getM_rewd_productID_f_target_category()+" qty:"+modal.getM_rewd_productID_f_target_category()+" \n";
                   // msg += "Pro qty:"+modal.getM_rewd_product_qty_f_target_category()+" \n";
                   // modal.setM_rewd_productID_f_target_category(0);
                } else if (modal.getM_flat_discount() > 0){
                    flat_discount += modal.getM_flat_discount()/100;
                    msg += "Flat Dis"+modal.getM_flat_discount()+" % "+(double)Math.round((final_price * flat_discount) * 1000d) / 1000d+" Taka \n";

                    //modal.setM_flat_discount(0);
                }
            }

//            if(cash_discount_f_purchase > 0){
//                msg += "Cash Dis. For Trg Purchase:"+cash_discount_f_purchase+"Taka \n";
//            }
//            if(cash_discount_f_product > 0){
//                msg += "Cash Dis. For Trg Product:"+cash_discount_f_product+"Taka \n";
//            }
//            if(cash_discount_f_category > 0){
//                msg += "Cash Dis. For Trg Category:"+ cash_discount_f_category+"Taka \n";
//            }
//            if(percentage_discount_purchase > 0){
//                msg += "percnt Dis. For Trg Purchase:"+final_price * percentage_discount_purchase+"Taka \n";
//            }
//            if(percentage_discount_product > 0){
//                msg += "percnt Dis. For Trg Product:"+final_price * percentage_discount_product+"Taka \n";
//            }
//            if(percentage_discount_category  > 0){
//                msg += "percnt Dis. For Trg Category:"+final_price * percentage_discount_category+"Taka \n";
//            }
//            if(flat_discount > 0){
//                msg += "Flat Dis. For Outlet:"+final_price * flat_discount+"Taka \n";
//            }

            double total_discount = cash_discount_f_purchase + cash_discount_f_product + cash_discount_f_category +(final_price * percentage_discount_purchase)+(final_price * percentage_discount_product)+(final_price * percentage_discount_category)+(final_price *flat_discount);
            msg += "Total Discount :"+total_discount+"Taka \n";
            msg += "Final Price :"+(final_price - total_discount)+"Taka \n";
            msg +="\n\n\n\n\n\n";

            mmOutputStream.write(msg.getBytes());

            //mmOutputStream.write(new byte[]{0x1D, 0x56, 0x41, 0x10},1,0);

            // tell the user data were sent


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String title,String message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SalesRecordOfPuller.this);

        dlgAlert.setMessage(message);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    private String updateOutletBalance(){
        String result = "";
        double bal = 0;
        String balance = outletPaymentEditText.getText().toString();
        if(!balance.equals("") || !balance.equals("0")){
            dbHelper.insertIntoOutletCashCollection(userCode,outletId,Double.parseDouble(balance));
            bal = amount - Double.parseDouble(balance);
            boolean res = dbHelper.updateOutletBalance(outletId,bal);
            if(res){
                result = "Success";
            }
        }

        return result;
    }
}
