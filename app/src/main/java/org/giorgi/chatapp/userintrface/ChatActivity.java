package org.giorgi.chatapp.userintrface;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.giorgi.chatapp.R;
import org.giorgi.chatapp.app.App;
import org.giorgi.chatapp.app.MainActivity;
import org.giorgi.chatapp.model.Contact;
import org.giorgi.chatapp.model.Message;
import org.giorgi.chatapp.transport.ChatEventListener;
import org.giorgi.chatapp.transport.TestChatTransport;

public class ChatActivity extends ActionBarActivity implements ChatEventListener {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments representing
     * each object in a collection. We use a {@link android.support.v4.app.FragmentStatePagerAdapter}
     * derivative, which will destroy and re-create fragments as needed, saving and restoring their
     * state in the process. This is important to conserve memory and is a best practice when
     * allowing navigation between objects in a potentially large collection.
     */
    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will display the object collection.
     */
    ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getChatTransport().addChatEventListener(this);
        setContentView(R.layout.activity_collection_demo);

        // Create an adapter that when requested, will return a fragment representing an object in
        // the collection.
        //
        // ViewPager and its adapters use support library fragments, so we must use
        // getSupportFragmentManager.
        mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());

        // Set up action bar.
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        // Specify that the Home button should show an "Up" caret, indicating that touching the
        // button will take the user one step up in the application's hierarchy.
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        App.getChatTransport().addChatEventListener(this);

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
        mViewPager.setCurrentItem(App.selectedIndex);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This is called when the Home (Up) button is pressed in the action bar.
                // Create a simple intent that starts the hierarchical parent activity and
                // use NavUtils in the Support Package to ensure proper handling of Up.
                Intent upIntent = new Intent(this, MainActivity.class);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is not part of the application's task, so create a new task
                    // with a synthesized back stack.
                    TaskStackBuilder.from(this)
                            // If there are ancestor activities, they should be added here.
                            .addNextIntent(upIntent)
                            .startActivities();
                    finish();
                } else {
                    // This activity is part of the application's task, so simply
                    // navigate up to the hierarchical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getChatTransport().removeChatEventListener(this);
    }

    @Override
    public void onIncomingMsg(Message m) {
        // TODO: Auto-generated method stub
        Contact c = App.getContactWithId(m.getSourceId());
        c.addMessage(m);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(c.getName())
                        .setContentText(m.getMessage());
        Intent resultIntent = new Intent(this, ChatActivity.class);
        // Because clicking the notification opens a new ("special") activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        int mNotificationId = (int) c.getId();
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr;
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    @Override
    public void onOutgoingMsg(Message m) {
        App.getContactWithId(App.selectedId).addMessage(m);
        App app = (App) getApplication();
        app.onOutgoingMsg(m);
    }

    @Override
    public void onStatusChanged(String contactId, boolean isOnline) {
        // TODO Auto-generated method stub

    }

    /**
     * A fragment representing a section of the app.
     */
    public static class DemoObjectFragment extends Fragment {

        public static final String ARG_ID = "id";
        public static final String ARG_INDEX = "index";
        private ChatEventListener chatEventListener;
        private int index;
        private long id;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
            Bundle args = getArguments();
            id = args.getLong(ARG_ID);
            index = args.getInt(ARG_INDEX);
            Button button = (Button) rootView.findViewById(R.id.send_button_id);

            final MessageListAdapter messageListAdapter = new MessageListAdapter();
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText editText = (EditText)
                            ((RelativeLayout) v.getParent()).findViewById(R.id.message_body_field);
                    String message = editText.getText().toString();
                    editText.setText("");
                    chatEventListener.onOutgoingMsg(
                            new Message(Message.MY_ID,
                                    App.selectedId, message,
                                    TestChatTransport.getCurrentTimeStamp()));
                    messageListAdapter.notifyDataSetChanged();
                }
            });
            ((ListView) rootView.findViewById(R.id.list_messages))
                    .setAdapter(messageListAdapter);
            return rootView;
        }

        public void setChatEventListener(ChatEventListener chatEventListener) {
            this.chatEventListener = chatEventListener;
        }


        private class MessageListAdapter extends BaseAdapter {
            @Override
            public int getCount() {
                return App.getContactList().get(index).getConversation().size();
            }

            @Override
            public Object getItem(int position) {
                return App.getContactList().get(index).getConversation().get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View row;
                Message m = App.getContactList().get(index).getMessage(position);
                if (m.isIncoming())
                    row = inflater.inflate(R.layout.incoming_message, parent, false);
                else
                    row = inflater.inflate(R.layout.outgouing_message, parent, false);
                TextView view = (TextView) row.findViewById(R.id.message_text);
                view.setText(m.getMessage());
                return row;
            }
        }
    }

    /**
     * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a fragment
     * representing an object in the collection.
     */
    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            DemoObjectFragment fragment = new DemoObjectFragment();
            fragment.setChatEventListener(ChatActivity.this);
            Bundle args = new Bundle();
            if (App.selectedId < 0 || App.selectedIndex < 0) {
                Intent intent = ChatActivity.this.getIntent();
                App.selectedIndex = intent.getIntExtra(App.INDEX_STRING, App.selectedIndex);
                App.selectedId = intent.getLongExtra(App.ID_STRING, App.selectedId);
            }
            args.putLong(DemoObjectFragment.ARG_ID, App.selectedId);
            args.putInt(DemoObjectFragment.ARG_INDEX, App.selectedIndex);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return App.getContactList().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return App.getContactList().get(position).getName();
        }
    }

}
