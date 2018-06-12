package net.ajcloud.wansviewplus.support.utils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import net.ajcloud.wansviewplus.entity.camera.CameraModel;
import net.ajcloud.wansviewplus.main.application.MainApplication;
import net.ajcloud.wansviewplus.support.utils.Bitmap.BitmapCompress;

import java.io.File;

public class CameraUtil {

	public static boolean isSupportControlDirection(CameraModel capability) {
		if (capability == null) {
			return false;
		}

		int ptz = capability.getFeatures().getPtz();

		if (ptz == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isSupportDuplexVoice(CameraModel capability) {
		if (capability == null) {
			return false;
		}

		int duplexvoice = capability.getFeatures().getDuplexvoice();

		if (duplexvoice == 1) {
			return true;
		} else {
			return false;
		}
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
												int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 :
				(int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 :


				(int) Math.min(Math.floor(w / minSideLength),
						Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) &&
				(minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static int computeSampleSize(BitmapFactory.Options options,
										int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	/**
	 * 加载本地图片
	 * http://bbs.3gstdy.com
	 *
	 * @param filename, cutout
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String filename, boolean cutout) {
		try {
			return new BitmapCompress(filename).compress();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, 800 * 450);
		//这里一定要将其设置回false，因为之前我们将其设置成了true
		opts.inJustDecodeBounds = false;
		try {
			Bitmap bitmap = BitmapFactory.decodeFile(filename, opts);
			if (cutout && bitmap != null) {
				return bitmap;
			} else {
				return BitmapFactory.decodeFile(filename, opts);
			}
		} catch (OutOfMemoryError err) {
			Log.d("tltest", "OutOfMemoryError1:" + err);
		} catch (Exception ex) {

		}
		return null;
	}

	public static void renameImage(String oid) {
		File directory = new File(MainApplication.fileIO.getRealTimePictureDirectory(oid));
		File[] files;
		File file = new File(MainApplication.fileIO.getRealTimePictureDirectory(oid) + "/realtime_picture.jpg");
		if (file.exists()) {
			file.delete();
		}

		files = directory.listFiles();
		if (files.length <= 0) {
			return;
		}

		if (files != null) {
			for (File f : files) {
				if (f.isFile()) { // 判断是否为文件夹
					f.renameTo(new File(MainApplication.fileIO.getRealTimePictureDirectory(oid) + "/realtime_picture.jpg"));
					return;
				}
			}
		}
	}

	public static boolean isSupport1080P(CameraModel capability) {
		if (capability == null) {
			return false;
		}

		String[] resolution = capability.getFeatures().getResolution();

		if (resolution == null) {
			return false;
		}

		for (int i = 0; i < resolution.length; i++) {
			if (resolution[i].equals("1920*1080")) {
				return true;
			}
		}

		return false;
	}

	public static boolean isSupportAutoTrack(CameraModel capability) {
		if (capability == null) {
			return false;
		}

		int autoTrack = capability.getFeatures().getAutotrack();

		if (autoTrack == 1) {
			return true;
		} else {
			return false;
		}
	}

	public static String intToIp(int i) {

		return ((i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF));
	}
}
