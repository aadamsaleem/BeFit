package com.aadam.befit.uiModules.LoggedIn;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aadam.befit.R;
import com.aadam.befit.animation.ViewAnimation;
import com.aadam.befit.database.RecordsDatabaseAdapter;
import com.aadam.befit.models.Record;
import com.aadam.befit.models.User;
import com.aadam.befit.util.PrefUtils;


public class SessionFragment extends Fragment implements SensorEventListener {

    //Properties
    private Sensor stepSensor;
    private SensorManager sManager;

    private View view;
    private Context context;
    private User user;

    private Handler customHandler = new Handler();

    private RecordsDatabaseAdapter recordsDatabaseAdapter;

    private TextView stepsTextView;
    private TextView timerTextView;
    private TextView distanceTextView;
    private TextView startSession;

    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private long achievementDistance = 1000L;

    private boolean sessionStarted;
    private long steps = 0;
    private long startTime = 0L;

    private String timerString;

    //thread for timer text view
    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerString = "" + mins + ":" + String.format("%02d", secs) + ":" + String.format("%03d", milliseconds);
            timerTextView.setText(timerString);
            customHandler.postDelayed(this, 0);
        }

    };


    public SessionFragment() {

    }

    public static SessionFragment newInstance() {
        SessionFragment fragment = new SessionFragment();
        return fragment;
    }

    //region Override Methods
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_session, container, false);

        context = getContext();

        user = PrefUtils.getCurrentUser(context);
        sessionStarted = false;

        stepsTextView = (TextView) view.findViewById(R.id.textView_steps);
        distanceTextView = (TextView) view.findViewById(R.id.textView_distance);
        timerTextView = (TextView) view.findViewById(R.id.textView_timer);
        startSession = (TextView) view.findViewById(R.id.textView_startSession);

        recordsDatabaseAdapter = new RecordsDatabaseAdapter(context);
        recordsDatabaseAdapter = recordsDatabaseAdapter.open();

        startSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionStarted) {
                    stopSession();
                } else {
                    startSession();
                }

            }
        });

        sManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        ViewAnimation.moveViewWithAlpha(this.view, 1f, 350, null);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;

        //update steps when sensor detects a step is taken
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps++;
            stepsTextView.setText(steps + "");
            distanceTextView.setText(String.format(java.util.Locale.US, "%.2f", calculateDistance(steps)) + " miles");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        recordsDatabaseAdapter.close();
    }
    //endregion

    //region Private methods
    private void startSession() {

        stepsTextView.setText("0");
        distanceTextView.setText("0 miles");
        steps = 0;
        timerTextView.setText("0:00:000");
        updatedTime = timeSwapBuff = timeInMilliseconds = 0L;
        sessionStarted = true;

        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        sManager.registerListener(SessionFragment.this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);

        startSession.setText("STOP SESSION");
    }

    private void stopSession() {
        sessionStarted = false;
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);

        sManager.unregisterListener(SessionFragment.this, stepSensor);

        startSession.setText("START SESSION");

        Record record = new Record();
        record.setUserID(user.getId());
        record.setSessionSteps("" + steps);
        record.setSessionLength(timerString);
        record.setSessionDistance(String.format(java.util.Locale.US, "%.2f", calculateDistance(steps)));

        recordsDatabaseAdapter.insertSession(record);
    }

    private float calculateDistance(long steps) {

        float distanceMiles;
        float multiplier;

        if (user.getGender().equals("MALE")) {
            multiplier = 0.415f;
        } else {
            multiplier = 0.413f;
        }

        //showing alert on achieving milestone (multiple of 1000ft)
        float distanceFeet = multiplier * Float.parseFloat(user.getHeight()) * 0.0328084f * steps;
        if (distanceFeet > achievementDistance) {

            achievementAlert("You ran " + achievementDistance + " feet.");
            achievementDistance += 1000;
        }

        distanceMiles = distanceFeet / (float) 5280;
        return distanceMiles;
    }

    //
    private void achievementAlert(String message) {
        android.app.AlertDialog.Builder bld = new android.app.AlertDialog.Builder(context);
        bld.setTitle("Congratulations!");
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        bld.create().show();
    }
    //endregion
}
