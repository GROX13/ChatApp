package org.giorgi.chatapp.asynchtasks;

import org.giorgi.chatapp.database.MyDBHelper;
import org.giorgi.chatapp.model.Contact;
import org.giorgi.chatapp.transport.NetworkEventListener;

import java.util.ArrayList;

public class DBContactListDownloaderTask extends ContactListDownloaderTask {

    private MyDBHelper dbHelper;
    private NetworkEventListener eventListener;

    public DBContactListDownloaderTask(MyDBHelper dbHelper, NetworkEventListener eventListener) {
        this.dbHelper = dbHelper;
        this.eventListener = eventListener;
    }

    @Override
    protected ArrayList<Contact> doInBackground(Void... params) {
        return (ArrayList<Contact>) dbHelper.getAllContacts();
    }

    @Override
    protected void onPostExecute(ArrayList<Contact> contacts) {
        eventListener.onContactListDownloaded(contacts);
    }

}
