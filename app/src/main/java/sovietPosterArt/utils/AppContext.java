package sovietPosterArt.utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by MEDION on 6-2-2016.
 */
public class AppContext extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        AppContext.context = getApplicationContext();
    }

    public static Context getContext() {
        return AppContext.context;
    }
}
