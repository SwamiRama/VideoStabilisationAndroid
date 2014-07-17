package videoStabilisation;

import java.io.File;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

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

	public StabilisationThread(long lengthInTime, double frameRate,
			double maxFrame, File file) {
		this.lengthInTime = lengthInTime;
		this.frameRate = frameRate;
		this.maxFrame = maxFrame;
		this.retriever = new MediaMetadataRetriever();
		this.retriever.setDataSource(file.toString());
		this.currentLengthInTime = 0;
		this.isFinished = false;
		this.mFps = (long) ((1.0 / frameRate) * 1000000);
	}

	public void next() {		
		if (currentLengthInTime < lengthInTime) {
			if (currentLengthInTime == 0) {
				src = retriever.getFrameAtTime(currentLengthInTime);
				dst = retriever.getFrameAtTime(currentLengthInTime+mFps);				
			} else {
				src = holder;
				dst = retriever.getFrameAtTime(currentLengthInTime+mFps);
			}
			if (src == null || dst == null) {
				Log.w("WARNING", "dst or src is NULL near Frame = " + currentLengthInTime);
			} else {
				saveBitmap = calculateStabilisation(src, dst);
			}
			
		} else {
			isFinished = true;
		}
		currentLengthInTime += mFps;
	}

	private Bitmap calculateStabilisation(Bitmap src, Bitmap dst) {
		return saveBitmap;
	}

	public Bitmap getCurrentBitmap() {
		return saveBitmap;
	}

	public boolean isFin() {
		return isFinished;
	}

}
