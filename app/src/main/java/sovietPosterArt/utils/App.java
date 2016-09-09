package sovietPosterArt.utils;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by MEDION on 7-1-2016.
 */
public class App {

    private static final String NORAKOMI_TAG = "NSPA: ";

    public static void log(String string) {
        Log.e("", string);
    }

    public static void log(String tag, String string) {
        Log.v(NORAKOMI_TAG + tag, string);
    }

    public static void logError(String tag, String string) {
        Log.e(NORAKOMI_TAG + tag, string);
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

    public static void toast(String string) {
        Toast toast = Toast.makeText(AppContext.getContext(), string, Toast.LENGTH_SHORT);
//        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
//        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }

    public static void toastLong(String string) {
        Toast toast = Toast.makeText(AppContext.getContext(), string, Toast.LENGTH_LONG);
//        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
//        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }


}

