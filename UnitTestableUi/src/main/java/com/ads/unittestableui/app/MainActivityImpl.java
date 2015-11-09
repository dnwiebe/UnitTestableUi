package com.ads.unittestableui.app;

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
    private MainActivity activity;
    SensorManager manager;
    Sensor accelerometer;
    SquareController controller;
    boolean inProgress;

    public MainActivityImpl (MainActivity activity) {
        this.activity = activity;
    }

    public void onCreate (Bundle bundle) {
        manager = getSensorManager ();
        accelerometer = getAccelerometer (manager);
        activity.setContentView (R.layout.activity_main);
        setButtonListeners ();
    }

    public void onStart () {
        if (inProgress) {
            register ();
        }
    }

    public void onStop () {
        if (inProgress) {
            unregister ();
        }
    }

    public class StartListener extends ToggleButtonListener {
        public StartListener (View stopButton) {super (stopButton);}
        @Override protected void guts () {register (); inProgress = true;}
    }

    public class StopListener extends ToggleButtonListener {
        public StopListener (View startButton) {super (startButton);}
        @Override protected void guts () {unregister (); inProgress = false;}
    }

    private SensorManager getSensorManager () {
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

    private void setButtonListeners () {
        View startButton = activity.findViewById (R.id.start);
        View stopButton = activity.findViewById (R.id.stop);
        startButton.setOnClickListener (new StartListener (stopButton));
        stopButton.setOnClickListener (new StopListener (startButton));
    }

    private void register () {
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

        protected final View otherButton;

        protected ToggleButtonListener (View otherButton) {
            this.otherButton = otherButton;
        }

        @Override
        public void onClick (View v) {
            v.setEnabled (false);
            otherButton.setEnabled (true);
            guts ();
        }

        protected abstract void guts ();
    }
}
