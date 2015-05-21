package org.giorgi.chatapp.parser;

import org.giorgi.chatapp.model.Contact;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Concrete JSON stream parser.
 */
public class ContactListJsonParser implements Parser {

    @Override
    public ArrayList<Contact> parse(InputStream stream) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        contacts.add(null);
        return contacts;
    }
}
