package org.giorgi.chatapp.asynchtasks;

import java.util.ArrayList;

import org.giorgi.chatapp.model.Contact;
import org.giorgi.chatapp.transport.NetworkEventListener;

import android.os.AsyncTask;

public abstract class ContactListDownloaderTask extends AsyncTask<Void, Void, ArrayList<Contact>> {
    private NetworkEventListener networkEventListener;

    public void setNetworkEventListener(NetworkEventListener networkEventListener) {
        this.networkEventListener = networkEventListener;
    }

    @Override
    protected void onPostExecute(ArrayList<Contact> contacts) {
        super.onPostExecute(contacts);
        networkEventListener.onContactListDownloaded(contacts);
    }
}
