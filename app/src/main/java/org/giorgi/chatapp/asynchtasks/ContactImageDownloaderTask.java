package org.giorgi.chatapp.asynchtasks;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.giorgi.chatapp.model.Contact;
import org.giorgi.chatapp.transport.NetworkEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ContactImageDownloaderTask extends AsyncTask<List<Contact>, Object, Void> {
    private NetworkEventListener networkEventListener;

    @Override
    @SafeVarargs
    protected final Void doInBackground(List<Contact>... params) {
        List<Contact> contactsList = params[0];
        for (int i = 0; i < contactsList.size(); i++) {
            Contact contact = contactsList.get(i);
            try {
                publishProgress(downloadImage(contact.getAvatar()), String.valueOf(contact.getId()));
            } catch (Exception e) {
                networkEventListener.onError(0, e.getMessage());
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    private byte[] downloadImage(String imageUrl) throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(imageUrl);
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        int imageLength = (int) (entity.getContentLength());
        InputStream is = entity.getContent();

        byte[] imageBlob = new byte[imageLength];
        int bytesRead = 0;
        while (bytesRead < imageLength) {
            int n = is.read(imageBlob, bytesRead, imageLength - bytesRead);
            if (n <= 0)
                n = 0; // do some error handling
            bytesRead += n;
        }
        return imageBlob;
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
