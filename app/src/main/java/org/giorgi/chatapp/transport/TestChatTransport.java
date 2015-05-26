package org.giorgi.chatapp.transport;

import org.giorgi.chatapp.model.Contact;
import org.giorgi.chatapp.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TestChatTransport extends ChatTransport {

    // Current time interval is (30 * 1000) 30 seconds
    private static final int timeInterval = 30000;
    // String for saving time format
    private static final String timeFormat = "yyyy-MM-dd HH:mm:ss";
    // This array list will then be used for generating random message
    private static final List<String> messageTexts = new ArrayList<>(
            Arrays.asList("Hello!", "How are you?", "Hello world!", "I'll meet you tomorrow."));

    // Will be used for getting random contact id for incoming message
    private ArrayList<Contact> contacts;


    // Returns random integer in [min, max] interval
    private static int randomInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    // Returns timestamp formatted time
    private static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate =
                new SimpleDateFormat(timeFormat, Locale.getDefault());
        Date now = new Date();
        return sdfDate.format(now);
    }

    @Override
    public void start() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if (contacts != null) {
                    long destination = Message.MY_ID;
                    long source = contacts.get(randomInt(0, contacts.size())).getId();
                    String text = messageTexts.get(randomInt(0, messageTexts.size() - 1));
                    String time = getCurrentTimeStamp();
                    notifyChatEventListener(new Message(source, destination, text, time));
                }
            }
        }, 0, timeInterval);
    }


    @Override
    public void sendMessage(Message m) {
        // Here should go implementation of handling
        // incoming message, yet not used in anything
    }

    @Override
    public void notifyChatEventListener(Message message) {
        for (int i = 0; i < listeners.size(); i++)
            listeners.get(i).onIncomingMsg(message);
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }
}
