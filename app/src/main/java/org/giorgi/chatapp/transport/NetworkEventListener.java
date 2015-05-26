package org.giorgi.chatapp.transport;

import java.util.List;

import org.giorgi.chatapp.model.Contact;

public interface NetworkEventListener {

    void onContactListDownloaded(List<Contact> contacts);

    void onAvatarDownloaded(byte[] imgData, String contactId);

    void onError(int errorCode, String errorMsg);

}
