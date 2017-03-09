package zju.vlsi.fanwei.minicircuit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by fanwei on 2017/2/26.
 */

public class ModelSelector extends BaseAdapter {

    private Context context;
    private List<ModelInformation> modelList;

    public ModelSelector(Context context, List<ModelInformation> modelList) {
        super();
        this.context = context;
        this.modelList = modelList;
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    @Override
    public ModelInformation getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the ListView and it Layout
        LayoutInflater inflater = LayoutInflater.from(context);
         convertView = inflater.inflate(R.layout.model_item,null);
        //get the model info at specific position
        ModelInformation modelInformation = modelList.get(position);
        //get the model_item layout and fill them with specific model name and model image
        TextView modelName = (TextView) convertView.findViewById(R.id.model_name);
        ImageView modelImage = (ImageView) convertView.findViewById(R.id.model_image);
        modelName.setText(modelInformation.getModelName());
        modelImage.setImageBitmap(modelInformation.getModelBitmap());
        return convertView;
    }
}
