package sovietPosterArt;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import sovietPosterArt.data.DataManager;
import sovietPosterArt.data.SearchAdapter;
import sovietPosterArt.data.api.sovietPosterArt.SovietArtMeService;
import sovietPosterArt.data.api.sovietPosterArt.model.Poster;
import sovietPosterArt.data.api.sovietPosterArt.model.SovietArtMePosters;
import sovietPosterArt.data.firebase.SovietArtMePage;
import sovietPosterArt.sovietPosterArt.R;
import sovietPosterArt.utils.App;
import sovietPosterArt.utils.Constants;
import sovietPosterArt.utils.ScreenUtils;

public class MainActivity extends GenericActivity implements
        View.OnClickListener,
        FloatingActionsMenu.OnFloatingActionsMenuUpdateListener {

    /**
     * MainActivity takes care of displaying the art work overview
     * See also: https://dazzling-inferno-8912.firebaseio.com/posters
     */

//    @Bind(R.id.overview_recycler) RecyclerView mRecyclerView;
//    @Bind(R.id.fab_menu_with_multiple_actions) FloatingActionsMenu mFabMenu;
//    @Bind(R.id.fab_menu_action_search) FloatingActionButton mFabActionSearch;
//    @Bind(R.id.fab_menu_action_filter) FloatingActionButton mFabActionFilter;
//    @Bind(R.id.fab_menu_action_show_all) FloatingActionButton mFabActionShowAll;
//    @Bind(R.id.fab_menu_action_show_random_poster) FloatingActionButton mFabActionShowRandomPoster;
//    @Bind(R.id.search_view) MaterialSearchView mMaterialSearchView;
//    @Bind(R.id.status_bar_underlay) LinearLayout mStatusBarUnderlay;

    RecyclerView mRecyclerView;
    FloatingActionsMenu mFabMenu;
    FloatingActionButton mFabActionSearch;
//    FloatingActionButton mFabActionFilter;
    FloatingActionButton mFabActionShowAll;
    FloatingActionButton mFabActionShowRandomPoster;
    MaterialSearchView mMaterialSearchView;
    LinearLayout mStatusBarUnderlay;

    private ArtFeedAdapter mArtFeedAdapter;
    private DataManager mDataManager;
    private boolean fabMenuOpened = false;
    private long backButtonClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_main);
//        ButterKnife.bind(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.overview_recycler);
        mFabMenu= (FloatingActionsMenu) findViewById(R.id.fab_menu_with_multiple_actions);
        mFabActionSearch = (FloatingActionButton) findViewById(R.id.fab_menu_action_search);
//         mFabActionFilter= (FloatingActionButton) findViewById(R.id.fab_menu_action_filter);;
         mFabActionShowAll= (FloatingActionButton) findViewById(R.id.fab_menu_action_show_all);;
         mFabActionShowRandomPoster= (FloatingActionButton) findViewById(R.id.fab_menu_action_show_random_poster);;
         mMaterialSearchView= (MaterialSearchView) findViewById(R.id.search_view);;
         mStatusBarUnderlay= (LinearLayout) findViewById(R.id.status_bar_underlay);;


        // todo: remove fab on scrolling: http://www.materialdoc.com/scrolling-techniques/
        mFabMenu.setOnFloatingActionsMenuUpdateListener(this);
        mFabActionSearch.setOnClickListener(this);
//        mFabActionFilter.setOnClickListener(this);
        mFabActionShowAll.setOnClickListener(this);
        mFabActionShowRandomPoster.setOnClickListener(this);
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
        mMaterialSearchView.setSubmitOnClick(true);
        mMaterialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                App.log(TAG, "query submitted: " + query);
                if (query.isEmpty()) { // todo query is not executed when query field is empty
                    mMaterialSearchView.setHint("Please enter a search query or close search view!");
                    mMaterialSearchView.setHintTextColor(getResources().getColor(R.color.deepDarkRed));
                } else {
                    handleSearchQuery(query);
                }


                // returning true will leave search components on UI
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                App.log(TAG, "onQueryTextChange");
                return false;
            }
        });

        mMaterialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                mStatusBarUnderlay.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchViewClosed() {
                mStatusBarUnderlay.setVisibility(View.GONE);
            }
        });
        // searchSuggestion are being set in the SearchAdapter

//        mMaterialSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
//        mMaterialSearchView.setBackgroundColor(getResources().getColor(R.color.deepDarkerRed88));

        // todo
//        mMaterialSearchView.setVoiceSearch(true); //or false

        // todo: statusbar underlay height is now set fixed to 25dp in xml;
        // todo: better is to set it manually and get height from system somewhat like code below
//        int height = ScreenUtils.getStatusBarHeightPx();
//
//        // set height of the view under status bar, when search view expands, manually.
//        mStatusBarUnderlay.setLayoutParams(
//                new LinearLayout.LayoutParams(
//                200,
//                height)
//        );
    }

    private void handleSearchQuery(String query) {
        App.log(TAG, "in handleSearchQUery: " + query);
        List<Poster> posters = SearchAdapter.getInstance().doSearchQuery(query);


        if (posters.size() == 1) {
            // Go to detail view to display the poster
            //todo: we're mimicing waiting for keyboard to be removed here, otherwise
            // todo: navigation bar won't be removed in detail screen. But really we should use a listener
            mRecyclerView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startDetailActivity(posters.get(0));
                }
            }, 500);


        } else if (posters.size() > 1) {
            // Set poster on Recycler View
            mArtFeedAdapter.setQueryResult(posters);

        } else if (posters.size() == 0) {
            App.logError(TAG, "search query returned 0 posters!! Should not happen");
        }


    }

    private void startDetailActivity(Poster poster) {
        final Context context = this;
        Intent intent = new Intent(context, ArtWorkDetailViewActivity.class);
        intent.putExtra(Constants.ART_WORK_OBJECT, poster);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
                SearchAdapter.getInstance().setSearchSuggestionData(posters, mMaterialSearchView);

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
//            case R.id.fab_menu_action_filter:
//                // it was the second button
//                App.log(TAG, "fab_menu_action_filter");
//                break;
            case R.id.fab_menu_action_search:
                if (mFabMenu==null){
                    String errorMessage = "mFabMenu == null";
                    App.logError(TAG, errorMessage);
                    return;
                }
                // it was the second button
                mFabMenu.collapse();
                mFabMenu.postDelayed(() -> {
                    mMaterialSearchView.showSearch();

                }, 250); // mimic finish animation before showing search view
                App.log(TAG, "fab_menu_action_search");
                break;
            case R.id.fab_menu_action_show_all:
                if (mFabMenu==null){
                    String errorMessage = "mFabMenu == null";
                    App.logError(TAG, errorMessage);
                    return;
                }

                // re-populate recycler view with full art collection (after search query...)

                mFabMenu.postDelayed(() -> {
                    mFabMenu.collapse();
                    mArtFeedAdapter.clearCollection();
                }, 250); // mimic finish animation before showing clearing
                mFabMenu.postDelayed(() -> {
                    mArtFeedAdapter.resetBackToFullCollection();
                }, 1000);
                App.log(TAG, "resetting back to show full art collection");
                break;
            case R.id.fab_menu_action_show_random_poster:
                if (mFabMenu==null){
                    String errorMessage = "mFabMenu == null";
                    App.logError(TAG, errorMessage);
                    return;
                }
                mFabMenu.postDelayed(() -> {
                    mFabMenu.collapse();
                    Poster poster = mArtFeedAdapter.getRandomPoster();
                    mFabMenu.postDelayed(() -> {
                        startDetailActivity(poster);
                    }, 500); // mimic wait till keyboard collapsed so that navigation bar can be removed in detail screen

                }, 250); // mimic finish animation before showing clearing

                App.log(TAG, "resetting back to show full art collection");
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mMaterialSearchView != null && mMaterialSearchView.isSearchOpen()) {
            App.log(TAG, "onBackPressed: search is open");
            mMaterialSearchView.closeSearch();
        } else if (mFabMenu != null && mFabMenu.isExpanded()) {
            mFabMenu.collapse();
        } else {
            App.log(TAG, "onBackPressed: search is NOT open");

            long timestamp = System.currentTimeMillis();

        /* App exit can only be performed by double clicking on back within specified milliSecs. */
            if (timestamp - backButtonClickTime <= 5000) {
                backButtonClickTime = 0;
                super.onBackPressed();
            } else {
                backButtonClickTime = timestamp;
                Toast.makeText(this, "Press again to leave app", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMenuExpanded() {
        App.log(TAG, "in onMenuExpanded");
        fabMenuOpened = true;

        boolean fullCollectionShown = mArtFeedAdapter.isFullArtWorkCollectionShown();
        findViewById(R.id.fab_menu_action_show_all).setVisibility(fullCollectionShown ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onMenuCollapsed() {
        App.log(TAG, "in onMenuCollapsed");
        fabMenuOpened = false;
    }

    @Override
    protected void onDestroy() {
        App.log(TAG, "in OnDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        App.log(TAG, "in onStop");
//        ButterKnife.unbind(this);
        super.onStop();
    }

    public boolean isFabMenuOpened() {
        return fabMenuOpened;
    }
}
