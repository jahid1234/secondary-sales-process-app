package com.example.root.kfilsecondarysales.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.kfilsecondarysales.Database.PostgresqlConnection;
import com.example.root.kfilsecondarysales.Database.SqliteDbHelper;
import com.example.root.kfilsecondarysales.R;

import java.net.NetworkInterface;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.root.kfilsecondarysales.Activity.HomeActivity.roleName;

public class LoginActivity extends AppCompatActivity {

    String user_role;
    SqliteDbHelper dbHelper;
    EditText employee_editText,password_editText;
    Button login_button;

    ProgressDialog pd;
    CheckBox remember_checkbox;
    private SharedPreferences rememberLoginPreference;
    private SharedPreferences.Editor rememberLoginPreferenceEditor;
    boolean rememberLogin = false;

    String userName,password;
    public static String userCode;
    boolean doublePressBackbtnToExit = false;

    PostgresqlConnection postgresqlConnection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new SqliteDbHelper(this);
        initializeUI();


        //Shared Preference
        rememberLoginPreference = getSharedPreferences("rememberLoginPrefs",MODE_PRIVATE);
        rememberLoginPreferenceEditor = rememberLoginPreference.edit();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle!=null)
        {
            user_role =(String) bundle.get("roleName");

        }

        onclickLoginButton();
       // loadFromSharedPreference();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void initializeUI(){
        employee_editText = findViewById(R.id.employee_editText);
        password_editText = findViewById(R.id.password_editText);

        login_button = findViewById(R.id.login_btn);
        //remember_checkbox = findViewById(R.id.remember_checkbox);
    }

    private void loadFromSharedPreference(){
        rememberLogin = rememberLoginPreference.getBoolean("rememberLogin",false);
        if(rememberLogin == true){
            employee_editText.setText(rememberLoginPreference.getString("username",""));
            password_editText.setText(rememberLoginPreference.getString("password",""));
            remember_checkbox.setChecked(true);
        }
    }

    private void onclickLoginButton(){

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = employee_editText.getText().toString();
                password = password_editText.getText().toString();

                if(userName.isEmpty()){
                    employee_editText.setError("Please Enter Empployee ID");
                    return;
                }

//                if(remember_checkbox.isChecked()){
//
//                    rememberLoginPreferenceEditor.putBoolean("rememberLogin",true);
//                    rememberLoginPreferenceEditor.putString("username",userName);
//                    rememberLoginPreferenceEditor.putString("password",password);
//                    rememberLoginPreferenceEditor.commit();
//                }else {
//                    rememberLoginPreferenceEditor.clear();
//                    rememberLoginPreferenceEditor.commit();
//                }


                try {
                    if (user_role.equals("puller")) {
                        loginPuller();
                    }else{
                        boolean vpn = checkVPN();
                       // if(vpn){
                            AsyncSender task = new AsyncSender();
                            task.execute();
                    //    }else {
                          //  showAlert("Vpn Connection","Please connect Vpn");
                     //   }
                    }
                }catch (NullPointerException ex){
                    toastMessage("No Role Found");
                }


//                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
//                intent.putExtra("roleName","puller");
//                startActivity(intent);
//                finish();
            }
        });

    }

    //Wrapper Class
    public class Wrapper{
        public String result;
        public String role;

    }

    private final class AsyncSender extends AsyncTask<String,String,Wrapper>{
        ResultSet resultSet,resultSet1;

        @Override
        protected Wrapper doInBackground(String... strings) {

            String role_Name = "dealer";
            Wrapper w = new Wrapper();
            postgresqlConnection = new PostgresqlConnection();
            Connection conn = postgresqlConnection.getConn();
            if(conn !=null) {
                try {
                    dbHelper.deleteDealer();

                    if (user_role.equals("dealer")) {
                        Statement statement = conn.createStatement();
                        // String sql_query = "select * from ad_user where name = '" + userName + "' and password = '" + password + "' and role = '"+user_role+"' ";
                        String sql_query = "select * from c_bpartner where username = '" + userName + "' and password = '" + password + "'";
                        resultSet = statement.executeQuery(sql_query);
                        if (resultSet.next()) {

                            w.role = "dealer";
                            userCode = resultSet.getString("c_bpartner_id");

                            w.result = "Success";
                        }else {
                            w.result = "Employee Id or Password did not match";
                        }
                    }else if(user_role.equals("aso")){
                        Statement statement = conn.createStatement();
                        // String sql_query = "select * from ad_user where name = '" + userName + "' and password = '" + password + "' and role = '"+user_role+"' ";
                        String sql_query = "select * from t_asoinfo where username = '" + userName + "' and password = '" + password + "'";
                        resultSet = statement.executeQuery(sql_query);
                        if (resultSet.next()) {

                            w.role = "aso";
                            userCode = resultSet.getString("t_asoinfo_id");

                            Statement statement1 = conn.createStatement();
                            String sql_query1 = "select c_bpartner_id,dealer_balance from t_aso_distributorassign where t_asoinfo_id = '" + Integer.parseInt(userCode) + "'";
                            resultSet1 = statement1.executeQuery(sql_query1);
                            if(resultSet1 !=null) {
                                while (resultSet1.next()) {
                                    String dealerCode = resultSet1.getString("c_bpartner_id");
                                    double balance    = resultSet1.getDouble("dealer_balance");
                                    boolean res = dbHelper.insertDealer(dealerCode,balance);

                                }
                            }
                            w.result = "Success";
                        }else {
                            w.result = "Employee Id or Password did not match";
                        }
                    }

                } catch (SQLException ex) {
                    w.result = ex.getMessage();
                } finally {
                    try {
                        if(conn != null) {
                            conn.close();
                        }else {
                            w.result = "connection failed";
                        }
                    } catch (SQLException e) {
                        w.result = e.getMessage();
                    } catch (NullPointerException ex) {
                        w.result = ex.getMessage();
                    } catch (Exception ex) {
                        w.result = ex.getMessage();
                    }
                }
            }else {
                w.result = "connection refused check host,port,acceptence of tcp/ip connection";
            }

            return w;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog();
        }

        @Override
        protected void onPostExecute(Wrapper w) {
            super.onPostExecute(w);
            dismissProgressDialog();
            try{
                if(w.result.equals("Success")){
                    Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                    intent.putExtra("roleName",w.role);
                    startActivity(intent);
                    finish();
                }else{
                    showAlert("Login error",w.result);
                }
            }catch (NullPointerException ex){
                showAlert("Error",ex.getMessage());
            }

        }
    }


    private void loginPuller(){

        Cursor result =  dbHelper.pullerLogin(userName,password);
        if(result.moveToFirst()){
            userCode = result.getString(0);
            Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
            intent.putExtra("roleName","puller");
            startActivity(intent);
            finish();
        }else{
            showAlert("Login Error","Employee Id or Password did not match");

        }

    }


    @Override
    public void onBackPressed() {
        if(doublePressBackbtnToExit){
            super.onBackPressed();
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        doublePressBackbtnToExit = true;
        toastMessage("Press again to exit");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doublePressBackbtnToExit = false;
            }
        },2000);
    }

    private void toastMessage(String message) {
        //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Toast toast = Toast.makeText(LoginActivity.this,message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    public void showAlert(String title,String message){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(LoginActivity.this);

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
            pd = new ProgressDialog(LoginActivity.this);
            pd.setTitle("Please Wait");
            pd.setMessage("Logging in.....");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
