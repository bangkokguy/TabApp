package bangkokguy.development.android.tabapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;
import static android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION;

/**
 * Service to check the network connection and change to preferred network if necessary
 */
public class NetworkWatchdog extends Service {

    public NetworkWatchdog() { }

    private final static String TAG = NetworkWatchdog.class.getSimpleName();
    private final static boolean DEBUG = BuildConfig.BUILD_TYPE.equals("debug"); //true;
    private final static String EVENT_HISTORY = "event_history";

    final static int WIFI_DISCONNECTING = 1;            //AUDIO_CHAFING = 1;
    final static int WIFI_CONNECTING = 2;               //AUDIO_GENTLE_ALARM = 2;
    final static int MOBILE_DISCONNECTING = 3;          //AUDIO_JUST_LIKE_MAGIC = 3;
    final static int MOBILE_CONNECTING = 4;             //AUDIO_SOLEMN = 4;
    final static int ERROR = 5;                         //AUDIO_TWIRL = 5;
    final static int FAILOVER = 6;                      //AUDIO_WET = 6;

    WifiManager wm = null;
    ConnectivityManager conMan = null;
    NetworkInfo netInfo = null;
    WifiInfo wifiInfo = null;

    BroadcastReceiver wifiReceiver = new NetworkChanged();
    BroadcastReceiver scanReceiver = new ScanReceiver();

    PreferredHotSpot preferredHotSpot;
    ConfiguredHotSpots configuredHotSpots;
    Overlay overlay;
    AudioWarning aw;
    Intent intent = null;


    // Target we publish for clients to send messages to IncomingHandler.
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public void onCreate() {
        Log.init(recipient); //getSharedPreferences(EVENT_HISTORY, MODE_PRIVATE));
        if(DEBUG) Log.d(TAG, "onCreate");

        //Notification noti =
        aw = new AudioWarning(this);
        aw.notify(ERROR, "WatchDog started");

        wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        configuredHotSpots = new ConfiguredHotSpots();
        preferredHotSpot = new PreferredHotSpot("",0,0);

        conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        overlay = new Overlay();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(DEBUG) Log.d(TAG, "onStartCommand");

        // Register receivers
        registerReceiver(wifiReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        registerReceiver(scanReceiver, new IntentFilter(SCAN_RESULTS_AVAILABLE_ACTION));

        // Start network scan for available wifi connections
        if(DEBUG) Log.w(TAG, "scan started");
        wm.startScan();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if(DEBUG) Log.d(TAG, "onDestroy");

        // service stops, free up receivers
        unregisterReceiver(wifiReceiver);
        unregisterReceiver(scanReceiver);
    }

    private boolean connectToNetwork (int netID) {
        String TAG = NetworkWatchdog.TAG + ":connectToNetwork";
        if(DEBUG) Log.d(TAG, "connectToNetwork");

        // no preferred network found or preferred network is not active, we do nothing
        if (!configuredHotSpots.isSSIDActive(preferredHotSpot.getSSID())) {
            if(DEBUG) Log.d(TAG, "No preferred network");
            displayNetworkInfo(Color.CYAN);
            return false;
        }

        // wifi turned off, we do nothing
        if (!wm.isWifiEnabled()) {
            if(DEBUG) Log.d(TAG, "Wifi not Enabled");
            displayNetworkInfo(Color.WHITE);
            return false;
        }

        WifiInfo wi = wm.getConnectionInfo();

        // preferred network is identical to actual network, we do nothing
        if (wi.getNetworkId() == netID) {
            if(DEBUG) Log.w(TAG, "preferred network = current network, no change->" + Integer.toString(wi.getNetworkId()) + " " + Integer.toString(netID) + " " + preferredHotSpot.getSSID());
            displayNetworkInfo(Color.WHITE);
            return true;
        }

        // wifi enabled, we have a new network which is better than the current, go ahead and swap
        if(DEBUG) Log.d(TAG, "disconnect/connect " + Integer.toString(netID));
        aw.notify(FAILOVER, "disconnect/connect " + Integer.toString(netID));

        // we try to disconnect the actual network first; on error we change the text color to red
        if (!wm.disconnect()) {
            if(DEBUG) Log.d(TAG, "disconnect not ok");
            aw.notify(ERROR, "disconnect not ok");
            displayNetworkInfo(Color.RED);
            return false;
        }

        // disconnect was OK, now try to connect new network; on error we change the color to magenta
        if (!wm.enableNetwork(netID, true)) {
            if(DEBUG) Log.v(TAG, "Connect not ok");
            aw.notify(ERROR,"Connect not ok");
            displayNetworkInfo(Color.MAGENTA);
            return false;
        }

        // everything ok here;
        if(DEBUG) Log.v(TAG, "everything ok here->connecting to the new network was successful");
        displayNetworkInfo(Color.GREEN);
        return true;
    }

    /**
     * receive network changes
     * A change in network connectivity has occurred.
     * A default connection has either been established or lost.
     * The NetworkInfo for the affected network is sent as an extra;
     * it should be consulted to see what kind of connectivity event occurred.
     * Apps targeting Android 7.0 (API level 24) and higher do not receive this broadcast
     * if they declare the broadcast receiver in their manifest.
     * Apps will still receive broadcasts if they register their BroadcastReceiver
     * with Context.registerReceiver() and that context is still valid.
     * If this is a connection that was the result of failing over from a disconnected network,
     * then the FAILOVER_CONNECTION boolean extra is set to true.
     * For a loss of connectivity, if the connectivity manager is attempting to connect
     * (or has already connected) to another network, the NetworkInfo for the new network
     * is also passed as an extra.
     * This lets any receivers of the broadcast know that they should not necessarily
     * tell the user that no data traffic will be possible. Instead, the receiver should
     * expect another broadcast soon, indicating either that the failover attempt succeeded
     * (and so there is still overall data connectivity), or that the failover attempt failed,
     * meaning that all connectivity has been lost.
     * For a disconnect event, the boolean extra EXTRA_NO_CONNECTIVITY is set to true
     * if there are no connected networks at all.
     * Constant Value: "android.net.conn.CONNECTIVITY_CHANGE"
     */
    public class NetworkChanged extends BroadcastReceiver {

        private final String TAG = NetworkWatchdog.TAG + "*" + NetworkChanged.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            if(DEBUG) Log.d (TAG, "onReceive");

            // make the intent data visible for the outer class
            setIntent(intent);

            // get the reason for network change
            NetworkInfo ni = intent.getExtras().getParcelable("networkInfo");
            NetworkInfo.State s = ni.getState();
            int nt = ni.getType();

            // Change in mobile connection
            if (nt == TYPE_MOBILE) {
                if (s == NetworkInfo.State.DISCONNECTED || s == NetworkInfo.State.DISCONNECTING)
                    aw.notify(MOBILE_DISCONNECTING, "Mobile Data turning off");
                else if (s == NetworkInfo.State.CONNECTED || s == NetworkInfo.State.CONNECTING)
                    aw.notify(MOBILE_CONNECTING, "Mobile Data turning on");
            // Change in WIFI connection
            } else {
                if (s == NetworkInfo.State.DISCONNECTED || s == NetworkInfo.State.DISCONNECTING)
                    aw.notify(WIFI_DISCONNECTING, "Wifi disconnecting");
                else if (s == NetworkInfo.State.CONNECTED || s == NetworkInfo.State.CONNECTING)
                    aw.notify(WIFI_CONNECTING, "Wifi connecting");
            }

            // refresh the overlay info
            displayNetworkInfo(Color.YELLOW);

            // send message to the main activity
            recipient.replyTo (2, "network changed");
        }
    }

    void setIntent (Intent intent) {this.intent = intent;}

    void displayNetworkInfo (int color) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                if (value==null)value = "null";
                Log.d(TAG, "Extra Info-->"+String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }

        /*WifiManager wm = null;
          ConnectivityManager conMan = null;
          NetworkInfo netInfo = null;*/

        if (wm == null || conMan == null) {
            overlay.setText("error", Color.RED);
            aw.notify(ERROR, "General error: wm or conman == null");
            return;
        }

        netInfo = conMan.getActiveNetworkInfo();
        //api 23 and up: network = conMan.getActiveNetwork();
        wifiInfo = wm.getConnectionInfo();

        Log.d(TAG, "wifiInfo.getLinkSpeed()->"  + Integer.toString(wifiInfo.getLinkSpeed()));

        String text = "";
        if (netInfo == null) text = "no def.net";
        else {
            Log.d(TAG, "netInfo.getExtraInfo()->" + netInfo.getExtraInfo());
            Log.d(TAG, "netInfo.getState()->" + netInfo.getState().toString());
            Log.d(TAG, "netInfo.getType():0-mobile, 1-Wifi->" + netInfo.getType());
            Log.d(TAG, "netInfo.getSubtypeName()->" + netInfo.getSubtypeName());
            Log.d(TAG, "netInfo.getTypeName()->" + netInfo.getTypeName());
            Log.d(TAG, "netInfo.isAvailable()->" + Boolean.toString(netInfo.isAvailable()));
            Log.d(TAG, "netInfo.isConnected()->" + Boolean.toString(netInfo.isConnected()));
            Log.d(TAG, "netInfo.isConnectedOrConnecting()->" + Boolean.toString(netInfo.isConnectedOrConnecting()));
            Log.d(TAG, "netInfo.isFailover()->" + Boolean.toString(netInfo.isFailover()));
            Log.d(TAG, "netInfo.isRoaming()->" + Boolean.toString(netInfo.isRoaming()));

            switch (netInfo.getType()) {
                case TYPE_WIFI:
                    text = wifiInfo.getSSID()
                            + " "
                            + Integer.toString(wifiInfo.getFrequency())
                            + " "
                            + Integer.toString(wifiInfo.getLinkSpeed());
                    break;
                case TYPE_MOBILE:
                    String s = netInfo.getExtraInfo();
                    text = s.substring(s.indexOf("."), s.length());
                    break;
                default:
                    text = netInfo.getTypeName();
            }
        }

        overlay.setText(text, color);
    }

    /**
     * overlay view and its methods
     */
    private class Overlay {
        private TextView overlay;

        Overlay () {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(NetworkWatchdog.this)) {

                    WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

                    int overlayFlag;
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        overlayFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                    else
                        overlayFlag = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

                    WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams (
                            WindowManager.LayoutParams.WRAP_CONTENT, //60, //width
                            WindowManager.LayoutParams.WRAP_CONTENT, //60,  //height
                            overlayFlag, //WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, //TYPE_SYSTEM_ALERT
                            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, //FLAG_WATCH_OUTSIDE_TOUCH,
                            PixelFormat./*OPAQUE*/TRANSPARENT
                    );

                    layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
                    layoutParams.x = 350;
                    layoutParams.y = 10;

                    overlay = new TextView(NetworkWatchdog.this);
                    overlay.setText ("dummy");

                    windowManager.addView(overlay, layoutParams);
                }
            }
        }
        void setText (String s, int color) {
            overlay.setText(s);
            overlay.setTextColor(color);
        }
    }

    /**
     * Receive the scan result about the active hot spots
     */
    public class ScanReceiver extends BroadcastReceiver {

        private final String TAG = NetworkWatchdog.TAG + ":" + ScanReceiver.class.getSimpleName(); // for logging

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive");

            configuredHotSpots = new ConfiguredHotSpots(); // reload phone config list (could have been changed)
            preferredHotSpot = new PreferredHotSpot("",0,0); // no preferred config for the current scan result

            List<ScanResult> sr = wm.getScanResults();
            if (sr == null)
                Log.e(TAG, "empty scan result");
            else {
                for (ScanResult lsr:sr) { // process scan result list
                    preferredHotSpot = preferredHotSpot.swapHotSpot( // make the new hotspot the favorite one, if it's better than the actual one
                            new PreferredHotSpot(
                                    lsr.SSID,
                                    configuredHotSpots.getNetID(lsr.SSID),
                                    lsr.level));
                }
                Log.d(TAG, "preferred->" + preferredHotSpot.getSSID());
                connectToNetwork(preferredHotSpot.getNetID());
            }
        }
    }

    /**
     * The best hot spot to choose
     */
    private class PreferredHotSpot {
        private final String TAG = NetworkWatchdog.TAG + "*" + PreferredHotSpot.class.getSimpleName();
        private final String[] PREFERRED_SSID = {"Gyatzo", "MrWhite"};
        private final int[] PREFERRED_SSID_PRIORITY = {10, 20};
        private final int MINIMUM_SIGNAL_STRENGTH = - 80; //dBm --- -30 best -90 worst -80 acceptable
        private int priority = 0;
        private int signalStrength = 0;
        private String SSID = "";
        private int netID = 0;

        PreferredHotSpot (String SSID, int netID, int signalStrength) {
            setPriority(SSID);
            setSignalStrength(signalStrength);
            setSSID(SSID);
            setNetID(netID);
            Log.d(TAG, "SSID("
                    + SSID
                    + ") netID("
                    + Integer.toString(netID)
                    + ") level("
                    + Integer.toString(signalStrength)
                    + ") prio("
                    + Integer.toString(priority)
                    + ")");
        }

        void setPriority(String SSID) {
            int i = 0;
            for (String s : PREFERRED_SSID) {
                if (SSID.equals(s)) this.priority = PREFERRED_SSID_PRIORITY [i];
                i++;
            }
        }

        void setSignalStrength(int signalStrength) {this.signalStrength = signalStrength;}

        void setSSID(String SSID) {this.SSID = SSID; }

        void setNetID(int netID) {this.netID = netID; }

        String getSSID() {return this.SSID; }

        int getSignalStrength() {return this.signalStrength;}

        int getPriority() {return this.priority;}

        int getNetID() {return this.netID;}

        boolean isPreferredSSID(String SSID) {
            for (String s : PREFERRED_SSID) {
                if (SSID.equals(s)) {
                    Log.w(TAG, "preferred SSID"); return true;}
            }
            return false;
        }

        boolean isStronger (int signalStrength) {return (signalStrength > this.signalStrength);}

        boolean isPrior (int priority) {return (priority > this.priority);}

        boolean isWeak (int signalStrength) { return signalStrength < MINIMUM_SIGNAL_STRENGTH;}

        PreferredHotSpot swapHotSpot (PreferredHotSpot newHotSpot) {
            String TAG = this.TAG + ":swapHotSpot";
            // new hot spot is not configured in the phone -> no swap
            if(!configuredHotSpots.isSSIDActive(newHotSpot.getSSID())) {
                Log.w(TAG, "// new hot spot is not configured in the phone -> no swap");
                return this;
            }
            // new hot spot is not preferred, android should decide which network to connect -> no swap
            if (!isPreferredSSID(newHotSpot.getSSID())) {
                Log.w(TAG, "// new hots pot is not preferred, android should decide which network to connect -> no swap");
                return this;
            }
            // new hot spot is preferred but weak -> no swap
            if (isWeak(newHotSpot.getSignalStrength())) {
                Log.w(TAG, "// new hot spot is preferred but weak -> no swap");
                return this;
            }

            // new hot spot is preferred, not weak and has higher priority -> swap
            if (isPrior(newHotSpot.getPriority())) {
                Log.w(TAG, "// new hot spot is preferred, not weak and has higher priority -> swap");
                return newHotSpot;
            }

            // new hot spot is preferred, not weak but not prior, thou current is weak and the new one is stronger -> swap
            if (isWeak(this.getSignalStrength()) && isStronger(newHotSpot.getSignalStrength())){
                Log.w(TAG, "// new hot spot is preferred, not weak but not prior, thou current is weak and the new one is stronger -> swap");
                return newHotSpot;
            }

            // no change
            Log.w(TAG, "// no change");
            return this;
        }
    }

    /**
     * all hot spots defined in the android phone
     */
    private class ConfiguredHotSpots {
        private final String TAG = NetworkWatchdog.TAG + "*" + ConfiguredHotSpots.class.getSimpleName();
        private List<WifiConfiguration> configuredHotSpots;

        ConfiguredHotSpots () {
            Log.d(TAG, "ConfiguredHotSpots");
            configuredHotSpots = wm.getConfiguredNetworks();
        }

        boolean isSSIDActive(String SSID) {
            return (getNetID(SSID) != -1);
        }

        int getNetID (String SSID) {
            if (configuredHotSpots == null)
                return -1;
            for (WifiConfiguration wc : configuredHotSpots) {
                if(wc.SSID.equals("\""+SSID+"\"")) {
                    return wc.networkId;
                }
            }
            return -1;
        }
    }

    /**
     * Log wrapper->log entries will be passed to main activity through SharedPreferences
     */
    static private class Log {
        //private final static String HISTORY_KEY = "log_entry";
        //private static SharedPreferences eventHistory;
        private static Recipient recipient;

        static void init (Recipient r) {recipient = r;}
                //(SharedPreferences eventHistory) {
            //Log.eventHistory = eventHistory;
        //}

        static void d(String tag, String s) {
            writeToPreference("d", tag, s);
            android.util.Log.d(tag, s);
        }

        static void w(String tag, String s) {
            writeToPreference("w", tag, s);
            android.util.Log.w(tag, s);
        }

        static void e(String tag, String s) {
            writeToPreference("e", tag, s);
            android.util.Log.e(tag, s);
        }

        static void v(String tag, String s) {
            writeToPreference("v", tag, s);
            android.util.Log.v(tag, s);
        }

        interface Recipient {
            void replyTo (int what, Object obj);
        }
        // log entries will be received through SharedPreferencesChangedListener
        private static void writeToPreference(String type, String tag, String s) {
            /*SharedPreferences.Editor editor = eventHistory.edit(); //save log entries
            editor.putString(
                    HISTORY_KEY, //key
                    type + " " + s + " " + tag); //content
            editor.apply();*/
            recipient.replyTo( 1, (type + " " + s + " " + tag));
        }
    }
    //--------------

    Log.Recipient recipient = new Log.Recipient() {
        @Override
        public void replyTo(int what, Object obj) {
            if ((replyTo != null) && serviceConnected)
                try {
                    replyTo.send(Message.obtain(null, what, obj));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
        }
    };

/*    static void replyToActivity (int what, Object obj) {
        if ((replyTo != null) && serviceConnected)
            try {
                replyTo.send(Message.obtain(null, what, obj));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
    }*/

    /**
     * Command to the service to display a message
     */
    static final int MSG_SAY_HELLO = 1;
    static Messenger replyTo = null;
    static boolean  serviceConnected = false;

    /**
     * Handler of incoming messages from clients.
     */
    static class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "handleMessage->"+msg.toString());
            switch (msg.what) {
                case MSG_SAY_HELLO:
                    Log.v (TAG, "   Object->" + msg.obj);
                    replyTo = msg.replyTo;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        serviceConnected = true;
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        serviceConnected = false;
        return super.onUnbind(intent);
    }

    //--------------
}