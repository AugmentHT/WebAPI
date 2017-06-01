package com.augmentht.tablehandler;

import com.augmentht.dao.SprJDao;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.ResultSet;
import java.util.Calendar;

/**
 * Created by dkochar on 5/25/2017.
 */
public class careOpportunityReceive {

    SprJDao dao ;

    public careOpportunityReceive(SprJDao dao) {
        this.dao = dao;
    }

    public SqlRowSet getUnprocessedCO(){
      SqlRowSet rs = dao.fetch("select * from ocean.careopportunityreceiveddata where Parameter8=1");
        return rs;
    }

}
