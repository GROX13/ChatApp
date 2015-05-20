package org.giorgi.chatapp.userintrface;

import com.example.freeunichat.model.Message;
import com.example.freeunichat.transport.ChatEventListsner;

public class ContactListActivity extends CustomActivity implements ChatEventListsner {

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
