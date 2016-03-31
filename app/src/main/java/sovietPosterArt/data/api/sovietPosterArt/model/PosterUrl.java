package sovietPosterArt.data.api.sovietPosterArt.model;

import java.io.Serializable;

/**
 * Created by MEDION on 3-3-2016.
 */
public class PosterUrl implements Serializable {

    private final String TAG = getClass().getSimpleName();
    private static final String URL_BASE_PATH = "http://sovietart.me";

    private String url;

    public String getPosterUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Url: " + url;
    }
}
