package sovietPosterArt.data.api.rijksmuseum.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MEDION on 24-2-2016.
 */
public class PaintingsResponse implements Parcelable {
    protected PaintingsResponse(Parcel in) {
    }

    public static final Creator<PaintingsResponse> CREATOR = new Creator<PaintingsResponse>() {
        @Override
        public PaintingsResponse createFromParcel(Parcel in) {
            return new PaintingsResponse(in);
        }

        @Override
        public PaintingsResponse[] newArray(int size) {
            return new PaintingsResponse[size];
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
