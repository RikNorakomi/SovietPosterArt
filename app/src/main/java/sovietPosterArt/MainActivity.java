package sovietPosterArt;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

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
import sovietPosterArt.utils.App;
import sovietPosterArt.utils.Constants;
import sovietPosterArt.utils.ScreenUtils;

public class MainActivity extends GenericActivity implements
        View.OnClickListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {

    /**
     * MainActivity takes care of displaying the art work overview
     */

    @Bind(R.id.overview_recycler) RecyclerView mRecyclerView;
    @Bind(R.id.fab_menu_with_multiple_actions) FloatingActionsMenu mFabMenu;
    @Bind(R.id.fab_menu_action_search) FloatingActionButton mFabActionSearch;
    @Bind(R.id.fab_menu_action_filter) FloatingActionButton mFabActionFilter;
    @Bind(R.id.search_view) MaterialSearchView mMaterialSearchView;

    private ArtFeedAdapter mArtFeedAdapter;
    private DataManager mDataManager;
    private boolean fabMenuOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // todo: remove fab on scrolling: http://www.materialdoc.com/scrolling-techniques/
        mFabMenu.setOnFloatingActionsMenuUpdateListener(this);
        mFabActionSearch.setOnClickListener(this);
        mFabActionFilter.setOnClickListener(this);
        initSearchView();

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

    private void initSearchView() {
        // documentation: https://github.com/MiguelCatalan/MaterialSearchView
        mMaterialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mMaterialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        mMaterialSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));

        // todo
//        mMaterialSearchView.setVoiceSearch(true); //or false
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

    @Override
    public void onClick(View v) {
        App.log(TAG, "in onClick v=" + v.getId());
        switch (v.getId()) {
            case R.id.fab_menu_action_filter:
                // it was the second button
                App.log(TAG, "fab_menu_action_filter");
                break;
            case R.id.fab_menu_action_search:
                // it was the second button
                mFabMenu.collapse();
                mFabMenu.postDelayed(() -> mMaterialSearchView.showSearch(), 250); // mimic finish animation before showing search view
                App.log(TAG, "fab_menu_action_search");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mMaterialSearchView.isSearchOpen()) {
            mMaterialSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMenuExpanded() {
        App.log(TAG, "in onMenuExpanded");
        fabMenuOpened = true;
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    @Override
    public void onMenuCollapsed() {
        App.log(TAG, "in onMenuCollapsed");
        fabMenuOpened = false;
    }

    public boolean isFabMenuOpened() {
        return fabMenuOpened;
    }
}
