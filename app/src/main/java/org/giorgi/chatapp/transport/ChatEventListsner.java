package org.giorgi.chatapp.transport;

import org.giorgi.chatapp.model.Message;

public interface ChatEventListsner {
    public void onIncomingMsg(Message m);

    public void onOutgoingMsg(Message m);

    public void onStatusChanged(String contactId, boolean isOnline);
}
