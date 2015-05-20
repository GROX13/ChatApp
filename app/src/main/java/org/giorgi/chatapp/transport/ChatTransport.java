package org.giorgi.chatapp.transport;

import java.util.ArrayList;
import java.util.List;

import org.giorgi.chatapp.model.Message;


public abstract class ChatTransport {
    private List<ChatEventListsner> listeners = new ArrayList<ChatEventListsner>();

    public abstract void start();

    public abstract void sendMessage(Message m);

    public void addChatEventListsner(ChatEventListsner listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeChatEventListsner(ChatEventListsner listener) {
        if (listeners.contains(listener))
            listeners.remove(listener);
    }
}
