package com.aadam.befit.uiModules.LoggedIn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.aadam.befit.R;
import com.aadam.befit.adapter.CustomLeaderboardAdapter;
import com.aadam.befit.animation.ViewAnimation;
import com.aadam.befit.database.RecordsDatabaseAdapter;
import com.aadam.befit.models.LeaderboardItem;

import java.util.ArrayList;

/**
 * Created by aadam on 16/4/2017.
 */

public class LeaderboardFragment extends Fragment {

    //Properties
    public static int maxSteps = 0;
    private CustomLeaderboardAdapter adapter;
    private View view;
    private ListView listView;
    private RecordsDatabaseAdapter recordsDatabaseAdapter;

    public LeaderboardFragment() {

    }

    public static LeaderboardFragment newInstance() {
        return new LeaderboardFragment();
    }

    //region Override Methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        listView = (ListView) view.findViewById(R.id.leaderboard_list);

        //initialize recordsdatabseadapter
        recordsDatabaseAdapter = new RecordsDatabaseAdapter(getContext());
        recordsDatabaseAdapter = recordsDatabaseAdapter.open();

        prepareListView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ViewAnimation.moveViewWithAlpha(this.view, 1f, 350, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recordsDatabaseAdapter.close();
    }
    //endregion

    //region Private Methods
    //populate the listView
    private void prepareListView() {

        ArrayList<LeaderboardItem> dataModels = new ArrayList<>();

        dataModels.addAll(recordsDatabaseAdapter.getLeaderboard());

        //find the maxsteps taken by a user amongst all user
        for (LeaderboardItem rec : dataModels) {
            int steps = Integer.parseInt(rec.getSteps());
            if (steps > maxSteps)
                maxSteps = steps;
        }
        adapter = new CustomLeaderboardAdapter(dataModels, getContext());

        //attach adapter to the listview
        listView.setAdapter(adapter);
    }
    //endregion
}
