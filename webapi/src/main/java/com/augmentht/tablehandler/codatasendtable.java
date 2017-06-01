package com.augmentht.tablehandler;

import com.augmentht.dao.SprJDao;

/**
 * Created by dkochar on 5/28/2017.
 */
public class codatasendtable {
    SprJDao dao ;

    public codatasendtable(SprJDao dao) {
        this.dao = dao;
    }

    public void execute(String sql){
        this.dao.execute(sql);
    }
}
