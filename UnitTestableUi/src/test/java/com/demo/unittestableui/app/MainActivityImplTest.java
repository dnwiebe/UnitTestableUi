package com.demo.unittestableui.app;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by dnwiebe on 11/5/15.
 */
public class MainActivityImplTest {

    private MainActivity activity;
    private MainActivityImpl subject;

    @Before
    public void setup () {
        activity = mock (MainActivity.class);
        subject = new MainActivityImpl ();
    }

    @Test
    public void onCreateInitializesSensorAndView () {
        Sensor accelerometer = makeAndInjectAccelerometer (activity);
        makeAndInjectButton (R.id.start);
        makeAndInjectButton (R.id.stop);

        subject.onCreate (activity, null);

        assertSame (subject.accelerometer, accelerometer);
        verify (activity).setContentView (R.layout.activity_main);
    }

    @Test
    public void onCreateInstallsProperOnClickListenersAndClearsProgressFlag () {
        makeAndInjectAccelerometer (activity);
        Button startButton = makeAndInjectButton (R.id.start);
        Button stopButton = makeAndInjectButton (R.id.stop);

        subject.onCreate (activity, null);

        MainActivityImpl.StartListener startListener =
                (MainActivityImpl.StartListener)getOnClickListener (startButton);
        checkSelfOther (startListener, startButton, stopButton);
        MainActivityImpl.StopListener stopListener =
                (MainActivityImpl.StopListener)getOnClickListener (stopButton);
        checkSelfOther (stopListener, stopButton, startButton);
        assertEquals (false, subject.inProgress);
    }

    @Test
    public void complainsIfNoSensorManager () {
        when (activity.getSystemService (Context.SENSOR_SERVICE)).thenReturn (null);

        try {
            subject.onCreate (activity, null);
            fail ();
        }
        catch (IllegalStateException e) {
            assertEquals ("This app requires a SensorManager system service", e.getMessage ());
        }
    }

    @Test
    public void complainsIfNoAccelerometer () {
        SensorManager sensorManager = mock (SensorManager.class);
        when (sensorManager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER)).thenReturn (null);
        when (activity.getSystemService (Context.SENSOR_SERVICE)).thenReturn (sensorManager);

        try {
            subject.onCreate (activity, null);
            fail ();
        }
        catch (IllegalStateException e) {
            assertEquals ("This app requires an accelerometer", e.getMessage ());
        }
    }

    @Test
    public void onStartOnStopWhileInProgressScenario () {
        SensorManager manager = mock (SensorManager.class);
        Sensor accelerometer = mock (Sensor.class);
        subject.manager = manager;
        subject.accelerometer = accelerometer;
        subject.inProgress = true;
        ImageView xRadiator = makeAndInjectRadiator (R.id.xRadiator);
        ImageView yRadiator = makeAndInjectRadiator (R.id.yRadiator);
        ImageView zRadiator = makeAndInjectRadiator (R.id.zRadiator);

        subject.onStart (activity);

        ArgumentCaptor<SensorEventListener> captor = ArgumentCaptor.forClass (SensorEventListener.class);
        verify (manager).registerListener (captor.capture (), eq (accelerometer), eq (100000));
        SquareController controller = (SquareController)captor.getValue ();
        assertSame (xRadiator, controller.xRadiator);
        assertSame (yRadiator, controller.yRadiator);
        assertSame (zRadiator, controller.zRadiator);
        assertEquals (true, subject.inProgress);

        subject.onStop ();

        verify (manager).unregisterListener (controller, accelerometer);
        assertEquals (true, subject.inProgress);
    }

    @Test
    public void onStartOnStopWhileNotInProgressScenario () {
        SensorManager manager = mock (SensorManager.class);
        Sensor accelerometer = mock (Sensor.class);
        subject.manager = manager;
        subject.accelerometer = accelerometer;
        subject.inProgress = false;

        subject.onStart (activity);

        verify (manager, never ()).registerListener (any (SensorEventListener.class), eq (accelerometer), anyInt ());
        assertEquals (false, subject.inProgress);

        subject.onStop ();

        verify (manager, never ()).unregisterListener (any (SensorEventListener.class), eq (accelerometer));
        assertEquals (false, subject.inProgress);
    }

    private Sensor makeAndInjectAccelerometer (MainActivity activity) {
        SensorManager sensorManager = mock (SensorManager.class);
        Sensor sensor = mock (Sensor.class);
        when (sensorManager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER)).thenReturn (sensor);
        when (activity.getSystemService (Context.SENSOR_SERVICE)).thenReturn (sensorManager);
        return sensor;
    }

    private View.OnClickListener getOnClickListener (Button button) {
        ArgumentCaptor<View.OnClickListener> captor = ArgumentCaptor.forClass (View.OnClickListener.class);
        verify (button).setOnClickListener (captor.capture ());
        return captor.getValue ();
    }

    private ImageView makeAndInjectRadiator (int id) {
        ImageView radiator = mock (ImageView.class);
        when (activity.findViewById (id)).thenReturn (radiator);
        return radiator;
    }

    private Button makeAndInjectButton (int id) {
        Button button = mock (Button.class);
        when (activity.findViewById (id)).thenReturn (button);
        return button;
    }

    private void checkSelfOther (View.OnClickListener listener,
                                 Button selfButton, Button otherButton) {
        listener.onClick (selfButton);
        verify (selfButton).setEnabled (false);
        verify (otherButton).setEnabled (true);
    }
}
