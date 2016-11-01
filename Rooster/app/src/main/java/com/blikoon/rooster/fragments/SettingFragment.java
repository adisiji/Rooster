package com.blikoon.rooster.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.rooster.AddReceiverDialog_Fragment;
import com.blikoon.rooster.ContactModel;
import com.blikoon.rooster.R;
import com.blikoon.rooster.model.Contact;
import com.blikoon.rooster.utils.AllUtil;
import com.blikoon.rooster.utils.prefUtil;

/**
 * Created by neobyte on 10/24/2016.
 */

public class SettingFragment extends Fragment implements AddReceiverDialog_Fragment.UserNameListener{

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private TextView servername, kodeTV, numberTV;
    private ImageView btnServer, btnKode, btnNumber;

    public SettingFragment(){

    }

    public static SettingFragment newInstance(String param) {
        SettingFragment fragment = new SettingFragment();
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
        View view =  inflater.inflate(R.layout.fragment_setting, container, false);
        servername = (TextView)view.findViewById(R.id.edit_server_txt);
        kodeTV = (TextView)view.findViewById(R.id.edit_kode_txt) ;
        numberTV = (TextView)view.findViewById(R.id.edit_number_txt);
        String server_name = prefUtil.getInstance(getActivity().getApplicationContext()).getString("server_name");
        servername.setText(server_name);
        String kode = prefUtil.getInstance(getActivity().getApplicationContext()).getString("filter_code");
        kodeTV.setText(kode);
        String number = prefUtil.getInstance(getActivity().getApplicationContext()).getString("filter_number");
        numberTV.setText(number);
        btnServer = (ImageView)view.findViewById(R.id.btn_edit_server);
        btnNumber = (ImageView)view.findViewById(R.id.btn_edit_number);
        btnKode = (ImageView)view.findViewById(R.id.btn_edit_kode);

        btnServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showServerDialog();
            }
        });

        btnKode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showKodeDialog();
            }
        });

        btnNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNumberDialog();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onFinishUserDialog(String inputUser, int which) {
        if(!inputUser.isEmpty()){
            switch (which) {
                case 1:
                    if(AllUtil.isValidId(inputUser)) {
                        prefUtil.getInstance().set("server_name",inputUser);
                        servername.setText(inputUser);
                        Contact contact = new Contact(inputUser);
                        ContactModel.mContacts.clear();
                        ContactModel.mContacts.add(contact);
                    }
                    else {
                        Toast.makeText(getActivity(), "Wrong ID Xmpp", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    prefUtil.getInstance().set("filter_code",inputUser);
                    kodeTV.setText(inputUser);
                    break;
                case 3:
                    prefUtil.getInstance().set("filter_number",inputUser);
                    numberTV.setText(inputUser);
                    break;
                default:
                    break;
            }
        }
        else {
            Toast.makeText(getActivity(), "ERROR !! Text is Empty", Toast.LENGTH_SHORT).show();
        }

    }

    private void showServerDialog(){
        FragmentManager fm = getFragmentManager();
        AddReceiverDialog_Fragment dialog_fragment = AddReceiverDialog_Fragment.newInstance("Edit Server",1);
        dialog_fragment.setTargetFragment(this,1);
        dialog_fragment.show(fm, "fragment_edit_name");
    }

    private void showKodeDialog(){
        FragmentManager fm = getFragmentManager();
        AddReceiverDialog_Fragment dialog_fragment = AddReceiverDialog_Fragment.newInstance("Edit Code",2);
        dialog_fragment.setTargetFragment(this,1);
        dialog_fragment.show(fm, "fragment_edit_name");
    }

    private void showNumberDialog(){
        FragmentManager fm = getFragmentManager();
        AddReceiverDialog_Fragment dialog_fragment = AddReceiverDialog_Fragment.newInstance("Edit Number",3);
        dialog_fragment.setTargetFragment(this,1);
        dialog_fragment.show(fm, "fragment_edit_name");
    }

}
