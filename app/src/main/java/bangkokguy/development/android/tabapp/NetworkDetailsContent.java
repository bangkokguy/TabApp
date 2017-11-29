package bangkokguy.development.android.tabapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * http://cellidfinder.com/cells/findcell
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
class NetworkDetailsContent {

    private final static String TAG = NetworkDetailsContent.class.getSimpleName();

    // An array of sample (dummy) items.
    private List<ConfigItem> items = new ArrayList<>();


    NetworkDetailsContent(Context context) {
        // Add an items pro config property
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan != null) {
            NetworkInfo netInfo = conMan.getActiveNetworkInfo();
            if (netInfo != null) {
                items.add(new ConfigItem(1, "getReason", netInfo.getReason(), "details"));
                items.add(new ConfigItem(1, "getSubtypeName", netInfo.getSubtypeName(), "details"));
                items.add(new ConfigItem(1, "getTypeName", netInfo.getTypeName(), "details"));
                items.add(new ConfigItem(1, "isFailover", Boolean.toString(netInfo.isFailover()), "details"));
                items.add(new ConfigItem(1, "isConnectedOrConnecting", Boolean.toString(netInfo.isConnectedOrConnecting()), "details"));
                items.add(new ConfigItem(1, "isConnected", Boolean.toString(netInfo.isConnected()), "details"));
                items.add(new ConfigItem(1, "getType", Integer.toString(netInfo.getType()), ""));
                items.add(new ConfigItem(1, "getSubType", Integer.toString(netInfo.getSubtype()), ""));
                NetworkInfo.State s = netInfo.getState();
                items.add(new ConfigItem(2, "getState", s.toString(), ""));
                items.add(new ConfigItem(1, "getExtraInfo", netInfo.getExtraInfo(), ""));
                NetworkInfo.DetailedState ds = netInfo.getDetailedState();
                items.add(new ConfigItem(3, "getDetailedState", ds.toString(), ""));
                items.add(new ConfigItem(1, "isAvailable", Boolean.toString(netInfo.isAvailable()), ""));
                items.add(new ConfigItem(1, "isRoaming", Boolean.toString(netInfo.isRoaming()), ""));
            }
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> allCellInfo;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "no permission");
        } else {
            items.add(new ConfigItem(4, "line 1 number", telephonyManager.getLine1Number(), ""));
            items.add(new ConfigItem(4, "sim operator", telephonyManager.getSimOperator(), ""));
            items.add(new ConfigItem(4, "sim country", telephonyManager.getSimCountryIso(), ""));
            items.add(new ConfigItem(4, "sim operator name", telephonyManager.getSimOperatorName(), ""));
            items.add(new ConfigItem(4, "network operator", telephonyManager.getNetworkOperator(), ""));
            items.add(new ConfigItem(4, "network operator name", telephonyManager.getNetworkOperatorName(), ""));
            allCellInfo = telephonyManager.getAllCellInfo();
            if (allCellInfo != null)
                for (CellInfo c : allCellInfo) {
                    items.add(new ConfigItem(5, "cellInfo", c.toString(), ""));
                    items.add(new ConfigItem(5, "addInfo", getAddInfo(c), ""));

                }
        }
    }


    public String getAddInfo(CellInfo cellInfo) {
        String additional_info = "nothing";
        if (cellInfo instanceof CellInfoGsm) {
            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
            CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
            additional_info = "cell identity " + cellIdentityGsm.getCid() + "\n"
                    + "Mobile country code " + cellIdentityGsm.getMcc() + "\n"
                    + "Mobile network code " + cellIdentityGsm.getMnc() + "\n"
                    + "local area " + cellIdentityGsm.getLac() + "\n";
        } else if (cellInfo instanceof CellInfoLte) {
            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            additional_info = "cell identity " + cellIdentityLte.getCi() + "\n"
                    + "Mobile country code " + cellIdentityLte.getMcc() + "\n"
                    + "Mobile network code " + cellIdentityLte.getMnc() + "\n"
                    + "physical cell " + cellIdentityLte.getPci() + "\n"
                    + "Tracking area code " + cellIdentityLte.getTac() + "\n";
        } else if (cellInfo instanceof CellInfoWcdma) {
            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
            CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
            additional_info = "cell identity " + cellIdentityWcdma.getCid() + "\n"
                    + "Mobile country code " + cellIdentityWcdma.getMcc() + "\n"
                    + "Mobile network code " + cellIdentityWcdma.getMnc() + "\n"
                    + "local area " + cellIdentityWcdma.getLac() + "\n";
        }
        return additional_info;
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
