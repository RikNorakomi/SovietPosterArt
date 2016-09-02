package sovietPosterArt.utils;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

/**
 * Created by MEDION on 7-1-2016.
 */
public class App {

    private final static String PRE_TAG = "Soviet: ";

    public static void log(String string) {
        Log.e("", string);
    }

    public static void log(String tag, String string) {
        Log.v(PRE_TAG + tag, string);
    }

    public static void logError(String tag, String string) {
        Log.e(PRE_TAG + tag, string);
    }

    public static void logError(String tag, String msg, Throwable t) {
        Log.e(PRE_TAG + tag, msg);
        Crashlytics.logException(new Throwable(msg, t));
    }



    public static void logCurrentMethod() {
        Log.e(PRE_TAG, getCurrentMethodName());
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

