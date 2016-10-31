package com.blikoon.rooster.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blikoon.rooster.AddReceiverDialog_Fragment;
import com.blikoon.rooster.R;
import com.blikoon.rooster.adapter.number.NumFilterAdapter;
import com.blikoon.rooster.adapter.number.RealmDataAdapter;
import com.blikoon.rooster.model.Number;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by neobyte on 10/24/2016.
 */

public class NumberFilterFragment extends Fragment implements AddReceiverDialog_Fragment.UserNameListener {

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private Realm mRealm = null;
    private NumFilterAdapter mAdapter;
    private List<Number> mDataList = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private RealmResults<Number> dataModelDbs;
    private RealmDataAdapter realmDataAdapter;
    private Unbinder unbinder;

    @BindView(R.id.floatingActionButton)
    FloatingActionButton fab;
    @BindView(R.id.rv_num_filter)
    RecyclerView rvNum;

    public NumberFilterFragment(){

    }

    public static NumberFilterFragment newInstance(String param){
        NumberFilterFragment fragment = new NumberFilterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_num_filter, container, false);
        ButterKnife.bind(this, view);
        unbinder = ButterKnife.bind(this, view);
        setupRecyclerView();
        mRealm = Realm.getDefaultInstance();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dataModelDbs = mRealm.where(Number.class).findAll();
        realmDataAdapter = new RealmDataAdapter(getActivity(), dataModelDbs, true);
        mAdapter.setRealmAdapter(realmDataAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onFinishUserDialog(String filter, int which, int id) {
            switch (which) {
                case 7: //when add number
                    tambahData(filter,id);
                    break;
                case 8: //edit number
                    ubahData(id,filter);
                    break;
                default:
                    break;
            }
    }

    private void setupRecyclerView() {
        rvNum.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());

        rvNum.setLayoutManager(mLayoutManager);
        mAdapter = new NumFilterAdapter(getActivity());
        rvNum.setAdapter(mAdapter);

        mAdapter.ubahData(new NumFilterAdapter.UbahDataInterface() {
            @Override
            public void ubahData(View view, int position) {
                dapatkanData(view, dataModelDbs.get(position));
            }
        });

        mAdapter.hapusData(new NumFilterAdapter.HapusDataInterface() {
            @Override
            public void hapusData(View view, int position) {
                menghapusData(position);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void dapatkanData(View view, Number dataModelDb) {
        int id = dataModelDb.getId();
        showEditDialog(id);
    }

    private void tambahData(String number, int id) {
        mRealm.beginTransaction();
        Number dataModel = mRealm.createObject(Number.class);
        dataModel.setId(id);
        dataModel.setmNumber(number);
        mRealm.commitTransaction();
    }

    private void ubahData(int id, String nomor) {
        mRealm.beginTransaction();
        Number dataModel = mRealm.where(Number.class).equalTo("id", id).findFirst();
        dataModel.setmNumber(nomor);
        mRealm.commitTransaction();
    }

    private void menghapusData(int position) {
        mRealm.beginTransaction();

        dataModelDbs.remove(position);

        mRealm.commitTransaction();
    }

    private int dapatkanId() {
        int ids = (int)(System.currentTimeMillis() / 1000);
        return ids;
    }

    private void showAddDialog() {
        FragmentManager fm = getFragmentManager();
        AddReceiverDialog_Fragment dialog_fragment = AddReceiverDialog_Fragment.newInstance("Add Number",7,dapatkanId());
        dialog_fragment.setTargetFragment(this,1);
        dialog_fragment.show(fm, "fragment_add_filter");
    }

    private void showEditDialog(int id){
        FragmentManager fm = getFragmentManager();
        AddReceiverDialog_Fragment dialog_fragment = AddReceiverDialog_Fragment.newInstance("Edit Number",8,id);
        dialog_fragment.setTargetFragment(this,1);
        dialog_fragment.show(fm, "fragment_edit_filter");
    }

}