package videoStabilisation.motionEstimation.blockMatching;

import android.graphics.Bitmap;

public class MatchingCriteria {
	Bitmap src, dst;

	public MatchingCriteria(Bitmap src, Bitmap dst) {
		this.src = src;
		this.dst = dst;
	}

	public double getMSE(Block srcBlock, Block dstBlock) {
		double MSE = 0;
		int x_width = srcBlock.getWidth();
		int y_height = srcBlock.getHeight();
		double N = x_width * y_height;
		for (int x = 0; x < x_width; x++) {
			for (int y = 0; y < y_height; y++) {
				MSE += Math.pow(srcBlock.getPixel(x, y) - dstBlock.getPixel(x, y), 2);
			}
		}
		return (MSE / N);
	}

	public double getMSE(int border) {
		double MSE = 0;
		for (int x = border; x < src.getWidth() - border; x++) {
			for (int y = border; y < src.getHeight() - border; y++) {
				MSE += Math.pow(src.getPixel(x, y) - dst.getPixel(x, y), 2);
			}
		}
		return (MSE) / (src.getHeight() * src.getWidth());
	}
}
