package renzellerodrigueza.com.mysharecam30;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by renzelle rodrigueza on 2017-04-18.
 * Tutorial for this array adapter using view holder
 * http://www.vogella.com/tutorials/AndroidListView/article.html#listfragments
 */

public class ImageAdapter extends ArrayAdapter<DataModel> {

    private Context context;
    private ArrayList<DataModel> dataSet;
    // view lookup cache stored in tag
    private ViewHolder viewHolder;


    private int lastPosition = -1;

    // View lookup cache
    private static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

    // Constructor
    public ImageAdapter(Context context, ArrayList<DataModel> data) {
        super(context, R.layout.row_int, data);
        this.context = context;
        this.dataSet = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final DataModel dataModel = getItem(position);
        View rowView = convertView;
        // Check if an existing view is being reused, otherwise inflate the view
        if (rowView == null) {

            //Initialize ViewHolder
            viewHolder = new ViewHolder();
            // Inflate custom row
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_int, null);
            viewHolder.imageView = (ImageView) rowView.findViewById(R.id.image_view_from_phone);
            viewHolder.textView = (TextView)rowView.findViewById(R.id.tv_fileName);
            rowView.setTag(viewHolder);
            Log.d("Null: ","This is null");
        }
        else
        {
            viewHolder = (ViewHolder) rowView.getTag();
            Log.d("Not Null: ","This is not null");
        }

        // Simple amimation for listview scroll
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        rowView.startAnimation(animation);
        lastPosition = position;

        viewHolder.imageView.setImageBitmap(dataModel.getImage());
        viewHolder.textView.setText(dataModel.getFileName());
        viewHolder.imageView.setTag(position);
        // Return the completed view to render on screen
        return rowView;
    }


}

