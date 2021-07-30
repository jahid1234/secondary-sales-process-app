package com.example.root.kfilsecondarysales.Database;

import java.net.ConnectException;
import java.net.SocketException;
import java.nio.channels.ConnectionPendingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by root on 9/4/19.
 */

public class PostgresqlConnection {
    public Connection conn;
    public String error;
    public Connection getConn(){
        try{
            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection("jdbc:postgresql://172.16.9.200:5432/ws_12nov2019", "adempiere", "adempiere");
            //conn = DriverManager.getConnection("jdbc:postgresql://172.16.9.45:5432/sales", "postgres", "adempiere");
            System.out.println(conn);

        }catch(ClassNotFoundException e){
            error = e.getMessage();
        }catch (SQLException sqlex){
            error = sqlex.getMessage();
        }catch (Exception ex){
            error = ex.getMessage();
        }
        return conn;
    }
}
