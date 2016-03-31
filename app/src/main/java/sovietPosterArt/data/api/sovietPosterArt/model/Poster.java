package sovietPosterArt.data.api.sovietPosterArt.model;

import java.io.Serializable;

/**
 * Created by MEDION on 3-3-2016.
 */
public class Poster implements Serializable {

    private final String TAG = getClass().getSimpleName();
    private static final String URL_BASE_PATH = "http://sovietart.me";

    private String title;
    private String author;
    private String image_filepath;
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
        return image_filepath;
    }

    public String getImageUrl() {
        String imageUrl = URL_BASE_PATH + getFilepath();
        return imageUrl;
    }

    public void setFilepath(String filepath) {
        this.image_filepath = filepath;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

//    public String getCategory() {
//        return category;
//    }
//
//    public void setCategory(String category) {
//        this.category = category;
//    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

//    @Override
//    public String toString() {
//        return "Poster{" +
//                "title='" + title + '\'' +
//                ", author='" + author + '\'' +
//                ", filepath='" + image_filepath + '\n' +
//                ", filename='" + filename + '\n' +
//                ", category='" + category + '\n' +
//                ", year='" + year + '\n' +
//                '}' + '\n';
//    }
}
