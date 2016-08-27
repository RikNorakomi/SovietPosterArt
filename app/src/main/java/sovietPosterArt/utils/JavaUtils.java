package sovietPosterArt.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Rik van Velzen, Norakomi.com, on 27-8-2016.
 * <p>
 * Check out:
 * https://github.com/RikNorakomi?tab=repositories
 * http://norakomi.blogspot.nl/
 * www.norakomi.com
 */
public class JavaUtils {

    public static ArrayList randomizeArrayList(ArrayList collection){
        long seed = System.nanoTime();
        Collections.shuffle(collection, new Random(seed));

        return collection;
    }
}
