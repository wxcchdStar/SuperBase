package wxc.android.base.utils;

import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.TypedValue;

public class ValueUtils {

    public static int dpToPx(Context context, float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics()));
    }

    public static int getColor(Context context, int resId) {
        return ContextCompat.getColor(context, resId);
    }

}
