package sovietPosterArt;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import sovietPosterArt.data.DataManager;
import sovietPosterArt.data.api.sovietPosterArt.model.SovietArtMePosters;
import sovietPosterArt.data.api.sovietPosterArt.SovietArtMeService;
import sovietPosterArt.data.api.sovietPosterArt.model.Poster;
import sovietPosterArt.sovietPosterArt.R;
import sovietPosterArt.ui.RecyclerAdapter;
import sovietPosterArt.utils.App;

public class MainActivity extends GenericActivity {
    /**
     * MainActivity takes care of displaying the art work overview
     */

    @Bind(R.id.overview_recycler)
    RecyclerView mRecyclerView;

    private RecyclerAdapter mRecyclerAdapter;
    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        // setup RecyclerView
        mRecyclerAdapter = new RecyclerAdapter(this);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mRecyclerAdapter);

        handleUiVisibilityChange();

        mDataManager = new DataManager() {
            @Override
            public void onDataLoaded(List<? extends Poster> data) {
                boolean dataNotNull = data != null;
                App.log(TAG, "setting data on recycler with dataNotNull = " + dataNotNull);
                mRecyclerAdapter.setArtWorkCollection((ArrayList<Poster>) data);
            }
        };

        App.log(TAG, "pre .loadSovietArtMePosters()");
//        mDataManager.loadSovietArtMePosters();

        getPosterData();
    }

    private void handleUiVisibilityChange() {
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        App.log(TAG, "in onSystemUiVisibilityChange()");
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            // TODO: The system bars are visible. Make any desired
                            // adjustments to your UI, such as showing the action bar or
                            // other navigational controls.
                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.
                        }
                    }
                });
    }

    public void getPosterData() {
        new Thread(() -> {
            ArrayList<Poster> posters = new ArrayList<>();

            String BASE_URL = "http://www.norakomi.com/assets/json";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // prepare call in Retrofit 2.0
            SovietArtMeService api = retrofit.create(SovietArtMeService.class);

            Call<SovietArtMePosters> call = api.loadPostersData();
            //asynchronous call
            call.enqueue(new Callback<SovietArtMePosters>() {
                @Override
                public void onResponse(Response<SovietArtMePosters> response, Retrofit retrofit) {
                    App.log(TAG, "response body " + response.body().toString());
                    posters.addAll(response.body().posters);
                    for (Poster p : posters) {
                        App.log(TAG, "poster title: " + p.getTitle() + " filepath: " + p.getFilepath());
                    }

                    mRecyclerAdapter.setArtWorkCollection(posters);
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }).run();
    }
}
