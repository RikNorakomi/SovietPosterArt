package sovietPosterArt.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MEDION on 24-2-2016.
 */
public class ArtistsResponse implements Parcelable{
    protected ArtistsResponse(Parcel in) {
    }

    public static final Creator<ArtistsResponse> CREATOR = new Creator<ArtistsResponse>() {
        @Override
        public ArtistsResponse createFromParcel(Parcel in) {
            return new ArtistsResponse(in);
        }

        @Override
        public ArtistsResponse[] newArray(int size) {
            return new ArtistsResponse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
