package sovietPosterArt.data.api.sovietPosterArt;

import retrofit.Call;
import retrofit.http.GET;
import sovietPosterArt.data.api.sovietPosterArt.model.SovietArtMePosters;

/**
 * Created by Rik van Velzen, Norakomi.com, on 27-2-2016.
 *
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */


public interface SovietArtMeService {

    String ENDPOINT = "http://www.norakomi.com/assets/json"; // todo check necessity to use / at end/start of urls

    /**
     * Loads poster objects from json
     */
    @GET("/soviet_art.json")
    Call<SovietArtMePosters> loadPostersData();


}
