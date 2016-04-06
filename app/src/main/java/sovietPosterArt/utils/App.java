package sovietPosterArt.utils;

import android.util.Log;

/**
 * Created by MEDION on 7-1-2016.
 */
public class App {

    public static void log(String string) {
        Log.e("", string);
    }

    public static void log(String tag, String string) {
        Log.e(tag, string);
    }

    public static void logCurrentMethod() {
        Log.e("App", getCurrentMethodName());
    }

    public static String getCurrentMethodName() {
        String className = "UNKNOWN";
        try {
            className = Thread.currentThread().getStackTrace().getClass().getEnclosingClass().toString();
        } catch (Exception e){

        }

        return "class = " + className
                + "method: " + Thread.currentThread().getStackTrace()[4].getMethodName() + "()";
    }




}

