package videoStabilisation;

import java.io.File;

import videoStabilisation.motionCompensation.MotionCompensation;
import videoStabilisation.motionEstimation.MotionEstimation;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

@SuppressLint("NewApi")
public class StabilisationThread {

	private Bitmap saveBitmap;
	private long lengthInTime;
	private boolean isFinished;
	private double frameRate;
	private long currentLengthInTime;
	private double maxFrame;
	private MediaMetadataRetriever retriever;
	private long mFps;
	private Bitmap src;
	private Bitmap dst;
	private Bitmap holder;
	private int frameNumber;
	private MotionEstimation motionEstimation;
	private double TranslationX;
	private double TranslationY;
	private double Rotation;
	private MotionCompensation motionCompensation;

	@SuppressLint("InlinedApi")
	public StabilisationThread(long lengthInTime, double frameRate,
			double maxFrame, File file) {
		this.lengthInTime = lengthInTime;
		this.frameRate = frameRate;
		this.maxFrame = maxFrame;
		this.retriever = new MediaMetadataRetriever();
		this.retriever.setDataSource(file.toString());
		this.currentLengthInTime = 0;
		this.isFinished = false;
		this.frameNumber = 0;
		this.motionEstimation = new MotionEstimation(16, 50, 30, true);
		this.motionCompensation = new MotionCompensation();
	}

	public void next() {
		if (currentLengthInTime < lengthInTime) {
			if (currentLengthInTime == 0) {
				src = retriever.getFrameAtTime(currentLengthInTime);
				dst = retriever.getFrameAtTime(currentLengthInTime + mFps);
			} else {
				src = holder;
				dst = retriever.getFrameAtTime(currentLengthInTime + mFps);
			}
			if (src == null || dst == null) {
				Log.w("WARNING", "dst or src is NULL near Frame = "
						+ currentLengthInTime);
			} else {
				saveBitmap = calculateStabilisation(src, dst);
				DLH.Converter.saveBitmap(saveBitmap, frameNumber);
			}
		} else {
			isFinished = true;
		}
		holder = dst;
		currentLengthInTime += mFps;
		frameNumber++;
		Log.i("Progress", "Progress =" + frameNumber + " of " + maxFrame);
	}

	private Bitmap calculateStabilisation(Bitmap src, Bitmap dst) {
		motionEstimation.startMotionEstimation(src, dst);

		TranslationX = motionEstimation.getTranslationX();
		TranslationY = motionEstimation.getTranslationY();
		Rotation = motionEstimation.getRotation();
//		xValues.Enqueue(TranslationX);
//		yValues.Enqueue(TranslationY);
//		rotationValue.Enqueue(Rotation);
		// zoomFaktor = motionEstimation.getScaling();
		motionCompensation.bitmapTransformation(TranslationX, TranslationY, Rotation, dst);

		// motionCompensation.startMotionCompensation(0, 0, 45, 0, 0, 0,
		// dstBitmap);
		// image =
		// UnmanagedImage.FromManagedImage(motionCompensation.saveBitmap);
		// for (int i = 0; i < motionEstimation.vectorsPoints.Length; i++)
		// {
		// Drawing.Line(image, motionEstimation.vectorsPoints[i][0],
		// motionEstimation.vectorsPoints[i][1], Color.Red);
		// }
		// return image.ToManagedImage();
		return motionCompensation.getBitmap();
	}

	public Bitmap getCurrentBitmap() {
		return saveBitmap;
	}

	public boolean isFin() {
		return isFinished;
	}

}
