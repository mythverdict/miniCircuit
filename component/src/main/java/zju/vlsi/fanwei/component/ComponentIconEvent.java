package zju.vlsi.fanwei.component;

import android.view.MotionEvent;

/**
 * Created by fanwei on 2017/3/1.
 */

public interface ComponentIconEvent {

    void onActionDown(ComponentView componentView, MotionEvent event);

    void onActionMove(ComponentView componentView, MotionEvent event);

    void onActionUp(ComponentView componentView, MotionEvent event);

}
