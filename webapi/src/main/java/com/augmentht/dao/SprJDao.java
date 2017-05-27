package com.augmentht.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * Created by dkochar on 5/23/2017.
 */
public class SprJDao {
    private JdbcTemplate jdbcTemplate=null;
    private String serverip =null;
    private String serverport = null;
    private String path = null;
    private String username = null;
    private String password=null;
    private boolean status = false;

    public SprJDao(){
        //if(jdbcTemplate.equals(null)){
        System.out.println("setting props");
        this.setProps();
        if (status){
            System.out.println("Path"+path+"a");
        DriverManagerDataSource datasource = new DriverManagerDataSource();
        datasource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        String urlpath= "jdbc:mysql://".concat(serverip).concat(":").concat(serverport).concat(path).concat("verifyServerCertificate=false&useSSL=true");
            System.out.println(urlpath);
        datasource.setUrl(urlpath);
        datasource.setUsername(username);
        datasource.setPassword(password);
        this.jdbcTemplate= new JdbcTemplate(datasource);
        this.sample();
        }else{
            System.out.println("props not read");
        }
    }

    private void setProps(){
        try {
            Properties props = new Properties();
            FileInputStream propfile = new FileInputStream("webapi/src/main/java/datasource.properties");
            props.load(propfile);
            propfile.close();
            serverip = props.getProperty("serverip");
            serverport = props.getProperty("serverport");
            path = props.getProperty("path");
            username = props.getProperty("username");
            password = props.getProperty("password");

        }catch (Exception e){
            System.out.println(e.getMessage());
            return;
        }
        finally {
            status = true;
        }
    }

    void sample(){
        this.jdbcTemplate.execute("select * from ocean.address");
    }

   public SqlRowSet fetch(String sql){
       return this.jdbcTemplate.queryForRowSet(sql);
    }

    public void execute(String sql){
        this.jdbcTemplate.execute(sql);
    }

}
