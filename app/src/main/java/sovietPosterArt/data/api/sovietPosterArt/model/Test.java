package sovietPosterArt.data.api.sovietPosterArt.model;

import java.io.Serializable;

/**
 * Created by MEDION on 3-3-2016.
 */
public class Test implements Serializable {

    private final String TAG = getClass().getSimpleName();

    private String url;

    public String getPosterUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "Url: " + url;
    }
}
