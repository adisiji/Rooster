package com.blikoon.rooster.adapter;

/**
 * Created by neobyte on 10/24/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.blikoon.rooster.Contact;
import com.blikoon.rooster.R;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by neobyte on 10/15/2016.
 */

public class ServerAdapter extends RecyclerView.Adapter<ServerAdapter.ContactHolder>
{
    private List<Contact> mContacts;


    public ServerAdapter(List<Contact> contactList)
    {
        mContacts = contactList;
    }

    @Override
    public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater
                .inflate(R.layout.list_item_contact, parent,
                        false);
        return new ContactHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactHolder holder, int position) {
        Contact contact = mContacts.get(position);
        holder.bindContact(contact);
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    public class ContactHolder extends RecyclerView.ViewHolder
    {
        private TextView contactTextView;
        private Contact mContact;
        public ContactHolder (final View itemView)
        {
            super(itemView);
            contactTextView = (TextView) itemView.findViewById(R.id.contact_jid);
        }


        public void bindContact(final Contact contact)
        {
            mContact = contact;
            if (mContact == null)
            {
                Log.d(TAG,"Trying to work on a null Contact object ,returning.");
                return;
            }
            contactTextView.setText(mContact.getJid());
        }
    }
}
