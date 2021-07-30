package com.example.root.kfilsecondarysales.Activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
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

import com.example.root.kfilsecondarysales.Adapter.OutletRecyclerViewAdapter;
import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
import com.example.root.kfilsecondarysales.Modal.RouteModal;
import com.example.root.kfilsecondarysales.Modal.SalesRecordModal;
import com.example.root.kfilsecondarysales.R;

import java.util.ArrayList;

public class SalesOutletList extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView salesListRecyclerView;
    String routeName;

    OutletRecyclerViewAdapter adapter;
    SalesRecordModal salesRecordModal;
    SqliteDbHelper dbHelper = new SqliteDbHelper(this);
    ArrayList<SalesRecordModal> salesRecordModalArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_outlet_list);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalesOutletList.this,RouteListOfOutlets.class);
                startActivity(intent);
                finish();
            }
        });

        salesListRecyclerView = findViewById(R.id.outlet_sales_recyclerview);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null)
        {
            routeName =(String) bundle.get("routeName");

        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getSalesInformation();
    }

    private void getSalesInformation(){
        Cursor result = dbHelper.getOutletSales(routeName);
        if(result.moveToFirst()){
            do{

                String outletNmae  = result.getString(2);
                String productName = result.getString(4);
                String quantity    = result.getString(5);
                String unit        = result.getString(6);

                salesRecordModal = new SalesRecordModal(outletNmae,productName,quantity,unit);
                salesRecordModalArrayList.add(salesRecordModal);
            }while(result.moveToNext());
        }
        initRecyclerView();
    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        salesListRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new OutletRecyclerViewAdapter(salesRecordModalArrayList,SalesOutletList.this);
        salesListRecyclerView.setAdapter(adapter);
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
                    salesListRecyclerView.removeAllViews();
                }else{
                    adapter.searchFilter(newText);
                }
                return true;
            }
        });

        return true;
    }

}
