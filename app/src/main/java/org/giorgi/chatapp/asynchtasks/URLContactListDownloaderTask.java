package org.giorgi.chatapp.asynchtasks;

import android.util.Log;

import org.giorgi.chatapp.model.Contact;
import org.giorgi.chatapp.parser.Parser;
import org.giorgi.chatapp.transport.NetworkEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class URLContactListDownloaderTask extends ContactListDownloaderTask {
    private String url;
    private Parser parser;
    private NetworkEventListener listener;

    public URLContactListDownloaderTask
            (String url, NetworkEventListener listener, Parser parser) {
        this.url = url;
        this.parser = parser;
        this.listener = listener;
    }

    @Override
    protected ArrayList<Contact> doInBackground(Void... params) {
        try {
            return loadJsonFromNetwork();
        } catch (IOException e) {
            Log.e(e.getMessage(), Arrays.toString(e.getStackTrace()));
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Contact> loadJsonFromNetwork() throws IOException {
        InputStream stream = null;
        ArrayList<Contact> contacts = null;

        try {
            stream = downloadUrl(url);
            contacts = (ArrayList<Contact>) parser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return contacts;
    }

    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    @Override
    protected void onPostExecute(ArrayList<Contact> contactList) {
        listener.onContactListDownloaded(contactList);
    }

}
