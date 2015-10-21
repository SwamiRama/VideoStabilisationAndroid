package videoStabilisation.motionEstimation;

import java.util.ArrayList;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import videoStabilisation.motionEstimation.blockMatching.BlockMatching;
import android.graphics.Bitmap;
import android.graphics.PointF;

public class MotionEstimation {

	private BlockMatching blockMatching;
	private int boxSize;
	private int numberOfFeaturePoints;
	private int method = 2;
	private ParameterExtraction parameterExtraction;
	private double oldValueY = 0;
	private double oldValueX = 0;
	private double maxDistance;
	private double trash = 4;
	private double oldValueRotation = 0;
	private boolean isLocked;
	private double oldValueA11;
	private double oldValueA12;
	private double oldValueA21;
	private double oldValueA22;

	public MotionEstimation(int boxSize, int numberOfFeaturePoints, double maxDistance, boolean isLocked) {
		this.boxSize = boxSize;
		this.numberOfFeaturePoints = numberOfFeaturePoints;
		this.maxDistance = maxDistance;
		this.isLocked = isLocked;
	}

	public void startMotionEstimation(Bitmap src, Bitmap dst) {
		Mat matSrc = new Mat();
		Utils.bitmapToMat(src, matSrc);
		Mat imageGray = new Mat();
		Imgproc.cvtColor(matSrc, imageGray, Imgproc.COLOR_RGB2GRAY);
		MatOfPoint goodFeaturePoints = new MatOfPoint();
		Imgproc.goodFeaturesToTrack(imageGray, goodFeaturePoints, numberOfFeaturePoints, 0.01, 30);
		MatOfPoint2f prevPts = new MatOfPoint2f(goodFeaturePoints.toArray());

		parameterExtraction = new ParameterExtraction(false, boxCalculation(prevPts, src, dst),
				new Point(src.getWidth() / 2, src.getHeight() / 2));
	}

	private Point[][] boxCalculation(MatOfPoint2f prevPts, Bitmap src, Bitmap dst) {
		ArrayList<Point[]> selectedVectors = new ArrayList<Point[]>();
		BlockMatching blockMatching;
		int gap = 50;
		selectedVectors = new ArrayList<Point[]>();
		for (int i = 0; i < prevPts.toList().size(); i++) {
			if (prevPts.toList().get(i).x > gap && prevPts.toList().get(i).x < src.getWidth() - gap
					&& prevPts.toList().get(i).y > gap && prevPts.toList().get(i).y < src.getHeight() - gap) {

				blockMatching = new BlockMatching(src, dst, prevPts.toList().get(i), i);
				selectedVectors.add(blockMatching.getPoints());

			}
		}

		return convertVectorsToArray(selectedVectors);
	}

	private Point[][] convertVectorsToArray(ArrayList<Point[]> sektor) {
		Point[][] vector = new Point[sektor.size()][];
		for (int i = 0; i < vector.length; i++) {
			vector[i] = sektor.get(i);
		}
		return vector;
	}

	public double getTranslationX() {
		if (isLocked) {
			if ((oldValueX + parameterExtraction.TranslationX > maxDistance)
					|| (oldValueX + parameterExtraction.TranslationX < -maxDistance)) {
				return oldValueX;
			} else {
				return oldValueX += trashFilter(parameterExtraction.TranslationX);
			}
		} else {
			return oldValueX += parameterExtraction.TranslationX;
		}
	}

	public double getTranslationY() {
		if (isLocked) {
			if ((oldValueY + parameterExtraction.TranslationY > maxDistance)
					|| (oldValueY + parameterExtraction.TranslationY < -maxDistance)) {
				return oldValueY;
			} else {
				return oldValueY += trashFilter(parameterExtraction.TranslationY);
			}
		} else {
			return oldValueY += parameterExtraction.TranslationY;
		}
	}

	private double trashFilter(double translation) {
		if (translation > 0)
			return (translation > trash ? trash : translation);
		else
			return (translation < -trash ? -trash : translation);
	}

	public double getRotation() {
		return oldValueRotation += parameterExtraction.Rotation;
	}

	public double getScalingX() {
		return parameterExtraction.getScalingX();
	}

	public float getScaling() {
		return (float) parameterExtraction.getScaling();
	}

	public double getSkewing() {
		return parameterExtraction.getSkewing();
	}

	public double getA11() {
		return oldValueA11 += parameterExtraction.A11;
	}

	public double getA12() {
		return oldValueA12 += parameterExtraction.A12;
	}

	public double getA21() {
		return oldValueA21 += parameterExtraction.A21;
	}

	public double getA22() {
		return oldValueA22 += parameterExtraction.A22;
	}
}
