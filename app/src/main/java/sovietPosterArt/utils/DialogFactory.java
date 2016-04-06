package sovietPosterArt.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import java.util.HashMap;

/**
 * Created by Rik van Velzen on 10-1-2016.
 * For more info on using Java 8 Lambda expressions
 * http://stackoverflow.com/questions/4480334/how-to-call-a-method-stored-in-a-hashmap-java
 * https://frenchstone.wordpress.com/2015/07/25/how-to-setup-the-retrolambda-to-android-studio-project/
 */
public class DialogFactory extends DialogConfigurations {

    public static final int DIALOG_ONE = 1;
    public static final int DIALOG_TWO = 2;
    public static final int DIALOG_THREE = 3;

    private HashMap<Integer, Runnable> methodMap = new HashMap<>();

    public void setMethodMap() {
        methodMap.put(DIALOG_ONE, this::configureDialogOne);
        methodMap.put(DIALOG_TWO, this::configureDialogTwo);
        methodMap.put(DIALOG_THREE, this::configureDialogThree);
    }

    public DialogFactory(Context context) {
        setContext(context);
        setMethodMap();
    }

    // setting return type to AlertDialog.builder allows for chaining of methods
    public void showDialog(int dialogType, ThreeButtonCallBack delegate) {
        methodMap.get(dialogType).run();

        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(textPositiveButton, (dialog, which) -> {delegate.onPositiveButtonClicked();})
                .setNegativeButton(textNegativeButton, (dialog, which) -> {delegate.onNegativeButtonClicked();})
                .setNeutralButton(textNeutralButton, (dialog, which) -> {delegate.onNeutralButtonClicked();})
                .setCancelable(isCancellable)
                .show();
    }


    public interface ThreeButtonCallBack {
        void onPositiveButtonClicked();

        void onNegativeButtonClicked();

        void onNeutralButtonClicked();
    }

    public interface TwoButtonCallBack {
        void onPositiveButtonClicked();

        void onNegativeButtonClicked();
    }

    public interface SingleButtonCallBack {
        void onPositiveButtonClicked();
    }
}
