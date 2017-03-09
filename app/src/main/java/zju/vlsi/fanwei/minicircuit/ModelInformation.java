package zju.vlsi.fanwei.minicircuit;

import android.graphics.Bitmap;

/**
 * Created by fanwei on 2017/2/26.
 */

public class ModelInformation {

    private Bitmap modelBitmap;
    private String modelName;

    public ModelInformation(Bitmap modelBitmap, String modelName) {
        super();
        this.modelBitmap = modelBitmap;
        this.modelName = modelName;
    }

    public Bitmap getModelBitmap() {
        return modelBitmap;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelBitmap(Bitmap modleBitmap) {
        this.modelBitmap = modleBitmap;
    }

    public void setModelName(String modleName) {
        this.modelName = modleName;
    }
}
