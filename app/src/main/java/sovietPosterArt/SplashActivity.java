package sovietPosterArt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

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

    // todo: think about adding start pre-loading image thumbs in this activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);

        int delayTimeMs = 1400;
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            startActivity(intent);
//            handler.postDelayed(this::finish, 500);

            // todo: come up with nice transition to main activity screen probably slide use slide in from bottom
            // todo: animation on recycler view

        }, delayTimeMs);
    }
}