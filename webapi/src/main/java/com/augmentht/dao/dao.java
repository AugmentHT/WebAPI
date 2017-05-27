package com.augmentht.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by dkochar on 5/20/2017.
 */
public class dao {
    public static void main(String... args){
        //dao testdao = new dao();
        SprJDao dao = new SprJDao();
    }

/*
    private final String serverip = "localhost";
    private final String serverport = "3306";
    private final String path = ""; //start with /
    private final String username = "apiapplication";
    private final String pasword = "p@ssword22";


    Connection con;


    dao(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://"+serverip+":"+serverport+path,username,pasword);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select count(*) from ocean.ehrapplication");

        }catch (Exception e){
            System.out.println("Failed db connection");
        }
    }

*/

}
