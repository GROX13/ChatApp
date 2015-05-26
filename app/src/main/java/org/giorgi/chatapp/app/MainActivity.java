package org.giorgi.chatapp.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import org.giorgi.chatapp.R;
import org.giorgi.chatapp.model.Message;
import org.giorgi.chatapp.transport.ChatEventListener;
import org.giorgi.chatapp.userintrface.ChatActivity;

import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, ChatEventListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.getChatTransport().addChatEventListener(this);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        App.saveDataToDatabase();
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((Switch) view).isChecked();

        if (on) {
            // TODO: Enable vibrate
            Log.d("", "ჩეირთო ბიჯო!");
        } else {
            // TODO: Disable vibrate
            Log.d("", "გამეირთო ბიჯო!");
        }
    }

    @Override
    public void onIncomingMsg(Message m) {
        // TODO:
    }

    @Override
    public void onOutgoingMsg(Message m) {
        // TODO:
    }

    @Override
    public void onStatusChanged(String contactId, boolean isOnline) {
        // TODO:
    }

    /**
     * A placeholder fragment containing a recently contact list view.
     */
    public static class RecentlyMessagedListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public RecentlyMessagedListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static RecentlyMessagedListFragment newInstance(int sectionNumber) {
            RecentlyMessagedListFragment fragment = new RecentlyMessagedListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView =
                    inflater.inflate(R.layout.fragment_recent_contacts_list, container, false);
            ((ListView) rootView).setAdapter(
                    new RecentView());
            return rootView;
        }

        private class RecentView extends BaseAdapter {

            public RecentView() {
                App.registerObserver(this);
            }

            @Override
            public int getCount() {
                return App.getRecentContactList().size();
            }

            @Override
            public Object getItem(int position) {
                return App.getContactWithId(App.getRecentContactList().get(position));
            }

            @Override
            public long getItemId(int position) {
                return App.getRecentContactList().get(position);
            }

            @Override
            @SuppressLint("ViewHolder")
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View row;
                row = inflater.inflate(R.layout.recent_contact_element, parent, false);
                TextView name = (TextView) row.findViewById(R.id.contact_name),
                        phone = (TextView) row.findViewById(R.id.last_message);
                ImageView image = (ImageView) row.findViewById(R.id.avatar_image);
                ImageView imageStatus = (ImageView) row.findViewById(R.id.status_image);
                ImageView messageStatus = (ImageView) row.findViewById(R.id.message_read_status);

                List<Long> recent = App.getRecentContactList();
                name.setText(App.getContactWithId(recent.get(position)).getName());
                // May be error
                phone.setText(App.getContactWithId(recent.get(position)).getMessage(0).getMessage());
                image.setImageBitmap(App.getContactWithId(recent.get(position)).getAvatarBitmap());

                if (App.getContactWithId(recent.get(position)).getOnlineStatus())
                    imageStatus.setImageResource(R.drawable.online);
                else
                    imageStatus.setImageResource(R.drawable.offline);

                if (App.getContactWithId(recent.get(position)).isUnreadMessage())
                    messageStatus.setImageResource(R.drawable.unread);
                else
                    messageStatus.setImageResource(R.drawable.read);

                ((ListView) parent).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        App.selectedIndex = position;
                        App.selectedId = App.getContactList().get(position).getId();
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        startActivity(intent);
                    }
                });

                return row;
            }

        }
    }

    /**
     * A placeholder fragment containing a contact list view.
     */
    public static class ContactListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ContactListFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ContactListFragment newInstance(int sectionNumber) {
            ContactListFragment fragment = new ContactListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_contact_list, container, false);
            ((ListView) rootView).setAdapter(
                    new ContactsView());
            return rootView;
        }

        private class ContactsView extends BaseAdapter {

            public ContactsView() {
                App.registerObserver(this);
            }

            @Override
            public int getCount() {
                return App.getContactList().size();
            }

            @Override
            public Object getItem(int position) {
                return App.getContactList().get(position);
            }

            @Override
            public long getItemId(int position) {
                return App.getContactList().get(position).getId();
            }

            @Override
            @SuppressLint("ViewHolder")
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View row;
                row = inflater.inflate(R.layout.contact_element, parent, false);
                TextView name = (TextView) row.findViewById(R.id.contact_name),
                        phone = (TextView) row.findViewById(R.id.phone_number);
                ImageView image = (ImageView) row.findViewById(R.id.avatar_image);
                ImageView imageStatus = (ImageView) row.findViewById(R.id.status_image);
                ImageView messageStatus = (ImageView) row.findViewById(R.id.message_read_status);

                name.setText(App.getContactList().get(position).getName());
                phone.setText(App.getContactList().get(position).getPhone());
                image.setImageBitmap(App.getContactList().get(position).getAvatarBitmap());

                if (App.getContactList().get(position).getOnlineStatus())
                    imageStatus.setImageResource(R.drawable.online);
                else
                    imageStatus.setImageResource(R.drawable.offline);

                if (App.getContactList().get(position).isUnreadMessage())
                    messageStatus.setImageResource(R.drawable.unread);
                else
                    messageStatus.setImageResource(R.drawable.read);

                ((ListView) parent).setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        App.selectedIndex = position;
                        App.selectedId = App.getContactList().get(position).getId();
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        startActivity(intent);
                    }
                });

                return row;
            }

        }
    }

    /**
     * A placeholder fragment containing a settings simple view.
     */
    public static class SettingsFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public SettingsFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static SettingsFragment newInstance(int sectionNumber) {
            SettingsFragment fragment = new SettingsFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.contact_settings, container, false);
            if (App.selectedId >= 0) {
                ImageView avatar = (ImageView) view.findViewById(R.id.settings_avatar_image);
                TextView name = (TextView) view.findViewById(R.id.settings_contact_name);
                TextView phone = (TextView) view.findViewById(R.id.settings_phone_number);
                avatar.setImageBitmap(App.getContactWithId(App.selectedId).getAvatarBitmap());
                name.setText(App.getContactWithId(App.selectedId).getName());
                phone.setText(App.getContactWithId(App.selectedId).getPhone());
            }
            return view;
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_main, container, false);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return RecentlyMessagedListFragment.newInstance(position + 1);
                case 1:
                    return ContactListFragment.newInstance(position + 1);
                case 2:
                    return SettingsFragment.newInstance(position + 1);
                default:
                    return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_recent).toUpperCase(l);
                case 1:
                    return getString(R.string.title_contacts).toUpperCase(l);
                case 2:
                    return getString(R.string.title_settings).toUpperCase(l);
            }
            return null;
        }
    }

}
