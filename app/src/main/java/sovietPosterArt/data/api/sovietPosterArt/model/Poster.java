package sovietPosterArt.data.api.sovietPosterArt.model;

import java.io.Serializable;

import sovietPosterArt.utils.App;

/**
 * Created by MEDION on 3-3-2016.
 */
public class Poster implements Serializable {

    private final String TAG = getClass().getSimpleName();
    private static final String URL_BASE_PATH = "http://sovietart.me"; // todo: move; should not be here!

    private String title;
    private String author;
    private String imageFilepath;
    private String highResImageFilepath;
    private String filename;
    //    private String category;
    private String year;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFilepath() {
        return imageFilepath;
    }

    /**
     * Returns url for high res or normal res image depending on device
     * being a tablet or phone respectively
     */
    public String getImageUrl() {
        return URL_BASE_PATH + getFilepath();
    }

    public String getThumbnailUrl() {
        return URL_BASE_PATH + "/img/posters/190px/" + filename;

    }

    public String getHighResImageUrl() {
        // todo refactor: filepathHighRes is file url; normal res is basepath + filepath combined
        String highResUrl = getFilepathHighResImg();
        if (highResUrl == null || highResUrl.isEmpty()){
            return getImageUrl();
        }
        return getFilepathHighResImg();
    }

    /**
     * For low end devices we could get the 190px thumb
     */
    public String getPosterThumbUrl() {
        String thumbUrl = URL_BASE_PATH + "/img/posters/190px/" + filename;
        App.log(TAG, "url for thumb: " + thumbUrl);
        return thumbUrl;
    }

    public void setFilepath(String filepath) {
        if (filepath == null) {
            filepath = "";
        }
        this.imageFilepath = filepath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setFilepathHighResImg(String filepathHighResImg) {
        highResImageFilepath = filepathHighResImg;
    }

    public String getFilepathHighResImg() {
        return highResImageFilepath;
    }
}
