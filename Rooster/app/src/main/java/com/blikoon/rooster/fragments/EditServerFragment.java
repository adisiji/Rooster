package com.blikoon.rooster.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.rooster.AddReceiverDialog_Fragment;
import com.blikoon.rooster.Contact;
import com.blikoon.rooster.ContactModel;
import com.blikoon.rooster.R;
import com.blikoon.rooster.adapter.ServerAdapter;
import com.blikoon.rooster.utils.AllUtil;
import com.blikoon.rooster.utils.prefUtil;

import java.util.List;

/**
 * Created by neobyte on 10/24/2016.
 */

public class EditServerFragment extends Fragment implements AddReceiverDialog_Fragment.UserNameListener{

    private FloatingActionButton fab;
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private RecyclerView contactsRecyclerView;
    private ServerAdapter mAdapter;
    private List<Contact> contacts;
    private TextView servername;
    private ImageView btnEdit,btnDel;
    public EditServerFragment(){

    }

    public static EditServerFragment newInstance(String param) {
        EditServerFragment fragment = new EditServerFragment();
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
        View view =  inflater.inflate(R.layout.fragment_edit_server, container, false);
        servername = (TextView)view.findViewById(R.id.edit_server_txt);
        String gg = prefUtil.getInstance(getActivity().getApplicationContext()).getString("server_name");
        servername.setText(gg);
        btnDel = (ImageView)view.findViewById(R.id.btn_del_server) ;
        btnEdit = (ImageView)view.findViewById(R.id.btn_edit_server);
        fab = (FloatingActionButton)view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "U click delete?", Toast.LENGTH_SHORT).show();
            }
        });
        /*contactsRecyclerView = (RecyclerView)view. findViewById(R.id.contact_list_recycler_view);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        fm = getActivity().getSupportFragmentManager();
        ContactModel model = ContactModel.get(context);
        contacts = model.getContacts();*/
        return view;
    }

    @Override
    public void onResume() {
        /*mAdapter = new ServerAdapter(contacts);
        contactsRecyclerView.setAdapter(mAdapter);*/
        super.onResume();
    }

    @Override
    public void onFinishUserDialog(String user, int which) {
        if(AllUtil.isValidId(user)){
            /*Contact contact = new Contact(user);
            contacts.add(contact);
            mAdapter.notifyItemInserted(contacts.size()-1);*/
        switch (which) {
            case 1:
                //when add server
                break;
            case 2:
                prefUtil.getInstance().set("server_name",user);
                servername.setText(user);
                Contact contact = new Contact(user);
                ContactModel.mContacts.clear();
                ContactModel.mContacts.add(contact);
                break;
            default:
                break;
        }
        }
        else {
            Toast.makeText(getActivity(), "Wrong ID Xmpp", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEditDialog(){
        FragmentManager fm = getFragmentManager();
        AddReceiverDialog_Fragment dialog_fragment = AddReceiverDialog_Fragment.newInstance("Edit Server",2);
        dialog_fragment.setTargetFragment(this,1);
        dialog_fragment.show(fm, "fragment_edit_name");
    }

    private void showAddDialog() {
        FragmentManager fm = getFragmentManager();
        AddReceiverDialog_Fragment dialog_fragment = AddReceiverDialog_Fragment.newInstance("Add Server",1);
        dialog_fragment.setTargetFragment(this,1);
        dialog_fragment.show(fm, "fragment_add_server");
    }

}
