package videoStabilisation.motionCompensation;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class MotionCompensation {
	private Bitmap saveBitmap;

	
	public void bitmapTransformation(double x_translation,
			double y_translation, double rotation, Bitmap bitmap) {
		final Bitmap saveBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), bitmap.getConfig());
		final Canvas c = new Canvas(saveBitmap);
		c.translate((float) x_translation, (float) y_translation);
		c.rotate((float) rotation, bitmap.getWidth() / 2,
				bitmap.getHeight() / 2);

		c.drawBitmap(bitmap, 0, 0, null);

		
	}

	public Bitmap getBitmap() {
		return saveBitmap;
	}
}
