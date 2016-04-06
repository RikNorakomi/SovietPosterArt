<<<<<<< HEAD
package sovietPosterArt;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import sovietPosterArt.data.api.sovietPosterArt.SovietArtMeService;
import sovietPosterArt.data.api.sovietPosterArt.model.Poster;
import sovietPosterArt.data.api.sovietPosterArt.model.PosterUrl;
import sovietPosterArt.data.api.sovietPosterArt.model.SovietArtMePostersPages;
import sovietPosterArt.utils.App;
import sovietPosterArt.utils.Constants;

/**
 * Created by norakomi on 3/28/2016.
 */
public class FirebaseManager {

    private Firebase firebaseRef;

    private String childUrlSovietArtMePages = "url_soviet_art_me_pages";
    private String childSovietArtMePages = "soviet_art_me_pages";
    private final String TAG = getClass().getSimpleName();

    public FirebaseManager() {
        firebaseRef = new Firebase(Constants.FIREBASE_URL);
//        attachValueEventListeners();
        addData();

        // test
        firebaseRef.child(childUrlSovietArtMePages).setValue(Constants.URL_SOVIET_ART_ME_POSTER_PAGES);
    }

    private void attachValueEventListeners() {
        firebaseRef.child("https://dazzling-inferno-8912.firebaseio.com/url_soviet_art_me_pages").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                App.log(TAG, snapshot.getValue().toString());  //prints "Do you have data? You'll love Firebase."
            }

            @Override
            public void onCancelled(FirebaseError error) {
                App.log(TAG, "in attachValueEventListeners.onCancelled()");
            }

        });
    }

    public void addData(){
        firebaseRef.child(childSovietArtMePages).setValue("pages urls");

//        getPageUrls();


    }

    public void getPageUrls() { // todo abstract away
        App.log(TAG, "in get page urls");
        new Thread(() -> {
            ArrayList<Poster> posters = new ArrayList<>();

            String BASE_URL = "http://www.norakomi.com/assets/json";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // prepare call in Retrofit 2.0
            SovietArtMeService api = retrofit.create(SovietArtMeService.class);

            Call<SovietArtMePostersPages> call = api.loadPosterPagesUrlList();
            //asynchronous call
            call.enqueue(new Callback<SovietArtMePostersPages>() {
                @Override
                public void onResponse(Response<SovietArtMePostersPages> response, Retrofit retrofit) {
                   // App.log(TAG, "FirebaseManager: onResponse sovietArtMeApi returned postersUrls#: " + response.body().soviet_art_me_pages.size());


                    List<PosterUrl> urls = new ArrayList<>();
                    urls.addAll(response.body().soviet_art_me_pages);

                    firebaseRef.child(childSovietArtMePages).child("pages urls").setValue(urls);

                }

                @Override
                public void onFailure(Throwable t) {
                    App.log(TAG, "in onFailure: " + t.toString());
                }
            });
        }).start();
    }

    public void readData(){

    }


}
=======
package sovietPosterArt;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import sovietPosterArt.data.api.sovietPosterArt.SovietArtMeService;
import sovietPosterArt.data.api.sovietPosterArt.model.Poster;
import sovietPosterArt.data.api.sovietPosterArt.model.PosterUrl;
import sovietPosterArt.data.api.sovietPosterArt.model.SovietArtMePostersPages;
import sovietPosterArt.utils.App;
import sovietPosterArt.utils.Constants;

/**
 * Created by norakomi on 3/28/2016.
 */
public class FirebaseManager {

    private Firebase firebaseRef;

    private String childUrlSovietArtMePages = "url_soviet_art_me_pages";
    private String childSovietArtMePages = "soviet_art_me_pages";
    private final String TAG = getClass().getSimpleName();

    public FirebaseManager() {
        firebaseRef = new Firebase(Constants.FIREBASE_URL);
//        attachValueEventListeners();
        addData();

        // test
        firebaseRef.child(childUrlSovietArtMePages).setValue(Constants.URL_SOVIET_ART_ME_POSTER_PAGES);
    }

    private void attachValueEventListeners() {
        firebaseRef.child("https://dazzling-inferno-8912.firebaseio.com/url_soviet_art_me_pages").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                App.log(TAG, snapshot.getValue().toString());  //prints "Do you have data? You'll love Firebase."
            }

            @Override
            public void onCancelled(FirebaseError error) {
                App.log(TAG, "in attachValueEventListeners.onCancelled()");
            }

        });
    }

    public void addData(){
        firebaseRef.child(childSovietArtMePages).setValue("pages urls");

//        getPageUrls();


    }

    public void getPageUrls() { // todo abstract away
        App.log(TAG, "in get page urls");
        new Thread(() -> {
            ArrayList<Poster> posters = new ArrayList<>();

            String BASE_URL = "http://www.norakomi.com/assets/json";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            // prepare call in Retrofit 2.0
            SovietArtMeService api = retrofit.create(SovietArtMeService.class);

            Call<SovietArtMePostersPages> call = api.loadPosterPagesUrlList();
            //asynchronous call
            call.enqueue(new Callback<SovietArtMePostersPages>() {
                @Override
                public void onResponse(Response<SovietArtMePostersPages> response, Retrofit retrofit) {
                   // App.log(TAG, "FirebaseManager: onResponse sovietArtMeApi returned postersUrls#: " + response.body().soviet_art_me_pages.size());


                    List<PosterUrl> urls = new ArrayList<>();
                    urls.addAll(response.body().soviet_art_me_pages);

                    firebaseRef.child(childSovietArtMePages).child("pages urls").setValue(urls);

                }

                @Override
                public void onFailure(Throwable t) {
                    App.log(TAG, "in onFailure: " + t.toString());
                }
            });
        }).start();
    }

    public void readData(){

    }


}
>>>>>>> f6acfb1f132cf1e625c0873564629688a58a8d5f
