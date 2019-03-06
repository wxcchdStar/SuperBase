package wxc.android.base.utils;

import android.app.Activity;
import android.view.View;

public class V {

    public static <T extends View> T f(Activity activity, int viewId) {
        return activity.findViewById(viewId);
    }

    public static <T extends View> T f(View view, int viewId) {
        return view.findViewById(viewId);
    }
}
