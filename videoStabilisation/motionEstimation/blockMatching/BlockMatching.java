package videoStabilisation.motionEstimation.blockMatching;

import org.opencv.core.Point;

import android.graphics.Bitmap;

public class BlockMatching {
	private Point dstPoint;
	private Point srcPoint;

	private int limit = Integer.MAX_VALUE;

	public BlockMatching(Bitmap src, Bitmap dst, Point featurePoint, int boxSize) {
		this.srcPoint = featurePoint;
		MatchingCriteria matchingCriteria = new MatchingCriteria(src, dst);
		Block srcBlock = new Block(boxSize, boxSize, srcPoint, src);
		Block dstBlock;
		double MSE = Double.MAX_VALUE;
		SearchArea searchArea = new SearchArea(boxSize * 2, boxSize * 2,
				srcPoint);
		for (int x = searchArea.getStart_X(); x < searchArea.getEnd_X(); x++) {
			for (int y = searchArea.getStart_Y(); y < searchArea.getEnd_Y(); y++) {
				dstBlock = new Block(boxSize, boxSize, new Point(x, y), dst);
				if (MSE > matchingCriteria.getMSE(srcBlock, dstBlock)) {
					MSE = matchingCriteria.getMSE(srcBlock, dstBlock);
					dstPoint = dstBlock.getPoint();
				}
			}
		}
		getMSE = MSE;
	}

	public Point[] getPoints() {
		Point[] points = new Point[2];
		points[0] = srcPoint;
		points[1] = dstPoint;
		return points;
	}

	public double getMSE;
}
