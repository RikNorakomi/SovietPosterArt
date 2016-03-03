package sovietPosterArt.data.api.rijksmuseum;

import retrofit2.Retrofit;

/**
 * Created by MEDION on 24-2-2016.
 */
public class BaseDataManager {

    private RijksMuseumService mRijksMuseumApi;

    private void createRijksMuseumApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RijksMuseumService.BASE_URL)
                .build();

        mRijksMuseumApi = retrofit.create(RijksMuseumService.class);
    }
}
