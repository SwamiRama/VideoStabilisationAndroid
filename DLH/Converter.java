package DLH;

import android.graphics.Bitmap;
import android.graphics.Canvas;


import java.io.File;
import java.io.FileOutputStream;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.os.Environment;

public class Converter {
	
	private static final String OUTPUT_DIR = Environment
			.getExternalStorageDirectory().getPath() + "/Video/Output/";

	public static Mat BitmapToMat(Bitmap bitmap) {
		Mat mat = new Mat();
		Utils.bitmapToMat(bitmap, mat);
		return mat;
	}	
	
	public static Bitmap gaussianBlur(Bitmap bitmap) {
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());
		Mat matBitmap = Converter.BitmapToMat(bitmap);
		Mat mGray = new Mat();
		Mat canny = new Mat();
		// Imgproc.cvtColor(mat, mGray, Imgproc.COLOR_RGBA2GRAY);
		// // doing a gaussian blur prevents getting a lot of false hits
		// Imgproc.GaussianBlur(mGray, mGray, new org.opencv.core.Size(5, 5),
		// 0.8485, 0.8485);
		Imgproc.Canny(matBitmap, canny, 80, 90);
		Imgproc.cvtColor(canny, mGray, Imgproc.COLOR_GRAY2BGRA, 4);
		// Values 3 and 4are the LowerThreshold and UpperThreshold.
		// Imgproc.cvtColor(mIntermediateMat,mat, Imgproc.COLOR_GRAY2BGRA, 4);
		Utils.matToBitmap(mGray, newBitmap);
		return newBitmap;
	}

	public static Bitmap scaleBitamp(Bitmap bitmap) {
		float factor = 1.1F;
		final Bitmap saveBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());
		final Canvas c = new Canvas(saveBitmap);
		c.scale(factor, factor, bitmap.getWidth() / 2, bitmap.getHeight() / 2);

		c.drawBitmap(bitmap, 0, 0, null);

		return saveBitmap;
	}

	public static void saveBitmap(Bitmap bitmap, int i) {
		try {

			File file = new File(OUTPUT_DIR + "Sequenz "  + i + ".jpg");
			FileOutputStream save = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, save);
			save.flush();
			save.close();
		} catch (Exception e) {

		}
	}

}
