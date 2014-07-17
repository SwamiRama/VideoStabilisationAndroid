package DLH;

import java.nio.ByteBuffer;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_imgproc;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.TextView.BufferType;
import static org.bytedeco.javacpp.opencv_core.*;

import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Converter {

	public static Mat BitmapToMat(Bitmap bitmap) {
		Mat mat = new Mat();
		Utils.bitmapToMat(bitmap, mat);
		return mat;
	}

	public static Bitmap MatToBitmap(Mat mat) {
		Bitmap bitmap = Bitmap.createBitmap(mat.width(), mat.height(),
				Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(mat, bitmap);
		return bitmap;
	}

	public static IplImage BitmapToIplImage(Bitmap bitmap) {
		IplImage image = IplImage.create(bitmap.getWidth(), bitmap.getHeight(),
				IPL_DEPTH_8U, 4);
		IplImage _3image = IplImage.create(bitmap.getWidth(),
				bitmap.getHeight(), IPL_DEPTH_8U, 3);
		IplImage _1image = IplImage.create(bitmap.getWidth(),
				bitmap.getHeight(), IPL_DEPTH_8U, 1);
		bitmap.copyPixelsToBuffer(image.getByteBuffer());
		return image;
	}

	public static Bitmap IplImageToBitmap(IplImage image) {

		IplImage image2 = IplImage.create(image.width(), image.height(),
				IPL_DEPTH_8U, 4);
		opencv_imgproc.cvCvtColor(image, image2, 2); 
		Bitmap bitmap = Bitmap.createBitmap(image2.width(), image2.height(),
				Bitmap.Config.ARGB_8888);
		ByteBuffer im = image2.getByteBuffer();
		im.rewind();
		bitmap.copyPixelsFromBuffer(im);
		return bitmap;
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

}
