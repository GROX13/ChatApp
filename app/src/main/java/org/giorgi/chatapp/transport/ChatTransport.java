package org.giorgi.chatapp.transport;

import org.giorgi.chatapp.model.Message;

import java.util.ArrayList;
import java.util.List;


public abstract class ChatTransport {
    private List<ChatEventListener> listeners = new ArrayList<>();

    public abstract void start();

    public abstract void sendMessage(Message m);

    public void addChatEventListener(ChatEventListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeChatEventListsner(ChatEventListener listener) {
        if (listeners.contains(listener))
            listeners.remove(listener);
    }
}
