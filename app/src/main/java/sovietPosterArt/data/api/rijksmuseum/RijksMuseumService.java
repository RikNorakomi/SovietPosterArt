package sovietPosterArt.data.api.rijksmuseum;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import retrofit.Callback;
import retrofit.http.Query;
import retrofit2.http.GET;
import sovietPosterArt.data.api.rijksmuseum.model.ArtistsResponse;
import sovietPosterArt.data.api.rijksmuseum.model.PaintingsResponse;

/**
 * The RijksMuseum has API's available for the following elements:
 * Collection: The complete online collection of the Rijksmuseum with all public data.
 * Content pages: Static pages as used on the website.
 * Usersets: Sets from Rijksstudio users.
 * Calendar: Calendar and availability of expositions, tours, etc.
 *
 * Extensive info on Rijksmuseum API can be found at: http://rijksmuseum.github.io/
 *
 * Example on creating url's with multiple query paramaters:
 * interface GitHubService {
        @GET("/search/repositories")
        RepositoriesResponse searchRepos(
        @Query("q") String query,
        @Query("since") Date since);
    }

 /search/repositories?q=retrofit&since=2015-08-27
 /search/repositories?q=retrofit&since=20150827
 */
public interface RijksMuseumService {

    String API_KEY = "Nvx6nBkA"; // todo: move to better place
    String BASE_URL = "https://www.rijksmuseum.nl/";
    String LANGUAGE_CODE = "en"; // possible values: en, nl
    String RESPONSE_DATA_FORMAT = "json"; // possible values: xml, json, jsonp

    String apiKey = "?key="+API_KEY;
    String dataFormat = "&dataFormat=" + RESPONSE_DATA_FORMAT;
    String basePath = "/api/"+ LANGUAGE_CODE + "/collection" + apiKey + dataFormat;

    @GET(basePath)
    void getArtworkByArtist(
            @Query("maker") String artist,
            Callback<ArtistsResponse> callback);

    @GET(basePath)
    void getPaintings(@Query("page") Integer page,
                      @Query("per_page") Integer pageSize,
                      Callback<List<PaintingsResponse>> callback);

    // For future reference

    String XML = "xml";
    String JSON = "json";
    String JSONP = "jsonp";

    // Language type
    // If you want the dataFormat to be set via interface method use:
    // @Query("dataFormat") @DataResponseFormat String dataResponseFormat,
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            XML,
            JSON,
            JSONP
    })
    @interface DataResponseFormat {}

}




/**
 *
 *
 * What are the differences between JSON and JSONP?: http://stackoverflow.com/questions/2887209/what-are-the-differences-between-json-and-jsonp
 *
* */
