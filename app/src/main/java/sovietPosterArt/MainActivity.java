package sovietPosterArt;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import com.firebase.client.Firebase;

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
import sovietPosterArt.data.api.sovietPosterArt.SovietArtMeService;
import sovietPosterArt.data.api.sovietPosterArt.model.Poster;
import sovietPosterArt.data.api.sovietPosterArt.model.Poster2;
import sovietPosterArt.data.api.sovietPosterArt.model.SovietArtMePosters;
import sovietPosterArt.sovietPosterArt.R;
import sovietPosterArt.ui.ArtFeedAdapter;
import sovietPosterArt.utils.App;
import sovietPosterArt.utils.ScreenUtils;

public class MainActivity extends GenericActivity {
    /**
     * MainActivity takes care of displaying the art work overview
     */

    @Bind(R.id.overview_recycler)
    RecyclerView mRecyclerView;

    private ArtFeedAdapter mArtFeedAdapter;
    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // setup RecyclerView
        mArtFeedAdapter = new ArtFeedAdapter(this);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mArtFeedAdapter);

        ScreenUtils.handleUiVisibilityChange(this); // todo remove??

        mDataManager = new DataManager() {
            @Override
            public void onDataLoaded(List<? extends Poster> data) {
                // todo: not working ...check
                boolean dataNotNull = data != null;
                App.log(TAG, "setting data on recycler with dataNotNull = " + dataNotNull);
                mArtFeedAdapter.setArtWorkCollection((ArrayList<Poster>) data);
            }
        };

        App.log(TAG, "pre .loadSovietArtMePosters()");
//        mDataManager.loadSovietArtMePosters();

        getPosterData();
//        new FirebaseManager();
    }


    public void getPosterData() { // todo abstract away
        new Thread(() -> {
            ArrayList<Poster> posters = new ArrayList<>();
            ArrayList<Poster2> posters2 = new ArrayList<>();


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
//                    App.log(TAG, "onResponse sovietArtMeApi returned postersss#: " + response.body().postersss.size());
                    App.log(TAG, response.toString());
                    posters.addAll(response.body().posters);
                    mArtFeedAdapter.setArtWorkCollection(posters);
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });

//            Call<SovietArtMePosters2> call2 = api.loadPostersData2();
//            //asynchronous call
//            call2.enqueue(new Callback<SovietArtMePosters2>() {
//                @Override
//                public void onResponse(Response<SovietArtMePosters2> response, Retrofit retrofit) {
////                    App.log(TAG, "onResponse sovietArtMeApi returned postersss#: " + response.body().postersss.size());
//                    App.log(TAG, response.toString());
//                    posters2.addAll(response.body().posters2);
//                }
//
//                @Override
//                public void onFailure(Throwable t) {
//                    Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });



        }).start();
    }
}
