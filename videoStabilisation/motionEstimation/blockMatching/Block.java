package videoStabilisation.motionEstimation.blockMatching;

import org.opencv.core.Point;

import android.graphics.Bitmap;


public class Block {
	private int blockWidth;
    private int blockHeight;
    private double center_X;
    private double center_Y;
    private Bitmap bitmap;
    private Point center;

    public Block(int blockWidth, int blockHeight, Point center, Bitmap bitmap)
    {
        this.blockWidth = blockWidth;
        this.blockHeight = blockHeight;
        this.center = center;
        this.center_X = center.x;
        this.center_Y = center.y;
        this.bitmap = bitmap;
    }

    public int getStart_X()
    {
        return (int)center_X - (blockWidth / 2);
    }

    public int getStart_Y()
    {
        return (int)center_Y - (blockHeight / 2);
    }

    public int getEnd_X()
    {
        return (int)center_X + (blockWidth / 2);
    }

    public int getEnd_Y()
    {
        return (int)center_Y + (blockHeight / 2);
    }

    public int getPixel(int x, int y)
    {
        return this.bitmap.getPixel(getStart_X() + x, getStart_Y() + y);
    }

    public int getWidth()
    {
        return blockHeight;
    }

    public int getHeight()
    {
        return blockHeight;
    }

    public Point getPoint()
    {
        return center;
    }
}
