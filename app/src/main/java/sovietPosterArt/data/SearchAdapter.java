package sovietPosterArt.data;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import sovietPosterArt.data.api.sovietPosterArt.model.Poster;

/**
 * Created by Rik van Velzen, Norakomi.com, on 26-8-2016.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */
public class SearchAdapter {

    /**
     * Not currently an adapter, but maybe should turn out to be one???
     */

    private static SearchAdapter instance;

    public static SearchAdapter getInstance() {
        return instance == null ? instance = new SearchAdapter() : instance;
    }

    Set<String> authors = new HashSet<>();
    Set<String> titles = new HashSet<>();

    public void setSearchSuggestionData(ArrayList<Poster> posters, MaterialSearchView materialSearchView) {
        // process creating search data of the UI Thread

        new Thread(() -> {
            synchronized (this) {
                for (Poster p : posters) {
                    authors.add(p.getAuthor());
                    titles.add(p.getAuthor());
                }
                setSearchSuggestionsOnSearchView(materialSearchView);
            }
        }).start();
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
