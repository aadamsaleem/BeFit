package com.aadam.befit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aadam.befit.R;
import com.aadam.befit.database.LoginDatabaseAdapter;
import com.aadam.befit.models.LeaderboardItem;
import com.aadam.befit.uiModules.LoggedIn.LeaderboardFragment;
import com.aadam.befit.util.PrefUtils;

import java.util.ArrayList;

/**
 * Created by aadam on 16/4/2017.
 */

public class CustomLeaderboardAdapter extends ArrayAdapter<LeaderboardItem> {

    //Properties
    Context mContext;
    private ArrayList<LeaderboardItem> dataSet;
    private int lastPosition = -1;
    private LoginDatabaseAdapter loginDatabaseAdapter;

    //region Constructor
    public CustomLeaderboardAdapter(ArrayList<LeaderboardItem> data, Context context) {
        super(context, R.layout.stats_row_item, data);
        this.dataSet = data;
        this.mContext = context;

    }
    //endregion

    //region Override Methods
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LeaderboardItem leaderboardItem = getItem(position);

        CustomLeaderboardAdapter.ViewHolder viewHolder;

        //Initialise loginDatabaseAdapter
        loginDatabaseAdapter = new LoginDatabaseAdapter(getContext());
        loginDatabaseAdapter = loginDatabaseAdapter.open();

        final View result;

        if (convertView == null) {

            viewHolder = new CustomLeaderboardAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.leaderboard_row_item, parent, false);

            viewHolder.rank = (TextView) convertView.findViewById(R.id.leaderboard_rank);
            viewHolder.steps = (TextView) convertView.findViewById(R.id.leaderboard_steps);
            viewHolder.name = (TextView) convertView.findViewById(R.id.leaderboard_name);
            viewHolder.progressbar = (ProgressBar) convertView.findViewById(R.id.progressBar_leaderboard);
            viewHolder.userImageView = (ImageView) convertView.findViewById(R.id.imageView_user);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CustomLeaderboardAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        //Animating each inflated item of listView
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        String username = loginDatabaseAdapter.getUserNameByID(leaderboardItem.getUserID());
        viewHolder.rank.setText("" + (position + 1));
        viewHolder.steps.setText(leaderboardItem.getSteps());
        viewHolder.name.setText(username);
        viewHolder.progressbar.setProgress(getProgress(leaderboardItem.getSteps()));
        if(PrefUtils.getCurrentUser(mContext).getName().equals(username))
            viewHolder.userImageView.setVisibility(View.VISIBLE);
        else viewHolder.userImageView.setVisibility(View.GONE);

        return convertView;
    }
    //endregion

    //region Private Methods
    //Compare the steps percentage wise with the user with max steps
    private int getProgress(String steps) {
        return (int) ((Float.parseFloat(steps) / LeaderboardFragment.maxSteps) * 100);
    }
    //endregion

    //region ViewHolder Class
    private static class ViewHolder {
        TextView rank;
        TextView steps;
        TextView name;
        ProgressBar progressbar;
        ImageView userImageView;
    }
    //endregion
}
