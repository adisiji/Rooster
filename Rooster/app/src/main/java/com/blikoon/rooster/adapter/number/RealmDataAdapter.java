package com.blikoon.rooster.adapter.number;

import android.content.Context;

import com.blikoon.rooster.model.Number;

import io.realm.RealmResults;

/**
 * Created by rio on 13/02/16.
 */
public class RealmDataAdapter extends RealmModelAdapter<Number> {
    public RealmDataAdapter(Context context, RealmResults<Number> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}
