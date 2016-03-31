package sovietPosterArt.data;

import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import sovietPosterArt.data.api.sovietPosterArt.SovietArtMeService;
import sovietPosterArt.data.api.sovietPosterArt.model.Poster;

/**
 * Created by Rik van Velzen, Norakomi.com, on 6-3-2016.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */
public abstract class BaseDataManager {

    public final String TAG = getClass().getSimpleName();

    private SovietArtMeService sovietArtMeApi;

    public BaseDataManager() {
        // setup Artwork APIs
        createSovietArtMeApi();
    }

    public abstract void onDataLoaded(List<? extends Poster> data);

    public void createSovietArtMeApi(){
        sovietArtMeApi = new Retrofit.Builder()
                .baseUrl(SovietArtMeService.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SovietArtMeService.class);

    }
    public SovietArtMeService getSovietArtMeApi(){
        return sovietArtMeApi;
    }
}
