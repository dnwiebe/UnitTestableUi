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

import java.lang.reflect.Constructor;

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
    public void onCreateInstallsProperOnClickListenersAndClearsProgressFlag () {
        addAccelerometer (activity);
        Button startButton = makeButton (R.id.start);
        Button stopButton = makeButton (R.id.stop);

        subject.onCreate (activity, null);

        assertSame (MainActivityImpl.StartListener.class, getOnClickListener (startButton).getClass ());
        assertSame (MainActivityImpl.StopListener.class, getOnClickListener (stopButton).getClass ());
        assertEquals (false, subject.inProgress);
    }

    @Test
    public void onStartOnStopWhileInProgressScenario () {
        SensorManager manager = mock (SensorManager.class);
        Sensor accelerometer = mock (Sensor.class);
        subject.manager = manager;
        subject.accelerometer = accelerometer;
        subject.inProgress = true;
        ImageView xRadiator = makeRadiator (R.id.xRadiator);
        ImageView yRadiator = makeRadiator (R.id.yRadiator);
        ImageView zRadiator = makeRadiator (R.id.zRadiator);

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

    private Sensor addAccelerometer (MainActivity activity) {
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

    private ImageView makeRadiator (int id) {
        ImageView radiator = mock (ImageView.class);
        when (activity.findViewById (id)).thenReturn (radiator);
        return radiator;
    }

    private Button makeButton (int id) {
        Button button = mock (Button.class);
        when (activity.findViewById (id)).thenReturn (button);
        return button;
    }
}
