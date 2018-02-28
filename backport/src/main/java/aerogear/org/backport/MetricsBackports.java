package aerogear.org.backport;

import android.content.Context;

/**
 * Exposes native implementations related to metrics that can be used by
 * react-native libraries and Cordova plugins and can't be expressed without
 * falling back to native code.
 */
public class MetricsBackports {
    /**
     * This is more or less just a POC of how it could work. getPackageName does not have to
     * be in this module because if a caller already has a Context they could just get the
     * package name themselves.
     *
     * @param context Android context
     * @return
     */
    public String getPackageName(Context context) {
        return context.getPackageName();
    }
}
