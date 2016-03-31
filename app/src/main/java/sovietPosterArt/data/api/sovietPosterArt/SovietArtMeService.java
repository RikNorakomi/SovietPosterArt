package sovietPosterArt.data.api.sovietPosterArt;

import retrofit.Call;
import retrofit.http.GET;
import sovietPosterArt.data.api.sovietPosterArt.model.SovietArtMePosters;
import sovietPosterArt.data.api.sovietPosterArt.model.SovietArtMePostersPages;
import sovietPosterArt.data.api.sovietPosterArt.model.SovietArtMeTest;

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

    @GET("/soviet_art2.json")
    Call<SovietArtMePosters> loadPostersData2();

    /**
     * Loads url of pages from the sovietArtMe website that contain individual postersss and information
     */
    @GET("/soviet_art_me_poster_pages.json")
    Call<SovietArtMePostersPages> loadPosterPagesUrlList();

    @GET("/soviet_art_test.json")
    Call<SovietArtMeTest> loadTestUrlList();
}
