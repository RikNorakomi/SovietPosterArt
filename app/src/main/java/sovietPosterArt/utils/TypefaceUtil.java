package sovietPosterArt.utils;

import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MEDION on 6-2-2016.
 */
public class TypefaceUtil {
    private static final Map<String, Typeface> sTypefaceCache = new HashMap<>();

    public static Typeface getAndCache(String assetPath) {
        synchronized (sTypefaceCache) {
            if (!sTypefaceCache.containsKey(assetPath)) {
                Typeface tf = Typeface.createFromAsset(
                        AppContext.getContext().getAssets(), assetPath);
                sTypefaceCache.put(assetPath, tf);
            }
            return sTypefaceCache.get(assetPath);
        }
    }
}
