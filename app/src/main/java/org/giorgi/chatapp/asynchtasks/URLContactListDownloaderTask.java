package org.giorgi.chatapp.asynchtasks;

import org.giorgi.chatapp.model.Contact;

import java.util.ArrayList;

public class URLContactListDownloaderTask extends ContactListDownloaderTask {
    private String url;

    public URLContactListDownloaderTask(String url) {
        this.url = url;
    }

    @Override
    protected ArrayList<Contact> doInBackground(Void... params) {
        // TODO Auto-generated method stub

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Contact> contactList) {
        // TODO Auto-generated method stub

    }

}
