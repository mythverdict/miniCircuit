package zju.vlsi.fanwei.component;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

/**
 * Created by fanwei on 2017/3/1.
 */

public class BitmapComponentIcon extends DrawableComponent implements ComponentIconEvent {
    public static final float DEFAULT_ICON_RADIUS = 30f;
    public static final float DEFAULT_ICON_EXTRA_RADIUS = 10f;

    public static final int LEFT_TOP = 0;
    public static final int RIGHT_TOP = 1;
    public static final int LEFT_BOTTOM = 2;
    public static final int RIGHT_BOTTOM = 3;

    private float iconRadius = DEFAULT_ICON_RADIUS;
    private float iconExtraRadius = DEFAULT_ICON_EXTRA_RADIUS;
    private float x;
    private float y;
    private int position = LEFT_TOP;

    private ComponentIconEvent iconEvent;
    public BitmapComponentIcon(Drawable drawableComponent, int position) {
        super(drawableComponent);
        this.position = position;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle(x, y, iconRadius, paint);
        super.draw(canvas);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getIconRadius() {
        return iconRadius;
    }

    public void setIconRadius(float iconRadius) {
        this.iconRadius = iconRadius;
    }

    public float getIconExtraRadius() {
        return iconExtraRadius;
    }

    public void setIconExtraRadius(float iconExtraRadius) {
        this.iconExtraRadius = iconExtraRadius;
    }

    @Override
    public void onActionDown(ComponentView componentView, MotionEvent event) {
        if (iconEvent != null) iconEvent.onActionDown(componentView, event);
    }

    @Override
    public void onActionMove(ComponentView componentView, MotionEvent event) {
        if (iconEvent != null) iconEvent.onActionMove(componentView, event);
    }

    @Override
    public void onActionUp(ComponentView componentView, MotionEvent event) {
        if (iconEvent != null) iconEvent.onActionUp(componentView, event);
    }

    public ComponentIconEvent getIconEvent() {
        return iconEvent;
    }

    public void setIconEvent(ComponentIconEvent iconEvent) {
        this.iconEvent = iconEvent;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
