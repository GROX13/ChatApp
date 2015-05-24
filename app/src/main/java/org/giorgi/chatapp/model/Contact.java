package org.giorgi.chatapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.giorgi.chatapp.R;
import org.giorgi.chatapp.app.App;

import java.util.ArrayList;
import java.util.List;

public class Contact {
    // Loading default avatar each time takes too much resources
    private static final Bitmap defaultAvatar
            = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.avatar);
    private long id;
    private String name;
    private String phone;
    private String avatar;
    private byte[] avatarImage;
    private boolean onlineStatus;
    private List<Message> conversation;

    public Contact() {
        onlineStatus = false;
        conversation = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Bitmap getAvatarBitmap() {
        if (avatarImage != null)
            return BitmapFactory.decodeByteArray(avatarImage, 0, avatarImage.length);
        else
            return defaultAvatar;
    }

    public void setAvatarBitmap(byte[] avatarImage) {
        this.avatarImage = avatarImage;
    }

    public boolean getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(boolean status) {
        onlineStatus = status;
    }

    public List<Message> getConversation() {
        return conversation;
    }

    public void setConversation(List<Message> conversation) {
        this.conversation = conversation;
    }
}
