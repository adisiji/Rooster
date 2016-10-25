package com.blikoon.rooster.fragments;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blikoon.rooster.ChatActivity;
import com.blikoon.rooster.Contact;
import com.blikoon.rooster.adapter.ContactAdapter;
import com.blikoon.rooster.ContactModel;
import com.blikoon.rooster.R;
import com.blikoon.rooster.RoosterConnection;

import java.util.List;

public class ContactListFragment extends Fragment implements
        ContactAdapter.mClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String TAG = "ContactListActivity";
    private FragmentManager fm;
    private RecyclerView contactsRecyclerView;
    private ContactAdapter mAdapter;
    private List<Contact> contacts;
    private String mParam1;

    public ContactListFragment (){

    }

    public static ContactListFragment newInstance(String param) {
        ContactListFragment fragment = new ContactListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void mClick(Contact contact){
        Intent intent = new Intent(getActivity()
                ,ChatActivity.class);
        intent.putExtra("EXTRA_CONTACT_JID",contact.getJid());
        startActivity(intent);
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
        Context context = getActivity().getApplicationContext();
        View view =  inflater.inflate(R.layout.fragment_contact_list, container, false);
        contactsRecyclerView = (RecyclerView)view. findViewById(R.id.contact_list_recycler_view);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        ContactModel model = ContactModel.get(context);
        contacts = model.getContacts();
        return view;
    }

    @Override
    public void onResume() {
        mAdapter = new ContactAdapter(contacts,this);
        contactsRecyclerView.setAdapter(mAdapter);
        super.onResume();
    }
}
