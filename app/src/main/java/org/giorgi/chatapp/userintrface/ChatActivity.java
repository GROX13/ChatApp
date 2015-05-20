package org.giorgi.chatapp.userintrface;

import android.os.Bundle;

import org.giorgi.chatapp.app.App;
import org.giorgi.chatapp.model.Message;
import org.giorgi.chatapp.transport.ChatEventListsner;

public class ChatActivity extends CustomActivity implements ChatEventListsner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getChatTransport().addChatEventListsner(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getChatTransport().removeChatEventListsner(this);
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

}
