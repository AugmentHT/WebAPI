package com.augmentht.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by dkochar on 5/23/2017.
 */
public class SprJDao {
    private JdbcTemplate jdbcTemplate = null;
    private String serverip = null;
    private String serverport = null;
    private String path = null;
    private String username = null;
    private String password = null;
    private boolean status = false;

    public SprJDao() {
        this.setProps();
        if (status) {
            DriverManagerDataSource datasource = new DriverManagerDataSource();
            datasource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            String urlpath = "jdbc:mysql://".concat(serverip).concat(":").concat(serverport).concat(path).concat("?verifyServerCertificate=false&useSSL=true");
            System.out.println(urlpath);
            datasource.setUrl(urlpath);
            datasource.setUsername(username);
            datasource.setPassword(password);
            this.jdbcTemplate = new JdbcTemplate(datasource);
           // this.cleanUp();
        } else {
            System.out.println("props not read");
        }
    }

    private void setProps() {
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

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        } finally {
            status = true;
        }
    }

    void cleanUp() {
        this.jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
                this.jdbcTemplate.execute("TRUNCATE ocean.careopportunityreceiveddata");
               this.jdbcTemplate.execute( "TRUNCATE ocean.patient");
        this.jdbcTemplate.execute("TRUNCATE ocean.patientaddress");
        this.jdbcTemplate.execute("TRUNCATE ocean.address");
        this.jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
        this.jdbcTemplate.execute("insert into ocean.careopportunityreceiveddata values(\"1\",\"1\",\"54250\",\"1\",\"Referral to neurosurgeon\",\"2\",\"Doctor's note\",\"3423f2g35g53g53\",\"Referral\",\"id1\",\"\",\"\",\"\",\"\",\"\",\"1\",\"2017-05-31 00:00:00\",\"47\");");
    }

    public SqlRowSet fetch(String sql) {
        return this.jdbcTemplate.queryForRowSet(sql);
    }

    public void execute(String sql) {
        this.jdbcTemplate.execute(sql);
    }

}
