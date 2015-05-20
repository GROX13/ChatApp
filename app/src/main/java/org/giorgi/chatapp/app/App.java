package org.giorgi.chatapp.app;

import java.util.ArrayList;
import java.util.List;

import org.giorgi.chatapp.database.MyDBHelper;
import org.giorgi.chatapp.model.Contact;
import org.giorgi.chatapp.model.Message;
import org.giorgi.chatapp.transport.ChatEventListsner;
import org.giorgi.chatapp.transport.ChatTransport;
import org.giorgi.chatapp.transport.NetworkEventListener;
import org.giorgi.chatapp.transport.TestChatTransport;

import android.app.Application;

public class App extends Application implements NetworkEventListener, ChatEventListsner {
    private static ChatTransport chatTransport;
    private static ArrayList<Contact> contacts;
    private static MyDBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        initApp();
    }

    private void initApp() {
        chatTransport = new TestChatTransport();
        chatTransport.addChatEventListsner(this);


    }

    public static ChatTransport getChatTransport() {
        return chatTransport;
    }

    public static ArrayList<Contact> getContactList() {
        return contacts;
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
        // TODO Auto-generated method stub

    }

    @Override
    public void onContactListDownloaded(List<Contact> contacts) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAvatarDownloaded(byte[] imgData, String contactId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        // TODO Auto-generated method stub

    }

}
