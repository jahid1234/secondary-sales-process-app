package com.example.root.kfilsecondarysales.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
import com.example.root.kfilsecondarysales.R;

import java.text.DecimalFormat;

import static com.example.root.kfilsecondarysales.Activity.LoginActivity.userCode;

public class PullerSalesStatus extends AppCompatActivity {

    int outletNumber = 0;
    SqliteDbHelper sqliteDbHelper;
    Toolbar toolbar;
    TextView totalOutlets,visitedOutletd,strikeRate,lpcCount,totalSalePrice,totalCollectedCash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puller_sales_status);

        sqliteDbHelper = new SqliteDbHelper(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PullerSalesStatus.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        uiInitialise();
        getTargetOutletNumber();
        getTotalCashCollection();
    }


    private void uiInitialise(){
        totalOutlets   = findViewById(R.id.target_outlet_number);
        visitedOutletd = findViewById(R.id.visited_outlet_number);
        strikeRate     = findViewById(R.id.strike_rate);
        lpcCount       = findViewById(R.id.lpc_count);
        totalSalePrice = findViewById(R.id.total_sale);
        totalCollectedCash = findViewById(R.id.collected_cash_amount);
    }

    private void getTargetOutletNumber(){

        Cursor result = sqliteDbHelper.getTargetOutletNumber(userCode);
        if(result.moveToFirst()){
            do{
                outletNumber  = result.getInt(0);
            }while(result.moveToNext());
        }

        totalOutlets.setText(String.valueOf(outletNumber));
        getPullerState();
    }

    private void getPullerState(){
        int visitedOutletNumber = 0,lpc = 0;
        double totalSale = 0;
        Cursor result = sqliteDbHelper.getPullerState(userCode);
        if(result.moveToFirst()){
            do{
                visitedOutletNumber  = result.getInt(0);
                lpc                  = result.getInt(1);
                totalSale            = result.getDouble(2);
            }while(result.moveToNext());
        }

        visitedOutletd.setText(String.valueOf(visitedOutletNumber));
        double strRate = ((double) visitedOutletNumber)/outletNumber;
//        DecimalFormat df = new DecimalFormat();
//        df.setMaximumFractionDigits(2);
        String sr = String.valueOf(strRate);
        strikeRate.setText(sr);
        lpcCount.setText(String.valueOf(lpc));
        totalSalePrice.setText(String.valueOf(totalSale));
    }


    private void getTotalCashCollection(){

        double collectedCash = 0;
        Cursor result = sqliteDbHelper.getPullerCashCollection(userCode);
        if(result.moveToFirst()){
            do{
                collectedCash  = result.getDouble(0);
            }while(result.moveToNext());
        }

        totalCollectedCash.setText(String.valueOf(collectedCash));
    }
}
