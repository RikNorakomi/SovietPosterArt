package sovietPosterArt;

import android.net.Uri;

/**
 * Created by Rik van Velzen, Norakomi.com, on 16-3-2016.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */

/**
 * Cached glide image files don't have extensions so by default FileProvider.getType() returns
 * application/octet-stream as the mime-type which will cause issues sharing images
 * Override the getType method to return the desired mime type and use this class as the provider specified
 * in AndroidManifest.xml */

public class ImageFileProvider extends android.support.v4.content.FileProvider {
    @Override public String getType(Uri uri) { return "image/jpeg"; }
}
