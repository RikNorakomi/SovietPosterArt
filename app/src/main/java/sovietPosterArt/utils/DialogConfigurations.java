package sovietPosterArt.utils;

import android.content.Context;

/**
 * Created by Rik van Velzen on 10-1-2016.
 */
public class DialogConfigurations {

    public CharSequence title = null;
    public CharSequence message = null;
    public CharSequence textPositiveButton = null;
    public CharSequence textNegativeButton = null;
    public CharSequence textNeutralButton = null;

    public boolean isCancellable = false;

    public Context context;

    public void setContext(Context context){
        this.context = context;
    }

    /** Dialog One:
     *  Dialog with title, message and 1 button
     * **/
    public void configureDialogOne(){
//        title = context.getString(R.string.dialog_one_title);
//        message = context.getString(R.string.dialog_one_message);
//        textPositiveButton = context.getString(R.string.dialog_one_button_positive);
//        isCancellable = false;
    }

    /** Dialog Two:
     *  Dialog without title(!), message and 2 buttons
     * **/
    public void configureDialogTwo(){
//        message = context.getString(R.string.dialog_two_message);
//        textPositiveButton = context.getString(R.string.dialog_two_button_positive);
//        textNegativeButton = context.getString(R.string.dialog_two_button_negative);
//        isCancellable = true;
    }

    /** Dialog Three:
     *  Dialog with title, without message(!) and 3 buttons
     * **/
    public void configureDialogThree(){
//        title = context.getString(R.string.dialog_three_title);
//        textPositiveButton = context.getString(R.string.dialog_three_button_positive);
//        textNegativeButton = context.getString(R.string.dialog_three_button_negative);
//        textNeutralButton = context.getString(R.string.dialog_three_button_neutral);
//        isCancellable = false;
    }


}
