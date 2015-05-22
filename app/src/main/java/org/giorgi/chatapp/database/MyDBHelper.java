package org.giorgi.chatapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.giorgi.chatapp.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class MyDBHelper extends SQLiteOpenHelper {
    // Database Version
    public static final int DATABASE_VERSION = 1;
    // Database Name
    public static final String DATABASE_NAME = "chat_manager";
    // Contacts table name
    public static final String TABLE_CONTACTS = "contacts";
    // Contacts Table Columns names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PH_NO = "phone_number";
    public static final String KEY_AVATAR_IMAGE = "avatar_image";

    private SQLiteDatabase db;
    private boolean exists;

    public MyDBHelper(Context context, String dbName, int version) {
        super(context, dbName, null, version);
        db = getWritableDatabase();
        exists = true;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createContactsTable = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT" + KEY_AVATAR_IMAGE + " TEXT" + ")";
        db.execSQL(createContactsTable);
        exists = false;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Soon will be commented
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
//        onCreate(db);
    }

    /**
     * Adding new contact
     *
     * @param contact
     */
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, contact.getId()); // Contact ID
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhone()); // Contact Phone Number
        values.put(KEY_AVATAR_IMAGE, contact.getAvatar()); // Contact Phone Number

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Getting single contact
     *
     * @param id id of account to be selected
     * @return new contact object
     */
    public Contact getContact(long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID,
                        KEY_NAME, KEY_PH_NO, KEY_AVATAR_IMAGE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        Contact contact = null;
        if (cursor != null) {
            cursor.moveToFirst();
            contact = setUpContact(cursor);
            cursor.close();
        }
        return contact;
    }

    private Contact setUpContact(Cursor cursor) {
        Contact contact = new Contact();
        contact.setId(Long.valueOf(cursor.getString(0)));
        contact.setName(cursor.getString(1));
        contact.setPhone(cursor.getString(2));
        contact.setAvatar(cursor.getString(3));
        return contact;
    }

    /**
     * Getting All Contacts
     *
     * @return List of contacts containing database
     */
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<Contact>();

        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                contactList.add(setUpContact(cursor));
            } while (cursor.moveToNext());
        }

        return contactList;
    }

    /**
     * Getting contacts Count
     *
     * @return number of contacts
     */
    public int getContactsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    /**
     * Updating single contact
     *
     * @param contact contact object
     * @return 0 every time
     */
    public int updateContact(Contact contact) {
        // TODO: May be done in the future
        return 0;
    }

    /**
     * Deleting single contact
     *
     * @param contact contact object
     */
    public void deleteContact(Contact contact) {
        // TODO: May be done in the future
    }

    /**
     * @return true if database exists false otherwise
     */
    public boolean dataBaseExists() {
        // TODO: Should be done ASAP
        return exists;
    }

}
