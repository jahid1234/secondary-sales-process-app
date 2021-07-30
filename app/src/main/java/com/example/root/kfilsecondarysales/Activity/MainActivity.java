package com.example.root.kfilsecondarysales.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

import com.example.root.kfilsecondarysales.R;

public class MainActivity extends AppCompatActivity {

    CardView asoBtn,dealerBtn,pullerBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        asoBtn = findViewById(R.id.asocardId);
        dealerBtn = findViewById(R.id.dealercardId);
        pullerBtn = findViewById(R.id.pullercardId);

        buttonClick();
    }

    public void buttonClick(){
        asoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                intent.putExtra("roleName","aso");
                startActivity(intent);
                finish();
            }
        });

        dealerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                intent.putExtra("roleName","dealer");
                startActivity(intent);
                finish();
            }
        });

        pullerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                intent.putExtra("roleName","puller");
                startActivity(intent);
                finish();
            }
        });
    }
}
