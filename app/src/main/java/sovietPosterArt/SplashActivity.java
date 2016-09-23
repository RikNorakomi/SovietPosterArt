package sovietPosterArt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

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
public class SplashActivity extends AppCompatActivity {

    // todo: think about adding start pre-loading and caching image thumbs for  in this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init Crashlytics
        Fabric.with(this, new Crashlytics());

        // Start Main Activity after slight delay
        Intent intent = new Intent(this, MainActivity.class);
        int delayTimeMs = 1000;
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            startActivity(intent);
//            handler.postDelayed(this::finish, 500);

            // todo: 1. come up with nice transition to main activity screen probably use slide in from bottom
            // todo: 2. animation on recycler view

        }, delayTimeMs);
    }
}