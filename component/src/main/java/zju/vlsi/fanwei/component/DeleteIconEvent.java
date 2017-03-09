package zju.vlsi.fanwei.component;

import android.view.MotionEvent;

/**
 * Created by fanwei on 2017/3/1.
 */

public class DeleteIconEvent implements ComponentIconEvent {

    @Override
    public void onActionDown(ComponentView componentView, MotionEvent event) {

    }

    @Override
    public void onActionMove(ComponentView componentView, MotionEvent event) {

    }

    @Override
    public void onActionUp(ComponentView componentView, MotionEvent event) {
        componentView.removeCurrentComponent();
    }
}
