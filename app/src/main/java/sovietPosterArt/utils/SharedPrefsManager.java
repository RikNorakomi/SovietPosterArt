package sovietPosterArt.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.Map;

import sovietPosterArt.sovietPosterArt.R;


/**
 * Created by Rik on 2/10/2016.
 */
public class SharedPrefsManager {

    private final String TAG = getClass().getSimpleName();

    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor prefsEditor = null;
    private Map<String, ?> sharedPrefsMap = null;

    public static final String IS_FAVORITE = "isFavorite";
    public static final String NUMBER_OF_TIMES_APP_GOT_STARTED = "numberOfTimesAppGotStarted";

    public SharedPrefsManager() {
        initSharedPrefs();
    }

    /**
     * Generic method to get a value using from a sharedPreferences Key; notice that null is returned instead of a default value
     * when a key is not present
     */

    public <Any> Any getSharedPref(@NonNull String sharedPrefsKey) {
        return getSharedPref(sharedPrefsKey, null);
    }

    public <Any> Any getSharedPref(@NonNull String sharedPrefsKey, Object defaultValue) {
        if (sharedPreferences == null) {
            initSharedPrefs();
        }

        if (!sharedPreferences.contains(sharedPrefsKey) && defaultValue == null) {
            App.log(TAG, "Shared Preferences does not contain the requested Key/Value pair");
            return null;
        }



        Object value = sharedPrefsMap.get(sharedPrefsKey);
        App.log(TAG, "value type of key: " + sharedPrefsKey + " = " + sharedPrefsMap.get(sharedPrefsKey).getClass().toString() +
                " value = " + value);

        if (value instanceof Boolean) {
            return (Any) value;
        } else if (value instanceof Integer) {
            return (Any) value;
        } else if (value instanceof Float) {
            return (Any) value;
        } else if (value instanceof String) {
            return (Any) value;
        }

        return null;
        /** Fore more info on dynamic return types: http://stackoverflow.com/questions/8191536/dynamic-return-type-in-java-method */
    }

    public void saveSharedPreference(@NonNull String key, Object value) {
        prefsEditor = sharedPreferences.edit();
        if (value instanceof String) {
            prefsEditor.putString(key, (String) value);
        } else if (value instanceof Boolean) {
            prefsEditor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            prefsEditor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            prefsEditor.putFloat(key, (Float) value);
        } else {
            /** value isn't of a valid type */
            App.log(TAG, "key's: " + key + " value isn't of a known type");
            return;
        }
        prefsEditor.apply();
    }

    public void initSharedPrefs() {
        sharedPreferences = AppContext.getContext().getSharedPreferences(AppContext.getContext().getString(R.string.shared_prefs_file_name), Activity.MODE_PRIVATE);
        sharedPrefsMap = sharedPreferences.getAll();
    }


}
