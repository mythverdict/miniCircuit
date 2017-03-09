package zju.vlsi.fanwei.component;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by fanwei on 2017/3/1.
 */

public class DrawableComponent extends Component {

    private Drawable drawableComponent;
    private Rect realBounds;

    public DrawableComponent(Drawable drawableComponent) {
        this.drawableComponent = drawableComponent;
        this.matrix = new Matrix();
        realBounds = new Rect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.concat(matrix);
        drawableComponent.setBounds(realBounds);
        drawableComponent.draw(canvas);
        canvas.restore();
    }

    @Override
    public int getWidth() {
        return drawableComponent.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return drawableComponent.getIntrinsicHeight();
    }

    @Override
    public void setDrawable(Drawable drawableComponent) {
        this.drawableComponent = drawableComponent;
    }

    @Override
    public Drawable getDrawable() {
        return drawableComponent;
    }

    @Override
    public void release() {
        super.release();
        if(drawableComponent != null){
            drawableComponent = null;
        }
    }
}
