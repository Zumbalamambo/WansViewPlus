package net.ajcloud.wansviewplus.support.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

public class SizeUtil {

	/**
	 * 获取屏幕尺寸
	 * 
	 * @param context context
	 * @return
	 */
	public static DisplayMetrics getScreenSize(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	/**
	 * @param context used to get system services
	 * @return screenWidth in pixels
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	/**
	 * @param context used to get system services
	 * @return screenHeigth in pixels
	 */
	public static int getScreenHeigth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.heightPixels;
	}


	/**
	 * 获取状态栏高度
	 * 
	 * @return
	 */
	public static int getStatusBarheight(Activity activity) {
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		return statusBarHeight;
	}

	/**
	 * 获取标题栏的高度
	 * 
	 * @param activity
	 * @return
	 */
	public static int getTiltleHeight(Activity activity) {
		int contentTop = activity.getWindow()
				.findViewById(Window.ID_ANDROID_CONTENT).getTop();

		return contentTop - getStatusBarheight(activity);
	}

	public static float dp2px(Context cxt, float dp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, cxt
				.getResources().getDisplayMetrics());
	}

	public static float sp2px(Context cxt, float sp) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, cxt
				.getResources().getDisplayMetrics());
	}
}
