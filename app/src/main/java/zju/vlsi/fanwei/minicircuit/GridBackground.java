package zju.vlsi.fanwei.minicircuit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by fanwei on 2017/3/2.
 */

public class GridBackground extends View {
    private Paint gridPaint;

    protected void init() {
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(0xf0282828);
        gridPaint.setStrokeJoin(Paint.Join.ROUND);
        gridPaint.setStrokeCap(Paint.Cap.ROUND);
        gridPaint.setStrokeWidth(3);
    }

    public GridBackground(Context context) {
        super(context);
        init();
    }

    public GridBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GridBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xf0000000);
        final int canvas_Width = 4032;
        final int canvas_Height = 4032;
        final int gridSpace = 72;
        int vertical_coordinate = 0;
        int horizon_coordinate = 0;
        for (int i = 0; i < 57; i++) {
            canvas.drawLine(0, vertical_coordinate, canvas_Width, vertical_coordinate, gridPaint);
            canvas.drawLine(horizon_coordinate, 0, horizon_coordinate, canvas_Height, gridPaint);
            vertical_coordinate += gridSpace;
            horizon_coordinate += gridSpace;
        }
    }
}
