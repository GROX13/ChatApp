package org.giorgi.chatapp.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.giorgi.chatapp.R;
import org.giorgi.chatapp.app.App;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class Contact {

    // Loading default avatar each time takes too much resources
    private static final byte[] defaultAvatar;

    static {
        Bitmap bitmap = BitmapFactory.decodeResource(App.getContext().getResources(), R.drawable.avatar);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        defaultAvatar = stream.toByteArray();
    }

    private long id;
    private String name;
    private String phone;
    private String avatar;
    private byte[] avatarImage;

    public Contact() {
        avatarImage = defaultAvatar;
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
        return BitmapFactory.decodeByteArray(avatarImage, 0, avatarImage.length);
    }

    public void setAvatarBitmap(byte[] avatarImage) {
        this.avatarImage = avatarImage;
    }

    public boolean getOnlineStatus() {
        Random random = new Random();
        return random.nextBoolean();
    }

    public void setOnlineStatus() {

    }
}
