package sovietPosterArt;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by MEDION on 13-1-2016.
 */
public class GenericActivity extends AppCompatActivity {

    public final String TAG = getClass().getSimpleName();

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
