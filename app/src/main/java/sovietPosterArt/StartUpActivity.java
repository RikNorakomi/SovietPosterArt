package sovietPosterArt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import sovietPosterArt.utils.AppContext;
import sovietPosterArt.utils.IdUtils;

/**
 * Created by Rik van Velzen, Norakomi.com, on 6-4-2016.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 * <p>
 * More info on adding a splash screen:
 * https://www.bignerdranch.com/blog/splash-screens-the-right-way/
 */
public class StartUpActivity extends AppCompatActivity {

    // todo: think about adding start pre-loading image thumbs in this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initThirdPartySDKsOnBackgroundThread();

        Intent intent = new Intent(AppContext.getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        int delayTimeMs = 1400;
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            AppContext.getContext().startActivity(intent);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 500);

            // todo: come up with nice transition to main activity screen probably slide use slide in from bottom
            // todo: animation on recycler view

        }, delayTimeMs);
    }

    private void initThirdPartySDKsOnBackgroundThread() {
        new Thread(this::initCrashlytics).start();
    }

    private void initCrashlytics() {
        Fabric.with(this, new Crashlytics());

        // TODO: Use the current user's information
        // You can call any combination of these three methods
        // Crashlytics.setUserEmail("user@fabric.io");
        // Crashlytics.setUserName("Test User");
        Crashlytics.setUserIdentifier(IdUtils.getUDID());
        Crashlytics.setString("IMEI", IdUtils.getIMEI());
        Crashlytics.setString("WLAN_MAC", IdUtils.getWLANMAC());
    }
}