package com.blikoon.rooster;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by neobyte on 10/15/2016.
 */

public class AddReceiverDialog_Fragment extends DialogFragment implements TextView.OnEditorActionListener{

    private UserNameListener listener;
    private EditText mEditText;

    public interface UserNameListener {
        void onFinishUserDialog(String content, int sikon);
    }

    public AddReceiverDialog_Fragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddReceiverDialog_Fragment newInstance(String title, int which) {
        AddReceiverDialog_Fragment dialog_fragment = new AddReceiverDialog_Fragment();
        Bundle args = new Bundle();
        args.putString("title",title);
        args.putInt("sikon",which);
        dialog_fragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        dialog_fragment.setArguments(args);
        return dialog_fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (UserNameListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling Fragment must implement OnAddFriendListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_receiver, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        final int sikon = getArguments().getInt("sikon",0);
        mEditText = (EditText) view.findViewById(R.id.txt_your_name);
        Button btnOk = (Button)view.findViewById(R.id.button) ;
        Button btnCancel = (Button)view.findViewById(R.id.button2);
        if(sikon==1){
            mEditText.setHint("Server Name");
        }
        else if(sikon==2){
            mEditText.setHint("Kode");
        }
        else if(sikon==3){
            mEditText.setHint("Number");
            mEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFinishUserDialog(mEditText.getText().toString(),sikon);
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Cancel Fragment","GOOD !");
                dismiss();
            }
        });

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // 2. Setup a callback when the "Done" button is pressed on keyboard
        mEditText.setOnEditorActionListener(this);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {
            // Return input text back to activity through the implemented listener
            final int sikon = getArguments().getInt("sikon",0);
            listener.onFinishUserDialog(mEditText.getText().toString(), sikon);
            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }

}