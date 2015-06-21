package dmays.potentialarcher.util;

import android.os.Bundle;
import android.util.Log;

public class BundleUtil {

    public static final String TAG = BundleUtil.class.getSimpleName();

    BundleUtil() {
        // prevent instantiation
    }

    public static void logBundleMap(Bundle bundle) {
        if (null == bundle) {
            Log.i(TAG, "Bundle is null");
            return;
        }

        Log.i(TAG, "Bundle map: " + bundle.toString());
        Log.i(TAG, "Bundle size: " + bundle.size());
        for(String key : bundle.keySet()) {
            Log.i(TAG, key + ": " + bundle.get(key));
        }
    }
}
