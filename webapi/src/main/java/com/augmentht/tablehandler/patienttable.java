package com.augmentht.tablehandler;

import com.augmentht.dao.SprJDao;

/**
 * Created by dkochar on 5/27/2017.
 */
public class patienttable {

    SprJDao dao ;

    public patienttable(SprJDao dao) {
        this.dao = dao;
    }

    public void execute(String sql){
        this.dao.execute(sql);
    }

}
