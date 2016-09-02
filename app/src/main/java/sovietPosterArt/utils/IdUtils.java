package sovietPosterArt.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

/**
 * Created by Rik van Velzen, Norakomi.com, on 2-9-2016.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */
public class IdUtils {

    /*
     * Getting Unique device Id's: http://www.technetexperts.com/mobile/getting-unique-device-id-of-an-android-smartphone/
     */

    private static final String TAG = IdUtils.class.getSimpleName();

    private static final String ERROR_RETRIEVING_ID = "Couldn't retrieve requested Id from device";

    public static String getUDID() {
        String UDID = ERROR_RETRIEVING_ID;
        try {
            UDID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Throwable e) {
            String errorMessage = "Couldn't get UDID from device.";
            App.logError(TAG, errorMessage, e);
        }
        return UDID;
    }

    /**
     * Retreives International Mobile Equipment Identity from device
     * Requires android.permission.READ_PHONE_STATE permission
     *
     * @return
     */
    public static String getIMEI() {
        String IMEI = ERROR_RETRIEVING_ID;
        try {
            TelephonyManager TelephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = TelephonyManager.getDeviceId();
        } catch (Throwable e) {
            String errorMessage = "Couldn't get IMEI from device.";
            App.logError(TAG, errorMessage, e);
        }
        return IMEI;
    }

    /**
     * Retreives WLAN MAC address from device
     * Requieres android.permission.ACCESS_WIFI_STATE permission
     *
     * @return
     */
    public static String getWLANMAC() {
        String WLAN_MAC = ERROR_RETRIEVING_ID;
        try {
            WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
            WLAN_MAC = wifiManager.getConnectionInfo().getMacAddress();
        } catch (Exception e) {
            String errorMessage = "Couldn't get IMEI from device.";
            App.logError(TAG, errorMessage, e);
        }
        return WLAN_MAC;
    }

    /**
     * Retreives WLAN MAC address from device
     * Requieres android.permission.ACCESS_WIFI_STATE permission
     *
     * @return
     */
    public static String getBluetoothAddress() {
        String BLUETOOTH_ADDRESS = ERROR_RETRIEVING_ID;
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BLUETOOTH_ADDRESS = bluetoothAdapter.getAddress();
        } catch (Exception e) {
            String errorMessage = "Couldn't get IMEI from device.";
            App.logError(TAG, errorMessage, e);
        }
        return BLUETOOTH_ADDRESS;
    }


    private static Context getContext() {
        return AppContext.getContext();
    }
}
