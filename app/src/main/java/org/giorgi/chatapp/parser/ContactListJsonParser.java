package org.giorgi.chatapp.parser;

import android.util.JsonReader;

import org.giorgi.chatapp.model.Contact;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Concrete JSON stream parser.
 */
public class ContactListJsonParser implements Parser {

    public static final String ID = "id";
    public static final String AVATAR_IMAGE = "avatarImg";
    public static final String DISPLAY_NAME = "displayName";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String CONTACTS_LIST = "contactList";

    @Override
    public ArrayList<Contact> parse(InputStream stream) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
        try {
            return readContactsObject(reader);
        } finally {
            reader.close();
        }
    }

    private ArrayList<Contact> readContactsObject(JsonReader reader) throws IOException {
        reader.beginObject();
        if (reader.hasNext()) {
            if (CONTACTS_LIST.equals(reader.nextName())) {
                return readArray(reader);
            }
        }
        reader.endObject();
        return new ArrayList<>();
    }

    private ArrayList<Contact> readArray(JsonReader reader) throws IOException {
        ArrayList<Contact> contactsList = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            contactsList.add(readContact(reader));
        }
        reader.endArray();
        return contactsList;
    }

    private Contact readContact(JsonReader reader) throws IOException {
        Contact contact = new Contact();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (ID.equals(name)) {
                contact.setId(reader.nextInt());
            } else if (DISPLAY_NAME.equals(name)) {
                contact.setName(reader.nextString());
            } else if (PHONE_NUMBER.equals(name)) {
                contact.setPhone(reader.nextString());
            } else if (AVATAR_IMAGE.equals(name)) {
                contact.setAvatar(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return contact;
    }

}
