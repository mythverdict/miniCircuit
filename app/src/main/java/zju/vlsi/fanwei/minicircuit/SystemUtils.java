package zju.vlsi.fanwei.minicircuit;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by fanwei on 2017/2/24.
 */

public class SystemUtils{

    public SystemInfo getScreenSize(Activity activity){
        SystemInfo info = new SystemInfo();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        info.screenWidth = metrics.widthPixels;
        info.screenHeight = metrics.heightPixels;
        return info;
    }

}
