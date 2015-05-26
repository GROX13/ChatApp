package org.giorgi.chatapp.transport;

import org.giorgi.chatapp.model.Message;

public interface ChatEventListener {

    void onIncomingMsg(Message m);

    void onOutgoingMsg(Message m);

    void onStatusChanged(String contactId, boolean isOnline);

}
