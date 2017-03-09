package zju.vlsi.fanwei.component;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

/**
 * Created by fanwei on 2017/3/8.
 */

public class BitmapComponentPins extends DrawableComponent implements ComponentPinsEvent {
    public static float DEFAULT_PIN_RADIUS = 10;
    public static float DEFAULT_PIN_LENGTH = 72;

    public static final int TOP = 1;
    public static final int LEFT_TOP = 2;
    public static final int RIGHT_TOP = 3;
    public static final int LEFT_MID = 4;
    public static final int RIGHT_MID = 5;
    public static final int LEFT_BOTTOM = 6;
    public static final int RIGHT_BOTTOM = 7;
    public static final int BOTTOM = 8;

    private float startPoint;
    private float endPoint;
    private int position = TOP;

    public BitmapComponentPins(Drawable drawableComponent, int position) {
        super(drawableComponent);
        this.position = position;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    public void onActionDown(ComponentView componentView, MotionEvent event) {

    }

    @Override
    public void onActionMove(ComponentView componentView, MotionEvent event) {

    }

    @Override
    public void onActionUp(ComponentView componentView, MotionEvent event) {

    }
}
