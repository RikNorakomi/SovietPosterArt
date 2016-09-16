package sovietPosterArt.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import sovietPosterArt.sovietPosterArt.R;

/**
 * Created by Rik van Velzen, Norakomi.com, on 16-9-2016.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */
public class AppPreferences {

    private final String TAG = getClass().getSimpleName();

    private static final String DOWNLOAD_HIGHRES = "downloadHighRes";

    private static AppPreferences instance;

    private SharedPreferences appSharedPrefs = null;
    private SharedPreferences.Editor prefsEditor = null;

    public static AppPreferences sharedInstance() {
        if (instance == null) {
            instance = new AppPreferences();
        }

        if (AppContext.getContext() != null && instance.appSharedPrefs == null) {
            instance.setContext(AppContext.getContext());
        }

        return instance;
    }

    public void setContext(Context context) {
        if (appSharedPrefs == null && context != null) {
            appSharedPrefs = context.getSharedPreferences(AppContext.getContext().getString(R.string.shared_prefs_file_name), Activity.MODE_PRIVATE);
            prefsEditor = appSharedPrefs.edit();
        }
    }

    public boolean shouldDownloadHighResImages() {
        boolean downloadHRImages = appSharedPrefs.getBoolean(DOWNLOAD_HIGHRES, false);
        return downloadHRImages;
    }

    public void setDownloadHighResImages(boolean downloadHighResImages){
        if (prefsEditor==null){
            App.logError(TAG, "prefsEditor is null!");
            return;
        }
        prefsEditor.putBoolean(DOWNLOAD_HIGHRES, downloadHighResImages).apply();
    }
}
