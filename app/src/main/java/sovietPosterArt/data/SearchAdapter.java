package sovietPosterArt.data;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sovietPosterArt.data.api.sovietPosterArt.model.Poster;
import sovietPosterArt.utils.App;

/**
 * Created by Rik van Velzen, Norakomi.com, on 26-8-2016.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */
public class SearchAdapter {

    private final String TAG = getClass().getSimpleName();

    /**
     * Not currently an adapter, but maybe should turn out to be one???
     */

    private static SearchAdapter instance;
    private ArrayList<Poster> posters;

    public static SearchAdapter getInstance() {
        return instance == null ? instance = new SearchAdapter() : instance;
    }

    Set<String> authors = new HashSet<>();
    Set<String> titles = new HashSet<>();

    HashMap<String, Set> SearchCategoriesMap =
            new HashMap<String, Set>() {
                {
                    put("AUTHORS", authors);
                    put("TITLES", titles);
                }
            };

    public void setSearchSuggestionData(ArrayList<Poster> posters, MaterialSearchView materialSearchView) {
        // process creating search data of the UI Thread
        this.posters = posters;
        new Thread(() -> {
            synchronized (this) {
                for (Poster p : posters) {
                    authors.add(p.getAuthor());
                    titles.add(p.getTitle());
                }
                setSearchSuggestionsOnSearchView(materialSearchView);
            }
        }).start();
    }

    public List<Poster> doSearchQuery(String query) {
        boolean titleFound = false;
        boolean authorFound = false;
        List<Poster> posterFound = new ArrayList<>();

        for (Poster p : posters) {
            if (p.getTitle().equals(query)) {
                titleFound = true;
                posterFound.add(p);
                break;
            } else if (p.getAuthor().equals(query)) {
                authorFound = true;
                posterFound.addAll(getAllPostersFromAuthor(query));
                break;
            }
        }

        if (posterFound.isEmpty()) {
            App.logError(TAG, "Posters found is empty. Should be possible!");
        } else {
            App.log(TAG, "Found poster#: " + posterFound.size());
        }

        // todo: add flag/indicator to inform callee what type of category has been found (e.g. autho, title, etc.)
        return posterFound;
    }

    private List<Poster> getAllPostersFromAuthor(String author) {
        App.log(TAG, "Trying to get all posters from author: " + author);
        List<Poster> posterFromAuthor = new ArrayList<>();
        for (Poster p : posters) {
            if (p.getAuthor().equals(author)) {
                posterFromAuthor.add(p);
            }
        }


        return posterFromAuthor;
    }

    private void setSearchSuggestionsOnSearchView(MaterialSearchView materialSearchView) {
        // combine all sets
        Set<String> suggestionsSet = new HashSet<>(authors);
        suggestionsSet.addAll(titles);

        String[] suggestion = suggestionsSet.toArray(new String[suggestionsSet.size()]);

        // cannot set suggestion on ui widget from background thread so post on UI threada
        materialSearchView.post(() -> materialSearchView.setSuggestions(suggestion));
    }
}
