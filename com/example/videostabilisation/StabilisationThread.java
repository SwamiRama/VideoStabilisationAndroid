package com.example.videostabilisation;

import java.io.File;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FrameGrabber.Exception;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Environment;

public class StabilisationThread {

	private IplImage currentIplImage;
	private Bitmap bitmap;
	private long lengthInTime;
	private boolean isFinished;
	private double frameRate;
	private int currentFrame;
	private double maxFrame;
	private MediaMetadataRetriever retriever;

	public StabilisationThread(long lengthInTime, double frameRate,
			double maxFrame, File file) {
		this.lengthInTime = lengthInTime;
		this.frameRate = frameRate;
		this.maxFrame = maxFrame;
		this.retriever = new MediaMetadataRetriever();
		this.retriever.setDataSource(file.toString());
		this.currentFrame = 0;
		this.isFinished = false;
	}

	public void next() {
		if (currentFrame < maxFrame) {
			bitmap = retriever.getFrameAtTime(500000);			
		} else {
			isFinished = true;
		}
		currentFrame++;
	}

	public Bitmap getCurrentBitmap() {
		return bitmap;
	}

	public boolean isFin() {
		return isFinished;
	}

}
