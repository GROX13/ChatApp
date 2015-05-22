package org.giorgi.chatapp.app;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import org.giorgi.chatapp.asynchtasks.ContactImageDownloaderTask;
import org.giorgi.chatapp.asynchtasks.DBContactListDownloaderTask;
import org.giorgi.chatapp.asynchtasks.URLContactListDownloaderTask;
import org.giorgi.chatapp.database.MyDBHelper;
import org.giorgi.chatapp.model.Contact;
import org.giorgi.chatapp.model.Message;
import org.giorgi.chatapp.network.NetworkReceiver;
import org.giorgi.chatapp.parser.ContactListJsonParser;
import org.giorgi.chatapp.transport.ChatEventListsner;
import org.giorgi.chatapp.transport.ChatTransport;
import org.giorgi.chatapp.transport.NetworkEventListener;
import org.giorgi.chatapp.transport.TestChatTransport;

import java.util.ArrayList;
import java.util.List;

public class App extends Application implements NetworkEventListener, ChatEventListsner {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    private static final String URL =
            "https://dl.dropboxusercontent.com/u/28030891/FreeUni/Android/assinments/contacts.json";
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    // The user's current network preference setting.
    public static String sPref = null;
    private static Context context;
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    private static ChatTransport chatTransport;
    private static ArrayList<Contact> contacts;
    // For saving observers
    private static ArrayList<BaseAdapter> observers = new ArrayList<>();
    private static MyDBHelper dbHelper;

    public static void registerObserver(BaseAdapter observer) {
        App.observers.add(observer);
    }

    public static ChatTransport getChatTransport() {
        return chatTransport;
    }

    public static ArrayList<Contact> getContactList() {
        if (contacts == null)
            return new ArrayList<>();
        return contacts;
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.context = this;
        initApp();
    }

    private void initApp() {
        chatTransport = new TestChatTransport();
        chatTransport.addChatEventListsner(this);
        dbHelper = new MyDBHelper(App.getContext(),
                MyDBHelper.DATABASE_NAME, MyDBHelper.DATABASE_VERSION);
        // Set up contact list download for my application
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        NetworkReceiver receiver = new NetworkReceiver();
        this.registerReceiver(receiver, filter);

        // Gets the user's network preference settings
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieves a string value for the preferences. The second parameter
        // is the default value to use if a preference value is not found.
        sPref = sharedPrefs.getString("listPref", "Wi-Fi");

        updateConnectedFlags();

        // Only loads the page if refreshDisplay is true. Otherwise, keeps previous
        // display. For example, if the user has set "Wi-Fi only" in prefs and the
        // device loses its Wi-Fi connection midway through the user using the app,
        // you don't want to refresh the display--this would force the display of
        // an error page instead of content.
        if (refreshDisplay) {
            // TODO: Check if database exists
            loadFromNet();
        } else {
            loadFromDataBase();
        }
    }

    private void loadFromDataBase() {
        new DBContactListDownloaderTask(dbHelper, this).execute();
    }

    // Checks the network connection and sets the wifiConnected and mobileConnected
    // variables accordingly.
    private void updateConnectedFlags() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    // Uses AsyncTask subclass to download the JSON feed from cloud.
    // This avoids UI lock up. To prevent network operations from
    // causing a delay that results in a poor user experience, always perform
    // network operations on a separate thread from the UI.
    private void loadFromNet() {
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass
            new URLContactListDownloaderTask
                    (URL, this, new ContactListJsonParser()).execute();
        } else {
            showError();
        }
    }

    // Displays an error if the app is unable to load content.
    private void showError() {
        Log.e("App", "Error occurred while downloading contact list!");
    }

    @Override
    public void onIncomingMsg(Message m) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onOutgoingMsg(Message m) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String contactId, boolean isOnline) {
        // TODO: Auto-generated method stub

    }

    private void notifyObservers() {
        for (int i = 0; i < observers.size(); i++) {
            observers.get(i).notifyDataSetChanged();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onContactListDownloaded(List<Contact> contacts) {
        App.contacts = (ArrayList<Contact>) contacts;
        if (!dbHelper.dataBaseExists())
            notifyDataBaseSaver();
        notifyAvatarDownloader();
        notifyObservers();
    }

    private void notifyDataBaseSaver() {
        for (int i = 0; i < App.contacts.size(); i++) {
            dbHelper.addContact(App.contacts.get(i));
        }
    }

    @SuppressWarnings("unchecked")
    private void notifyAvatarDownloader() {
        ContactImageDownloaderTask task = new ContactImageDownloaderTask();
        task.setNetworkEventListener(this);
        task.execute(App.contacts);
    }


    @Override
    public void onAvatarDownloaded(byte[] imgData, String contId) {
        long contactId = Long.valueOf(contId);
        for (int i = 0; i < App.contacts.size(); i++) {
            Contact contact = App.contacts.get(i);
            if (contact.getId() == contactId) {
                contact.setAvatarBitmap(imgData);
                break;
            }
        }
        notifyObservers();
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        Toast.makeText(getApplicationContext(), errorMsg,
                Toast.LENGTH_LONG).show();
    }

}
