package org.giorgi.chatapp.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.giorgi.chatapp.app.App;
import org.giorgi.chatapp.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class MyDBHelper extends SQLiteOpenHelper {
    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "test_database_02";

    // Contacts table name
    public static final String TABLE_CONTACTS = "contacts";
    // Contacts table column names
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PH_NO = "phone_number";
    public static final String KEY_AVATAR_IMAGE = "avatar_url";
    public static final String KEY_HAVE_MESSAGE = "have_message";

    // Message table name
    public static final String TABLE_MESSAGES = "messages";
    // Message table column names
    public static final String KEY_MESSAGE_ID = "message_id";
    public static final String KEY_MESSAGE_SRC = "source";
    public static final String KEY_MESSAGE_DST = "destination";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_MESSAGE_TIME = "time";

    public MyDBHelper(String dbName, int version) {
        super(App.getContext(), dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createContactsTable = "CREATE TABLE " + TABLE_CONTACTS
                + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_PH_NO
                + " TEXT," + KEY_AVATAR_IMAGE + " TEXT," + KEY_HAVE_MESSAGE + " INTEGER" + ")";
        String createMessageTable = "CREATE TABLE " + TABLE_MESSAGES
                + "(" + KEY_MESSAGE_ID + " INTEGER PRIMARY KEY,"
                + KEY_MESSAGE_SRC + " INTEGER," + KEY_MESSAGE_DST
                + " INTEGER," + KEY_MESSAGE + " TEXT," + KEY_MESSAGE_TIME + " TEXT" + ")";

        Log.d("contacts table", createContactsTable);
        Log.d("messages table", createMessageTable);

        db.execSQL(createContactsTable);
        db.execSQL(createMessageTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Soon will be commented
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        onCreate(db);
    }

    /**
     * Adding new contact
     *
     * @param contact contact object
     */
    public void addContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, contact.getId()); // Contact ID
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getPhone()); // Contact Phone Number
        values.put(KEY_AVATAR_IMAGE, contact.getAvatar()); // Contact Phone Number
        values.put(KEY_HAVE_MESSAGE, contact.isUnreadMessage()); // Contact Has Unread Message

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
        contact.setId(cursor.getLong(0));
        contact.setName(cursor.getString(1));
        contact.setPhone(cursor.getString(2));
        contact.setAvatar(cursor.getString(3));
        contact.setUnreadMessage((cursor.getInt(4) == 0));
        return contact;
    }

    /**
     * Getting All Contacts
     *
     * @return List of contacts containing database
     */
    public List<Contact> getAllContacts() {
        List<Contact> contactList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                contactList.add(setUpContact(cursor));
            } while (cursor.moveToNext());
        }
        db.close();
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
        boolean exists = true;
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase
                    (App.getContext().getDatabasePath(DATABASE_NAME).toString(),
                            null, SQLiteDatabase.OPEN_READONLY);
            String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
            Cursor cursor = checkDB.rawQuery(countQuery, null);
            if (cursor.getCount() <= 0)
                exists = false;
            cursor.close();
            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
            exists = false;
        }
        return exists;
    }

}
