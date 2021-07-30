package com.example.root.kfilsecondarysales.Activity;

import android.content.Intent;
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

import com.example.root.kfilsecondarysales.Adapter.ProductRecyclerViewAdapter;
import com.example.root.kfilsecondarysales.Adapter.RouteRecyclerViewAdapter;
import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
import com.example.root.kfilsecondarysales.Modal.RouteModal;
import com.example.root.kfilsecondarysales.R;

import java.util.ArrayList;

public class RouteListOfOutlets extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView routeRecyclerView;
    RouteRecyclerViewAdapter adapter;
    SqliteDbHelper dbHelper = new SqliteDbHelper(this);
    RouteModal routeModal;
    ArrayList<RouteModal> routeListOfOutletsArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list_of_outlets);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteListOfOutlets.this,HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        routeRecyclerView = findViewById(R.id.route_recyclerview);
        getRouteList();

    }

    private void getRouteList(){
        Cursor result = dbHelper.getRouteList();
        if(result.moveToFirst()){

           do{
               String routeName = result.getString(0);
               routeModal = new RouteModal(routeName);
               routeListOfOutletsArrayList.add(routeModal);
           }while (result.moveToNext());
        }

        initRecyclerView();
    }

    private void initRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        routeRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RouteRecyclerViewAdapter(routeListOfOutletsArrayList,RouteListOfOutlets.this);
        routeRecyclerView.setAdapter(adapter);
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
                    routeRecyclerView.removeAllViews();
                }else{
                    adapter.searchFilter(newText);
                }
                return true;
            }
        });

        return true;
    }
}
