package com.ajcloud.wansview.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by mamengchao on 2018/05/14.
 */
@Entity
public class Test {
    @Id
    private int id;
    private String name;
    @Generated(hash = 1543324089)
    public Test(int id, String name) {
        this.id = id;
        this.name = name;
    }
    @Generated(hash = 372557997)
    public Test() {
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
