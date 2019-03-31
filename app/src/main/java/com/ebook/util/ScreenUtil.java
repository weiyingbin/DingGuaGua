package com.ebook.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;


/**
 * 获取屏幕宽度和高度
 */
public class ScreenUtil {
    public static Integer getAndroidScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;
        float density = dm.density;
        int densityDpi = dm.densityDpi;
        return (int) (height / density);
    }

    public static Integer getAndroidScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        float density = dm.density;
        int densityDpi = dm.densityDpi;
        return (int) (width / density);
    }
}
