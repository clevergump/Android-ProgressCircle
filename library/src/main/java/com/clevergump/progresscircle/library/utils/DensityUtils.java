package com.clevergump.progresscircle.library.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class DensityUtils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取屏幕的宽度（像素值）
     */
    public static int getScreenWidthPixels(Context context) {
        Context applicationContext = context.getApplicationContext();
        WindowManager wm = (WindowManager) applicationContext
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * 获取屏幕的长度/高度（像素值）
     */
    public static int getScreenHeightPixels(Context context) {
        Context applicationContext = context.getApplicationContext();
        WindowManager wm = (WindowManager) applicationContext
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 获取屏幕的尺寸（英寸数）
     *
     * @param activity
     * @return
     */
    public static double getScreenSize(Activity activity) {
        DisplayMetrics dm = getDisplayMetrics(activity);
		/*
		 * The logical density of the display. This is a scaling factor for the
		 * Density Independent Pixel unit, where one DIP is one pixel on an
		 * approximately 160 dpi screen (for example a 240x320, 1.5"x2" screen),
		 * providing the baseline of the system's display. Thus on a 160dpi
		 * screen this density value will be 1; on a 120 dpi screen it would be
		 * .75; etc.
		 *
		 * <p>This value does not exactly follow the real screen size (as given
		 * by {@link #xdpi} and {@link #ydpi}, but rather is used to scale the
		 * size of the overall UI in steps based on gross changes in the display
		 * dpi. For example, a 240x320 screen will have a density of 1 even if
		 * its width is 1.8", 1.3", etc. However, if the screen resolution is
		 * increased to 320x480 but the screen size remained 1.5"x2" then the
		 * density would be increased (probably to 1.5).
		 *
		 * @see #DENSITY_DEFAULT
		 */
        float density = dm.density;
        // 屏幕宽度的像素值
        int widthPixels = dm.widthPixels;
        // 屏幕长度/高度的像素值
        int heightPixels = dm.heightPixels;
        // 屏幕对角线的像素值
        double diagonalPixels = Math.sqrt(Math.pow(widthPixels, 2)
                + Math.pow(heightPixels, 2));
        dm = null;
        return diagonalPixels / (160 * density);
    }

    public static float getDensity(Activity activity) {
        DisplayMetrics dm = getDisplayMetrics(activity);
		/*
		 * The logical density of the display. This is a scaling factor for the
		 * Density Independent Pixel unit, where one DIP is one pixel on an
		 * approximately 160 dpi screen (for example a 240x320, 1.5"x2" screen),
		 * providing the baseline of the system's display. Thus on a 160dpi
		 * screen this density value will be 1; on a 120 dpi screen it would be
		 * .75; etc.
		 *
		 * <p>This value does not exactly follow the real screen size (as given
		 * by {@link #xdpi} and {@link #ydpi}, but rather is used to scale the
		 * size of the overall UI in steps based on gross changes in the display
		 * dpi. For example, a 240x320 screen will have a density of 1 even if
		 * its width is 1.8", 1.3", etc. However, if the screen resolution is
		 * increased to 320x480 but the screen size remained 1.5"x2" then the
		 * density would be increased (probably to 1.5).
		 *
		 * @see #DENSITY_DEFAULT
		 */
        float density = dm.density;
        dm = null;
        return density;
    }

    private static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }
}