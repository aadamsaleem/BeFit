package com.aadam.befit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aadam.befit.R;
import com.aadam.befit.models.Record;

import java.util.ArrayList;

public class CustomRecordListAdapter extends ArrayAdapter<Record> {

    //Properties
    Context mContext;
    private ArrayList<Record> dataSet;
    private int lastPosition = -1;

    //region Constructor
    public CustomRecordListAdapter(ArrayList<Record> data, Context context) {
        super(context, R.layout.stats_row_item, data);
        this.dataSet = data;
        this.mContext = context;

    }
    //endregion

    //region Override Methods
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Record record = getItem(position);

        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.stats_row_item, parent, false);
            viewHolder.length = (TextView) convertView.findViewById(R.id.length);
            viewHolder.steps = (TextView) convertView.findViewById(R.id.steps);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.time = (TextView) convertView.findViewById(R.id.time);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        //Animating each inflated item of listView
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.length.setText("Duration: " + record.getSessionLength());
        viewHolder.steps.setText("Steps: " + record.getSessionSteps());
        viewHolder.distance.setText("Distance: " + record.getSessionDistance() + " miles");
        viewHolder.time.setText(record.getSessionDate().toString());
        return convertView;
    }
    //endregion

    //region ViewHolder class
    private static class ViewHolder {
        TextView length;
        TextView steps;
        TextView distance;
        TextView time;
    }
    //endregion
}