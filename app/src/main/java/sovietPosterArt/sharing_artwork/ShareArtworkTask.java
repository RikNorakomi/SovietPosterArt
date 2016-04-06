package sovietPosterArt.sharing_artwork;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import sovietPosterArt.data.api.sovietPosterArt.model.Poster;
import sovietPosterArt.utils.App;
import sovietPosterArt.utils.Constants;

/**
 * Created by Rik van Velzen, Norakomi.com, on 16-3-2016.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

public class ShareArtworkTask extends AsyncTask<String, Void, File> {
    private final String TAG = getClass().getSimpleName();
    private final Context context;
    private Poster artwork;

    public ShareArtworkTask(Context context, Poster artwork) {
        this.context = context;
        this.artwork = artwork;
        String imageUrl = artwork.getImageUrl();
        execute(imageUrl);
    }

    @Override protected File doInBackground(String... params) {
        String url = params[0]; // should be easy to extend to share multiple images at once
        try {
            return Glide
                    .with(context)
                    .load(url)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get() // needs to be called on background thread
                    ;
        } catch (Exception ex) {
            Log.w("SHARE", "Sharing " + url + " failed", ex);
            return null;
        }
    }
    @Override protected void onPostExecute(File result) {
        if (result == null) {
            App.log(TAG, "Error: file to share is null");
            return; }

        // Provider authorities should match authorities string as defined in AndroidManifest.xml
        String providerAuthorities = "sovietPosterArt.fileprovider";
        Uri uri = FileProvider.getUriForFile(context, providerAuthorities, result);
        share(uri); // startActivity probably needs UI thread
    }

    private void share(Uri uri) {
        /** Sharing to Facebook only shares the image and not the text. In order to share text you need to
         *  use the Facebook SDK */

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpg");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Art work from Norakomi's Soviet Poster Art app");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                Html.fromHtml("<p>I found this art work via the <a href=" + Constants.TEMP_APP_URL + ">"
                        + Constants.APP_NAME + " App</a>:</p>" +
                        "Title: " + artwork.getTitle() + "<br>" +
                        "Author: " + artwork.getAuthor() + "<br>" +
                        "Date: " + artwork.getYear() + "<br>" ));

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(shareIntent, "Share Soviet Poster Artwork:"));
    }
}
