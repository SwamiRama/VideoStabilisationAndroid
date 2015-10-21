package videoStabilisation.motionEstimation.blockMatching;

import org.opencv.core.Point;

public class SearchArea {
	double searchAreaWidth, searchAreaHeight;
	double featurePoint_X, featurePoint_Y;

	public SearchArea(int searchAreaWidth, int searchAreaHeight, Point featurePoint) {
		this.searchAreaWidth = searchAreaWidth;
		this.searchAreaHeight = searchAreaHeight;
		this.featurePoint_X = featurePoint.x;
		this.featurePoint_Y = featurePoint.y;
	}

	public int getStart_X() {
		return (int) Math.round(featurePoint_X - (searchAreaWidth / 2));
	}

	public int getStart_Y() {
		return (int) Math.round(featurePoint_Y - (searchAreaHeight / 2));
	}

	public int getEnd_X() {
		return (int) Math.round(featurePoint_X + (searchAreaWidth / 2));
	}

	public int getEnd_Y() {
		return (int) Math.round(featurePoint_Y + (searchAreaHeight / 2));
	}
}
