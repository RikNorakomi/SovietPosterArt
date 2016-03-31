package sovietPosterArt.data;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import sovietPosterArt.data.api.sovietPosterArt.model.Poster;
import sovietPosterArt.data.api.sovietPosterArt.model.SovietArtMePosters;
import sovietPosterArt.utils.App;

/**
 * Created by Rik van Velzen, Norakomi.com, on 6-3-2016.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */
public abstract class DataManager extends BaseDataManager {

    public void loadSovietArtMePosters(){
        App.log(TAG, "in loadSovietArtMePosters()");

        Call<SovietArtMePosters> call = getSovietArtMeApi().loadPostersData();

        //asynchronous call
        call.enqueue(new Callback<SovietArtMePosters>() {
            @Override
            public void onResponse(Response<SovietArtMePosters> response, Retrofit retrofit) {
                App.log(TAG, "response body " + response.body().toString());
                ArrayList<Poster> posters = new ArrayList<>();
                posters.addAll(response.body().posters);
                for (Poster p : posters) {
                    App.log(TAG, "poster title: " + p.getTitle());
                }

                onDataLoaded(posters);
            }

            @Override
            public void onFailure(Throwable t) {
                App.log(TAG, "Failed to load data in loadSovietArtMePosters()");
            }
        });
    }
}
