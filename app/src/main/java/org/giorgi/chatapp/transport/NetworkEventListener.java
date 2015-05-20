package org.giorgi.chatapp.transport;

import java.util.List;

import org.giorgi.chatapp.model.Contact;

public interface NetworkEventListener {
    public void onContactListDownloaded(List<Contact> contacts);

    public void onAvatarDownloaded(byte[] imgData, String contactId);

    public void onError(int errorCode, String errorMsg);
}
