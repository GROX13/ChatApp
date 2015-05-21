package org.giorgi.chatapp.asynchtasks;

import android.os.AsyncTask;

import org.giorgi.chatapp.model.Contact;
import org.giorgi.chatapp.transport.NetworkEventListener;

import java.util.List;

public class ContactImageDownloaderTask extends AsyncTask<List<Contact>, Object, Void> {
    private NetworkEventListener networkEventListener;

    public ContactImageDownloaderTask() {
        // TODO: Auto-generated constructor stub
    }

    @Override
    protected Void doInBackground(List<Contact>... params) {
        // TODO: Auto-generated method stub
        return null;
    }

    public void setNetworkEventListener(NetworkEventListener networkEventListener) {
        this.networkEventListener = networkEventListener;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        if (networkEventListener != null)
            networkEventListener.onAvatarDownloaded((byte[]) values[0], (String) values[1]);
    }

}
