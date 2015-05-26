package org.giorgi.chatapp.app;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.BaseAdapter;

import org.giorgi.chatapp.R;
import org.giorgi.chatapp.asynchtasks.ContactImageDownloaderTask;
import org.giorgi.chatapp.asynchtasks.DBContactListDownloaderTask;
import org.giorgi.chatapp.asynchtasks.URLContactListDownloaderTask;
import org.giorgi.chatapp.database.MyDBHelper;
import org.giorgi.chatapp.model.Contact;
import org.giorgi.chatapp.model.Message;
import org.giorgi.chatapp.network.NetworkReceiver;
import org.giorgi.chatapp.parser.ContactListJsonParser;
import org.giorgi.chatapp.transport.ChatEventListener;
import org.giorgi.chatapp.transport.ChatTransport;
import org.giorgi.chatapp.transport.NetworkEventListener;
import org.giorgi.chatapp.transport.TestChatTransport;
import org.giorgi.chatapp.userintrface.ChatActivity;

import java.util.ArrayList;
import java.util.List;

public class App extends Application implements NetworkEventListener, ChatEventListener {

    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    public static final String ID_STRING = "ID";
    public static final String INDEX_STRING = "index";
    private static final String URL =
            "https://dl.dropboxusercontent.com/u/28030891/FreeUni/Android/assinments/contacts.json";
    // Whether the display should be refreshed.
    public static boolean refreshDisplay = true;
    // The user's current network preference setting.
    public static String sPref = null;
    /**
     * This variable contains id of selected item
     */
    public static long selectedId = -1;
    public static int selectedIndex = -1;
    private static Context context;
    // Whether there is a Wi-Fi connection.
    private static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    private static boolean mobileConnected = false;
    private static ArrayList<Contact> contacts;
    private static ArrayList<Long> recentContacts;
    // For saving observers
    private static ArrayList<BaseAdapter> observers = new ArrayList<>();
    private static MyDBHelper dbHelper;
    private static TestChatTransport chatTransport;
    private static ArrayList<Long> recentList;
    private Handler handler;
    private Runnable notifierRunnable;

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

    public static ArrayList<Long> getRecentContactList() {
        if (recentContacts == null)
            return new ArrayList<>();
        return recentContacts;
    }

    public static Context getContext() {
        return context;
    }

    public static MyDBHelper getDBHelper() {
        return dbHelper;
    }

    public static int getIndex(long id) {
        for (int i = 0; i < App.contacts.size(); i++) {
            if (App.contacts.get(i).getId() == id) {
                App.contacts.get(i);
                return i;
            }
        }
        return -1;
    }

    public static Contact getContactWithId(long id) {
        return App.contacts.get(getIndex(id));
    }

    public static void setRecentList(ArrayList<Long> recentList) {
        App.recentList = recentList;
    }

    public static void saveDataToDatabase() {
        // TODO: Should save data into database
    }

    @Override
    public void onCreate() {
        super.onCreate();
        App.context = this;
        initApp();
    }

    private void initApp() {
        chatTransport = new TestChatTransport();
        chatTransport.setContacts(App.contacts);
        chatTransport.addChatEventListener(this);
        handler = new Handler();
        notifierRunnable = new Runnable() {
            @Override
            public void run() {
                notifyObservers();
            }
        };
        dbHelper = new MyDBHelper(MyDBHelper.DATABASE_NAME, MyDBHelper.DATABASE_VERSION);
        recentContacts = dbHelper.getRecent();
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
        if (refreshDisplay && !dbHelper.dataBaseExists()) {
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
        Contact c = App.getContactWithId(m.getSourceId());
        c.addMessage(m);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(c.getName())
                        .setContentText(m.getMessage());
        Intent resultIntent = new Intent(this, ChatActivity.class);
        resultIntent.putExtra(ID_STRING, c.getId());
        resultIntent.putExtra(INDEX_STRING, App.getIndex(c.getId()));
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        int mNotificationId = (int) c.getId();
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr;
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        c.addMessage(m);
        c.setUnreadMessage(true);
        if (recentContacts.contains(m.getSourceId())) {
            recentContacts.remove(m.getSourceId());
        }
        recentContacts.add(0, m.getSourceId());
        handler.post(notifierRunnable);
    }

    @Override
    public void onOutgoingMsg(Message m) {
        // TODO Auto-generated method stub
        if (recentContacts.contains(m.getDestinationId())) {
            recentContacts.remove(m.getDestinationId());
        }
        recentContacts.add(0, m.getDestinationId());
        chatTransport.sendMessage(m);
        handler.post(notifierRunnable);
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
        chatTransport.setContacts(App.contacts);
        chatTransport.start();
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
        if (((sPref.equals(ANY)) && (wifiConnected || mobileConnected))
                || ((sPref.equals(WIFI)) && (wifiConnected))) {
            // AsyncTask subclass
            ContactImageDownloaderTask task = new ContactImageDownloaderTask();
            task.setNetworkEventListener(this);
            task.execute(App.contacts);
        } else {
            showError();
        }
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
        // Toast.makeText(getApplicationContext(), errorMsg,
        // Toast.LENGTH_LONG).show();
    }
}
