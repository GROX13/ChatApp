package org.giorgi.chatapp.transport;

import org.giorgi.chatapp.model.Message;

import java.util.ArrayList;
import java.util.List;


public abstract class ChatTransport {
    protected List<ChatEventListener> listeners = new ArrayList<>();

    public abstract void start();

    public abstract void sendMessage(Message message);

    public abstract void notifyChatEventListener(Message message);

    public void addChatEventListener(ChatEventListener listener) {
        if (!listeners.contains(listener))
            listeners.add(listener);
    }

    public void removeChatEventListener(ChatEventListener listener) {
        if (listeners.contains(listener))
            listeners.remove(listener);
    }
}
