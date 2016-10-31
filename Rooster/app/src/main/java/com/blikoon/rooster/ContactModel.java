package com.blikoon.rooster;

import android.content.Context;

import com.blikoon.rooster.model.Contact;
import com.blikoon.rooster.utils.prefUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gakwaya on 4/16/2016.
 */
public class ContactModel {

    private static ContactModel sContactModel;
    public static List<Contact> mContacts;

    public static ContactModel get(Context context)
    {
        if(sContactModel == null)
        {
            sContactModel = new ContactModel(context);
        }
        return  sContactModel;
    }

    private ContactModel(Context context)
    {
        mContacts = new ArrayList<>();
        populateWithInitialContacts(context);

    }

    private void populateWithInitialContacts(Context context)
    {
        //Create the Contacts and add them to the list;
        String server = prefUtil.getInstance().getString("server_name",null);
        if(server!=null){
            Contact contact = new Contact(server);
            mContacts.add(contact);
        }
    }

    public List<Contact> getContacts()
    {
        return mContacts;
    }

}
