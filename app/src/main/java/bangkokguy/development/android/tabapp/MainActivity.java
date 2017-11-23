package bangkokguy.development.android.tabapp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerTabStrip;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.LinePageIndicator;
import com.viewpagerindicator.TabPageIndicator;
import com.viewpagerindicator.TitlePageIndicator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ServiceConnection
{

    final static private String TAG = MainActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    static PagerAdapter pagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    TitlePageIndicator titleIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startService(new Intent(this, NetworkWatchdog.class));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the n primary sections of the activity.
        /*mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());*/
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        // This is the code part, where our fragment should be filled in the right way
        /*http://viewpagerindicator.com/*/
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(pagerAdapter);

        //Bind the title indicator to the adapter
        titleIndicator = findViewById(R.id.titles);
        titleIndicator.setViewPager(mViewPager);

        /*PagerTabStrip pagerTabStrip = mViewPager.findViewById(R.id.pager_tab_strip);*/
        /*PagerTitleStrip pagerTitleStrip = mViewPager.findViewById(R.id.pager_title_strip);*/

        /*pagerTabStrip.setTabIndicatorColorResource(R.color.colorAccent);
        pagerTabStrip.setDrawFullUnderline(true);*/
        /*pagerTabStrip.addView();*/
        /*pagerTitleStrip.setTextColor(Color.RED);*/

        // from here this is the floating action button - no changes needed at the moment
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        // from here this is the sliding menu - no changes needed at the moment
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Bind to NetworkWatchdog
        bindService(new Intent(this, NetworkWatchdog.class), this /*this=implemented ServiceConnection*/, 0);

    }

    Messenger serviceMessenger = null;
    boolean serviceConnected = false;
    Messenger messageBack = null;

    /**
     * Following two methods implement the interface callbacks from ServiceConnection
     * @param componentName ?
     * @param iBinder ?
     */
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d(TAG, "onServiceConnected");
        serviceMessenger = new Messenger(iBinder);
        serviceConnected = true;

        try {
            Message msg = Message.obtain(null, 1, "gekkó");
            messageBack = new Messenger(new IncomingHandler());
            msg.replyTo = messageBack;
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    static Fragment networkDetails = null;

    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "Message in->"+msg.toString());
            switch (msg.what) {
                case 1:
                    Log.v(TAG, "   Object->" + msg.obj);
                    break;
                case 2:
                    Log.v(TAG, "   case 2->");
                    networkDetails = pagerAdapter.getNetworkDetailsFragment();
                    if (networkDetails != null) {
                        Messenger m = networkDetails.getArguments().getParcelable(NetworkDetailsFragment.ARG_MESSENGER);
                        if (m != null)
                            try {
                                m.send(Message.obtain(null, 1, "basszki anyád"));
                                pagerAdapter.notifyDataSetChanged();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                    }
                    // how about removing the dirty fragment and create a new?
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d(TAG, "onServiceDisconnected");
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (serviceConnected) {
            unbindService(this);
            serviceConnected = false;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
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

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    /*public class SectionsPagerAdapter extends FragmentPagerAdapter {
        final static int FRAGMENT_LOG = 0;
        final static int FRAGMENT_WIFI_LIST = 1;
        final static int FRAGMENT_WIFI_DETAILS = 2;
        final static int FRAGMENT_NETWORK_DETAILS = 3;
        final static int FRAGMENT_I_DO_NOT_KNOW = 4;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //ItemFragment extends Fragment
            switch (position) {
                case FRAGMENT_LOG:
                    return ItemFragment.newInstance(position + 1);
                case FRAGMENT_WIFI_LIST:
                    return ItemFragment.newInstance(position + 1);
                case FRAGMENT_WIFI_DETAILS:
                    return WifiDetailsFragment.newInstance(position + 1);
                case FRAGMENT_NETWORK_DETAILS:
                    networkDetails = NetworkDetailsFragment.newInstance(position + 1);
                    return networkDetails;
                case FRAGMENT_I_DO_NOT_KNOW:
                    return ItemFragment.newInstance(position + 1);

                default:
                    throw new NullPointerException("Fragment type out of range");
            }
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }
    }*/
}
