package com.demo.unittestableui.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by dnwiebe on 11/5/15.
 */
public class MainActivityImpl {
    SensorManager manager;
    Sensor accelerometer;
    SquareController controller;
    boolean inProgress;

    public void onCreate (MainActivity activity, Bundle bundle) {
        manager = getSensorManager (activity);
        accelerometer = getAccelerometer (manager);
        activity.setContentView (R.layout.activity_main);
        setButtonListeners (activity);
    }

    public void onStart (MainActivity activity) {
        if (inProgress) {register (activity);}
    }

    public void onStop () {
        if (inProgress) {unregister ();}
    }

    public class StartListener extends ToggleButtonListener {
        public StartListener (MainActivity activity, View stopButton) {super (activity, stopButton);}
        @Override protected void specificOnClick () {register (activity); inProgress = true;}
    }

    public class StopListener extends ToggleButtonListener {
        public StopListener (MainActivity activity, View startButton) {super (activity, startButton);}
        @Override protected void specificOnClick () {unregister (); inProgress = false;}
    }

    private SensorManager getSensorManager (MainActivity activity) {
        SensorManager manager = (SensorManager)activity.getSystemService (Context.SENSOR_SERVICE);
        if (manager == null) {
            throw new IllegalStateException ("This app requires a SensorManager system service");
        }
        return manager;
    }

    private Sensor getAccelerometer (SensorManager manager) {
        Sensor accelerometer = manager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER);
        if (accelerometer == null) {
            throw new IllegalStateException ("This app requires an accelerometer");
        }
        return accelerometer;
    }

    private void setButtonListeners (MainActivity activity) {
        View startButton = activity.findViewById (R.id.start);
        View stopButton = activity.findViewById (R.id.stop);
        startButton.setOnClickListener (new StartListener (activity, stopButton));
        stopButton.setOnClickListener (new StopListener (activity, startButton));
    }

    private void register (MainActivity activity) {
        controller = new SquareController (
                (ImageView)activity.findViewById (R.id.xRadiator),
                (ImageView)activity.findViewById (R.id.yRadiator),
                (ImageView)activity.findViewById (R.id.zRadiator)
        );
        manager.registerListener (controller, accelerometer, 100000);
    }

    private void unregister () {
        manager.unregisterListener (controller, accelerometer);
    }

    private abstract class ToggleButtonListener implements View.OnClickListener {

        protected final MainActivity activity;
        protected final View otherButton;

        protected ToggleButtonListener (MainActivity activity, View otherButton) {
            this.activity = activity;
            this.otherButton = otherButton;
        }

        @Override
        public void onClick (View v) {
            commonOnClick (v);
            specificOnClick ();
        }

        private void commonOnClick (View v) {
            v.setEnabled (false);
            otherButton.setEnabled (true);
        }

        protected abstract void specificOnClick();
    }
}
