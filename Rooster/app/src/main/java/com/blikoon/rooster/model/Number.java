package com.blikoon.rooster.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by gakwaya on 4/16/2016.
 */
public class Number extends RealmObject {

    @PrimaryKey
    private int id;

    private String mNumber;

    public String getmNumber()
    {
        return mNumber;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
