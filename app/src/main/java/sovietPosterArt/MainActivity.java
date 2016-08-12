package sovietPosterArt;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.ArrayList;

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
import sovietPosterArt.data.api.sovietPosterArt.model.SovietArtMePosters;
import sovietPosterArt.data.firebase.SovietArtMePage;
import sovietPosterArt.sovietPosterArt.R;
import sovietPosterArt.ui.ArtFeedAdapter;
import sovietPosterArt.utils.Constants;
import sovietPosterArt.utils.ScreenUtils;

public class MainActivity extends GenericActivity {
    /**
     * MainActivity takes care of displaying the art work overview
     */

    @Bind(R.id.overview_recycler) RecyclerView mRecyclerView;
    @Bind(R.id.fab_menu_with_multiple_actions) FloatingActionsMenu mFabMenu;
//    @Bind(R.id.fab_menu_action_search) FloatingActionsMenu mFabActionSearch;
//    @Bind(R.id.fab_menu_action_filter) FloatingActionButton mFabActionFilter;

    private ArtFeedAdapter mArtFeedAdapter;
    private DataManager mDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // setup RecyclerView

        int numberOfColumns = 2;
        if (ScreenUtils.isTablet(this)) {
            numberOfColumns = 3;
        }
        mArtFeedAdapter = new ArtFeedAdapter(this);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(numberOfColumns, LinearLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(mArtFeedAdapter);


//        getPosterDataViaJSON();
        getPosterDataViaFirebase();
    }

    public void getPosterDataViaFirebase() {
        ArrayList<Poster> posters = new ArrayList<>();

        Firebase posterRefs = new Firebase(Constants.FIREBASE_HOME_POSTERS);
        posterRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                App.log(TAG, "Count = " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    SovietArtMePage sovietArtMePage = postSnapshot.getValue(SovietArtMePage.class);
//                    App.log(TAG, "poster: " + sovietArtMePage.toString());

                    Poster p = new Poster();
                    p.setAuthor(sovietArtMePage.getAuthor());
                    p.setFilename(sovietArtMePage.getImageFileName());
                    p.setFilepath(sovietArtMePage.getImageUrlInfo());
                    p.setFilepathHighResImg(sovietArtMePage.getHighResImageUrl());
                    p.setTitle(sovietArtMePage.getTitle());
                    p.setYear(sovietArtMePage.getYear());
                    posters.add(p);
                }

                mArtFeedAdapter.setArtWorkCollection(posters);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void getPosterDataViaJSON() { // todo abstract away
        new Thread(() -> {
            ArrayList<Poster> posters = new ArrayList<>();

            String BASE_URL = "http://www.norakomi.com/assets/json";

            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

            // prepare call in Retrofit2
            SovietArtMeService api = retrofit.create(SovietArtMeService.class);

            Call<SovietArtMePosters> call = api.loadPostersData();
            call.enqueue(new Callback<SovietArtMePosters>() {
                @Override
                public void onResponse(Response<SovietArtMePosters> response, Retrofit retrofit) {
//                    App.log(TAG, response.toString());
                    posters.addAll(response.body().posters);
                    mArtFeedAdapter.setArtWorkCollection(posters);
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

}
