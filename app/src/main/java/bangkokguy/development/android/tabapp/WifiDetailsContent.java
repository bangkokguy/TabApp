package bangkokguy.development.android.tabapp;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
class WifiDetailsContent {

    private final static String TAG = WifiDetailsContent.class.getSimpleName();

    // An array of sample (dummy) items.
    private List<ConfigItem> items = new ArrayList<>();


    WifiDetailsContent(Context context) {
        ConfiguredHotSpots((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        ScannedHotSpots((WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
    }


    /**
     * all hot spots defined in the android phone
     */
    private void ConfiguredHotSpots(WifiManager wm) {List<WifiConfiguration> configuredHotSpots;

        Log.d(TAG, "ConfiguredHotSpots");
        if (wm != null) {
            configuredHotSpots = wm.getConfiguredNetworks();
            if (configuredHotSpots != null) {
                for (WifiConfiguration wc : configuredHotSpots) {
                    items.add(new ConfigItem(1, "SSID", wc.SSID, ""));
                }
            }
        }
    }

    /**
     * all hot spots scanned by android
     */
    private void ScannedHotSpots(WifiManager wm) {
        List<ScanResult> sr = wm.getScanResults();
        if (sr == null)
            Log.e(TAG, "empty scan result");
        else {
            for (ScanResult lsr:sr) { // process scan result list
                /*items.add(new ConfigItem(2, "BSSID", lsr.BSSID, ""));*/
                /*items.add(new ConfigItem(2, "capabilities", lsr.capabilities, ""));*/
                items.add(new ConfigItem(2, "SSID", lsr.SSID, ""));
                /*items.add(new ConfigItem(2, "centerFreq0", Integer.toString(lsr.centerFreq0), ""));
                items.add(new ConfigItem(2, "centerFreq1", Integer.toString(lsr.centerFreq1), ""));
                items.add(new ConfigItem(2, "channelWidth", Integer.toString(lsr.channelWidth), ""));
                items.add(new ConfigItem(2, "describeContents", Integer.toString(lsr.describeContents()), ""));
                items.add(new ConfigItem(2, "frequency", Integer.toString(lsr.frequency), ""));
                items.add(new ConfigItem(2, "level", Integer.toString(lsr.level), ""));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    items.add(new ConfigItem(2, "is80211mcResponder", Boolean.toString(lsr.is80211mcResponder()), ""));
                    items.add(new ConfigItem(2, "isPasspointNetwork", Boolean.toString(lsr.isPasspointNetwork()), ""));
                    items.add(new ConfigItem(2, "operatorFriendlyName", lsr.operatorFriendlyName.toString(), ""));
                    items.add(new ConfigItem(2, "venueName", lsr.venueName.toString(), ""));
                }

                items.add(new ConfigItem(2, "timestamp", Long.toString(lsr.timestamp), ""));*/
            }
        }
    }

    List<ConfigItem> getItems () {
        return items;
    }
    /**
     * A dummy item representing a piece of content.
     */
    public class ConfigItem {
        private final int[] pics = {
                R.drawable.ic_menu_camera,
                R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_manage,
                R.drawable.ic_menu_send,
                R.drawable.ic_menu_share};
        private int pic;
        private String id;
        private String content;
        private String details;

        ConfigItem(int pic, String id, String content, String details) {
            this.pic = pics[pic-1];
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return id + " " + content + " " + details;
        }

        public String getContent() {
            return content;
        }

        public String getDetails() {
            return details;
        }

        public int getPic() {
            return pic;
        }

        public String getId() {
            return id;
        }
    }
}
