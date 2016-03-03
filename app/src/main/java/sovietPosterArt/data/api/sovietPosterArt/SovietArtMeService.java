package sovietPosterArt.data.api.sovietPosterArt;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by Rik van Velzen, Norakomi.com, on 27-2-2016.
 *
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */


public interface SovietArtMeService {
    @GET("/soviet_art.json")
    Call<SovietArtMePosters> loadPostersData();
}
