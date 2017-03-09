package zju.vlsi.fanwei.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fanwei on 2017/3/1.
 */

public class ComponentView extends FrameLayout {
    private enum ActionMode {
        NONE,   //nothing
        DRAG,   //drag the component with your finger
        ZOOM_WITH_TWO_FINGER,   //zoom in or zoom out the component and rotate the component with two finger
        ICON,    //touch in icon
        CLICK    //Click the component
    }

    private static final String TAG = "Component";

    private static final int DEFAULT_MIN_CLICK_DELAY_TIME = 200;
    public static final int FLIP_HORIZONTALLY = 0;
    public static final int FLIP_VERTICALLY = 1;
    public static final int ROTATIONLOCKED = 1;
    public static final int ROTATIONUNLOCKED = 0;

    private  static  final int TYPE_1_PINS_VERTICAL = 1;
    private  static  final int TYPE_1_PINS_HORIZON = 2;
    private  static  final int TYPE_2_PINS_VERTICAL = 3;
    private  static  final int TYPE_2_PINS_HORIZON = 4;
    private static final int TYPE_3_PINS_2_LEFT = 5;
    private static final int TYPE_3_PINS_2_RIGHT = 6;
    private static final int PIN_LENGHT = 72;
    private static final int PIN_CIRCLE_RADIUS = 5;

    private Paint borderPaint;

    private RectF componentRect;
    private Matrix sizeMatrix;
    private Matrix downMatrix;

    private Matrix moveMatrix;

    private BitmapComponentIcon currentIcon;

    private List<BitmapComponentIcon> icons = new ArrayList<>(4);
    //the first point down position
    private float downX;
    private float downY;

    private float oldDistance = 0f;
    private float oldRotation = 0f;
    private int rotationLocked = ROTATIONUNLOCKED;

    private PointF midPoint;

    private ActionMode currentMode = ActionMode.NONE;
    private List<Component> components = new ArrayList<>();

    private Component handlingComponent;

    private boolean locked;
    private boolean constrained;

    private int touchSlop = 3;

    private OnComponentOperationListener onComponentOperationListener;

    private  int typeOfPins;

    private long lastClickTime = 0;
    private int minClickDelayTime = DEFAULT_MIN_CLICK_DELAY_TIME;

    public ComponentView(Context context) {
        this(context, null);
    }

    public ComponentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ComponentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setAlpha(128);
        borderPaint.setStrokeWidth(9);

        sizeMatrix = new Matrix();
        downMatrix = new Matrix();
        moveMatrix = new Matrix();

        componentRect = new RectF();

        configDefaultIcons();
    }

    public void configDefaultIcons() {
        BitmapComponentIcon deleteIcon = new BitmapComponentIcon(
                ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_close_white_18dp),
                BitmapComponentIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());
        BitmapComponentIcon zoomIcon = new BitmapComponentIcon(
                ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_scale_white_18dp),
                BitmapComponentIcon.RIGHT_BOTTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());
        BitmapComponentIcon flipIcon = new BitmapComponentIcon(
                ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_horizontallyflip_white_18dp),
                BitmapComponentIcon.RIGHT_TOP);
        flipIcon.setIconEvent(new FlipHorizontallyEvent());

        icons.clear();
        icons.add(deleteIcon);
        icons.add(zoomIcon);
        icons.add(flipIcon);
    }

    @Override protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            componentRect.left = left;
            componentRect.top = top;
            componentRect.right = right;
            componentRect.bottom = bottom;
        }
    }

    @Override protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawComponents(canvas);
    }

    private void drawComponents(Canvas canvas) {
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            if (component != null) {
                component.draw(canvas);
            }
        }

        if (handlingComponent != null && !locked) {

            float[] bitmapPoints = getComponentPoints(handlingComponent);

            float x1 = bitmapPoints[0];
            float y1 = bitmapPoints[1];
            float x2 = bitmapPoints[2];
            float y2 = bitmapPoints[3];
            float x3 = bitmapPoints[4];
            float y3 = bitmapPoints[5];
            float x4 = bitmapPoints[6];
            float y4 = bitmapPoints[7];
            //画选中框
//            canvas.drawLine(x1, y1, x2, y2, borderPaint);
//            canvas.drawLine(x1, y1, x3, y3, borderPaint);
//            canvas.drawLine(x2, y2, x4, y4, borderPaint);
//            canvas.drawLine(x4, y4, x3, y3, borderPaint);

            //draw pins
            switch (typeOfPins){
                case TYPE_1_PINS_HORIZON:
                    canvas.drawLine(x1+PIN_LENGHT, (y1+y3)/2, x1, (y1+y3)/2, borderPaint);
                    canvas.drawCircle(x1+PIN_LENGHT, (y1+y3)/2, PIN_CIRCLE_RADIUS, borderPaint);
                    break;
                case TYPE_1_PINS_VERTICAL:
                    canvas.drawLine((x1+x2)/2, y1-PIN_LENGHT, (x1+x2)/2, y1, borderPaint);
                    canvas.drawCircle((x1+x2)/2, y1-PIN_LENGHT, PIN_CIRCLE_RADIUS, borderPaint);
                    break;
                case TYPE_2_PINS_HORIZON:
                    canvas.drawLine(x1-PIN_LENGHT, (y1+y3)/2, x1, (y1+y3)/2, borderPaint);
                    canvas.drawLine(x2, (y1+y3)/2, x2+PIN_LENGHT, (y1+y3)/2, borderPaint);
                    canvas.drawCircle(x1-PIN_LENGHT, (y1+y3)/2, PIN_CIRCLE_RADIUS, borderPaint);
                    canvas.drawCircle(x2+PIN_LENGHT, (y1+y3)/2, PIN_CIRCLE_RADIUS, borderPaint);
                    break;
                default:
                    break;
            }
            //draw icons
            float rotation = calculateRotation(x4, y4, x3, y3);
            for (BitmapComponentIcon icon : icons) {
                switch (icon.getPosition()) {
                    case BitmapComponentIcon.LEFT_TOP:
                        configIconMatrix(icon, x1, y1, rotation);
                        break;

                    case BitmapComponentIcon.RIGHT_TOP:
                        configIconMatrix(icon, x2, y2, rotation);
                        break;

                    case BitmapComponentIcon.LEFT_BOTTOM:
                        configIconMatrix(icon, x3, y3, rotation);
                        break;

                    case BitmapComponentIcon.RIGHT_BOTTOM:
                        configIconMatrix(icon, x4, y4, rotation);
                        break;
                }
                //draw the icons' outer circle
                icon.draw(canvas, borderPaint);
            }
        }
    }


    //取消icon显示
    public void releaseIcons() {

    }

    //set coordinate of four icons respectively
    private void configIconMatrix(BitmapComponentIcon icon, float x, float y, float rotation) {
        icon.setX(x);
        icon.setY(y);
        icon.getMatrix().reset();

        icon.getMatrix().postRotate(rotation, icon.getWidth() / 2, icon.getHeight() / 2);
        icon.getMatrix().postTranslate(x - icon.getWidth() / 2, y - icon.getHeight() / 2);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (locked) return super.onInterceptTouchEvent(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();

                return findCurrentIconTouched() != null || findHandlingComponent() != null;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        if (locked) return super.onTouchEvent(event);

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                currentMode = ActionMode.DRAG;

                downX = event.getX();
                downY = event.getY();

                midPoint = calculateMidPoint();
                oldDistance = calculateDistance(midPoint.x, midPoint.y, downX, downY);
                oldRotation = calculateRotation(midPoint.x, midPoint.y, downX, downY);

                currentIcon = findCurrentIconTouched();
                if (currentIcon != null) {
                    currentMode = ActionMode.ICON;
                    currentIcon.onActionDown(this, event);
                } else {
                    handlingComponent = findHandlingComponent();
                }

                if (handlingComponent != null) {
                    downMatrix.set(handlingComponent.getMatrix());
                }

                invalidate();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                oldDistance = calculateDistance(event);
                oldRotation = calculateRotation(event);

                midPoint = calculateMidPoint(event);

                if (handlingComponent != null && isInComponentArea(handlingComponent, event.getX(1),
                        event.getY(1)) && findCurrentIconTouched() == null) {
                    currentMode = ActionMode.ZOOM_WITH_TWO_FINGER;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                handleCurrentMode(event);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                long currentTime = SystemClock.uptimeMillis();

                if (currentMode == ActionMode.ICON && currentIcon != null && handlingComponent != null) {
                    currentIcon.onActionUp(this, event);
                }

                if (currentMode == ActionMode.DRAG
                        && Math.abs(event.getX() - downX) < touchSlop
                        && Math.abs(event.getY() - downY) < touchSlop
                        && handlingComponent != null) {
                    currentMode = ActionMode.CLICK;
                    if (onComponentOperationListener != null) {
                        onComponentOperationListener.onComponentClicked(handlingComponent);
                    }
                    if (currentTime - lastClickTime < minClickDelayTime) {
                        if (onComponentOperationListener != null) {
                            onComponentOperationListener.onComponentDoubleTapped(handlingComponent);
                        }
                    }
                }

                if (currentMode == ActionMode.DRAG && handlingComponent != null) {
                    if (onComponentOperationListener != null) {
                        onComponentOperationListener.onComponentDragFinished(handlingComponent);
                    }
                }

                currentMode = ActionMode.NONE;
                lastClickTime = currentTime;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (currentMode == ActionMode.ZOOM_WITH_TWO_FINGER && handlingComponent != null) {
                    if (onComponentOperationListener != null) {
                        onComponentOperationListener.onComponentZoomFinished(handlingComponent);
                    }
                }
                currentMode = ActionMode.NONE;
                break;
        }//end of switch(action)

        return true;
    }

    private void handleCurrentMode(MotionEvent event) {
        switch (currentMode) {
            case NONE:
                break;
            case DRAG:
                if (handlingComponent != null) {
                    moveMatrix.set(downMatrix);
                    if(Math.abs(event.getX() - downX) >= 50 || Math.abs(event.getY() - downY) >= 50) {
                        moveMatrix.postTranslate(event.getX() - downX, event.getY() - downY);
                        handlingComponent.getMatrix().set(moveMatrix);
                        downMatrix.set(handlingComponent.getMatrix());
                        downX = event.getX();
                        downY = event.getY();
//                        invalidate();
                    }
                    //constrain Component
                    if (constrained) constrainComponent();
                }

                break;
            case ZOOM_WITH_TWO_FINGER:
                //for further use:descend edit
//                if (handlingComponent != null) {
//                    float newDistance = calculateDistance(event);
//                    float newRotation = calculateRotation(event);
//
//                    moveMatrix.set(downMatrix);
//                    moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
//                            midPoint.y);
//                    moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
//                    handlingComponent.getMatrix().set(moveMatrix);
//                }

                break;

            case ICON:
                if (handlingComponent != null && currentIcon != null) {
                    currentIcon.onActionMove(this, event);
                }

                break;
        }// end of switch(currentMode)
    }

    public void zoomAndRotateCurrentComponent(MotionEvent event) {
        zoomAndRotateComponent(handlingComponent, event);
    }

    public void zoomAndRotateComponent(Component component, MotionEvent event) {
        if (component != null) {
//            float newDistance = calculateDistance(midPoint.x, midPoint.y, event.getX(), event.getY());
            float newRotation = calculateRotation(midPoint.x, midPoint.y, event.getX(), event.getY());

            moveMatrix.set(downMatrix);
//            moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
//                    midPoint.y);
            if(newRotation-oldRotation > 45 && rotationLocked == ROTATIONUNLOCKED) {
                moveMatrix.postRotate(90, midPoint.x, midPoint.y);
                rotationLocked = ROTATIONLOCKED;
                handlingComponent.getMatrix().set(moveMatrix);
                return;
            }
            else if(newRotation-oldRotation < -45 && rotationLocked == ROTATIONUNLOCKED) {
                moveMatrix.postRotate(-90, midPoint.x, midPoint.y);
                rotationLocked = ROTATIONLOCKED;
                handlingComponent.getMatrix().set(moveMatrix);
                return;
            }

        }
    }

    private void constrainComponent() {
        float moveX = 0;
        float moveY = 0;
        PointF currentCenterPoint = handlingComponent.getMappedCenterPoint();
        if (currentCenterPoint.x < 0) {
            moveX = -currentCenterPoint.x;
        }

        if (currentCenterPoint.x > getWidth()) {
            moveX = getWidth() - currentCenterPoint.x;
        }

        if (currentCenterPoint.y < 0) {
            moveY = -currentCenterPoint.y;
        }

        if (currentCenterPoint.y > getHeight()) {
            moveY = getHeight() - currentCenterPoint.y;
        }

        handlingComponent.getMatrix().postTranslate(moveX, moveY);
    }

    private BitmapComponentIcon findCurrentIconTouched() {
        for (BitmapComponentIcon icon : icons) {
            float x = icon.getX() - downX;
            float y = icon.getY() - downY;
            float distance_pow_2 = x * x + y * y;
            if (distance_pow_2 <= Math.pow(icon.getIconRadius() + icon.getIconRadius(), 2)) {
                return icon;
            }
        }

        return null;
    }

    /**
     * find the touched ComponentArea
     **/
    private Component findHandlingComponent() {
        for (int i = components.size() - 1; i >= 0; i--) {
            if (isInComponentArea(components.get(i), downX, downY)) {
                return components.get(i);
            }
        }
        return null;
    }

    private boolean isInComponentArea(Component component, float downX, float downY) {
        return component.contains(downX, downY);
    }

    private PointF calculateMidPoint(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) return new PointF();
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(x, y);
    }

    private PointF calculateMidPoint() {
        if (handlingComponent == null) return new PointF();
        return handlingComponent.getMappedCenterPoint();
    }

    /**
     * calculate rotation in line with two fingers and x-axis
     **/
    private float calculateRotation(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) return 0f;
        double x = event.getX(0) - event.getX(1);
        double y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    private float  calculateRotation(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * calculate Distance in two fingers
     **/
    private float calculateDistance(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) return 0f;
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    private float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;

        return (float) Math.sqrt(x * x + y * y);
    }

    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            if (component != null) {
                transformComponent(component);
            }
        }
    }

    /**
     * Component's drawable will be too bigger or smaller
     * This method is to transform it to fit
     * step 1：let the center of the Component image is coincident with the center of the View.
     * step 2：Calculate the zoom and zoom
     **/
    private void transformComponent(Component component) {
        if (component == null) {
            Log.e(TAG, "transformComponent: the bitmapComponent is null or the bitmapComponent bitmap is null");
            return;
        }

        if (sizeMatrix != null) {
            sizeMatrix.reset();
        }

        //step 1
        float offsetX = (getWidth() - component.getWidth()) / 2;
        float offsetY = (getHeight() - component.getHeight()) / 2;

        sizeMatrix.postTranslate(offsetX, offsetY);

        //step 2
        float scaleFactor;
        if (getWidth() < getHeight()) {
            scaleFactor = (float) getWidth() / component.getWidth();
        } else {
            scaleFactor = (float) getHeight() / component.getHeight();
        }

        sizeMatrix.postScale(scaleFactor / 2, scaleFactor / 2, getWidth() / 2, getHeight() / 2);

        component.getMatrix().reset();
        component.getMatrix().set(sizeMatrix);

        invalidate();
    }

    public void flipCurrentComponent(int direction) {
        flip(handlingComponent, direction);
    }

    public void flip(Component component, int direction) {
        if (component != null) {
            if (direction == FLIP_HORIZONTALLY) {
                component.getMatrix().preScale(-1, 1, component.getCenterPoint().x, component.getCenterPoint().y);
                component.setFlippedHorizontally(!component.isFlippedHorizontally);
            } else if (direction == FLIP_VERTICALLY) {
                component.getMatrix().preScale(1, -1, component.getCenterPoint().x, component.getCenterPoint().y);
                component.setFlippedVertically(!component.isFlippedVertically);
            }

            if (onComponentOperationListener != null) {
                onComponentOperationListener.onComponentFlipped(component);
            }

            invalidate();
        }
    }

    public boolean replace(Component component) {
        return replace(component, true);
    }

    public boolean replace(Component component, boolean needStayState) {
        if (handlingComponent != null && component != null) {
            if (needStayState) {
                component.getMatrix().set(handlingComponent.getMatrix());
                component.setFlippedVertically(handlingComponent.isFlippedVertically());
                component.setFlippedHorizontally(handlingComponent.isFlippedHorizontally());
            } else {
                handlingComponent.getMatrix().reset();
                // reset scale, angle, and put it in center
                float offsetX = (getWidth() - handlingComponent.getWidth()) / 2;
                float offsetY = (getHeight() - handlingComponent.getHeight()) / 2;
                component.getMatrix().postTranslate(offsetX, offsetY);

                float scaleFactor;
                if (getWidth() < getHeight()) {
                    scaleFactor = (float) getWidth() / handlingComponent.getDrawable().getIntrinsicWidth();
                } else {
                    scaleFactor = (float) getHeight() / handlingComponent.getDrawable().getIntrinsicHeight();
                }
                component.getMatrix()
                        .postScale(scaleFactor / 2, scaleFactor / 2, getWidth() / 2, getHeight() / 2);
            }
            int index = components.indexOf(handlingComponent);
            components.set(index, component);
            handlingComponent = component;

            invalidate();
            return true;
        } else {
            return false;
        }
    }

    public void rotationUnlocked(Component component){
        if(rotationLocked == ROTATIONLOCKED) {
            rotationLocked = ROTATIONUNLOCKED;
            if(onComponentOperationListener != null){
                onComponentOperationListener.onComponentZoomFinished(component);
            }
        }
    }

    public boolean remove(Component component) {
        if (components.contains(component)) {
            components.remove(component);
            if (onComponentOperationListener != null) {
                onComponentOperationListener.onComponentDeleted(component);
            }
            if (handlingComponent == component) {
                handlingComponent = null;
            }
            invalidate();

            return true;
        } else {
            Log.d(TAG, "remove: the Component is not in this ComponentView");

            return false;
        }
    }

    public boolean removeCurrentComponent() {
        return remove(handlingComponent);
    }

    public void removeAllComponents() {
        components.clear();
        if (handlingComponent != null) {
            handlingComponent.release();
            handlingComponent = null;
        }
        invalidate();
    }

    public void onScrollGridBackground(float offsetX, float offsetY){
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            component.getMatrix().postTranslate(-offsetX, -offsetY);
        }
        invalidate();
    }

    public void addComponent(Component component, float offsetX, float offsetY, int typeOfPins) {
        this.typeOfPins = typeOfPins;
        if (component == null) {
            Log.e(TAG, "Component to be added is null!");
            return;
        }
        float defaultOffsetX = (getWidth() - component.getWidth()) / 2;
        float defaultOffsetY = (getHeight() - component.getHeight()) / 2;
        component.getMatrix().postTranslate(defaultOffsetX + offsetX, defaultOffsetY + offsetY);

        component.getMatrix()
                .postScale(3, 3, getWidth() / 2, getHeight() / 2);

        handlingComponent = component;
        components.add(component);

        invalidate();
    }

    public float[] getComponentPoints(Component component) {
        if (component == null) return new float[8];
        return component.getMappedBoundPoints();
    }

    public void save(File file) {
        ComponentUtils.saveImageToGallery(file, createBitmap());
        ComponentUtils.notifySystemGallery(getContext(), file);
    }

    public Bitmap createBitmap() {
        handlingComponent = null;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    public int getComponentCount() {
        return components.size();
    }

    public boolean isNoneComponent() {
        return getComponentCount() == 0;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        invalidate();
    }

    public void setMinClickDelayTime(int minClickDelayTime) {
        this.minClickDelayTime = minClickDelayTime;
    }

    public int getMinClickDelayTime() {
        return minClickDelayTime;
    }

    public boolean isConstrained() {
        return constrained;
    }

    public void setConstrained(boolean constrained) {
        this.constrained = constrained;
        postInvalidate();
    }

    public void setOnComponentOperationListener(OnComponentOperationListener onComponentOperationListener) {
        this.onComponentOperationListener = onComponentOperationListener;
    }

    public OnComponentOperationListener getOnComponentOperationListener() {
        return onComponentOperationListener;
    }

    public Component getCurrentComponent() {
        return handlingComponent;
    }

    public List<BitmapComponentIcon> getIcons() {
        return icons;
    }

    public void setIcons(List<BitmapComponentIcon> icons) {
        this.icons = icons;
        invalidate();
    }

    public interface OnComponentOperationListener {
        void onComponentClicked(Component component);

        void onComponentDeleted(Component component);

        void onComponentDragFinished(Component component);

        void onComponentZoomFinished(Component component);

        void onComponentFlipped(Component component);

        void onComponentDoubleTapped(Component component);
    }

}
