package com.ads.unittestableui.app;

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
        subject = new MainActivityImpl (activity);
    }

    @Test
    public void complainsIfNoSensorManager () {
        when (activity.getSystemService (Context.SENSOR_SERVICE)).thenReturn (null);

        try {
            subject.onCreate (null);
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
            subject.onCreate (null);
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

        subject.onCreate (null);

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

        subject.onStart ();

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

        subject.onStart ();

        verify (manager, never ()).registerListener (any (SensorEventListener.class), eq (accelerometer), anyInt ());
        assertEquals (false, subject.inProgress);

        subject.onStop ();

        verify (manager, never ()).unregisterListener (any (SensorEventListener.class), eq (accelerometer));
        assertEquals (false, subject.inProgress);
    }

    @Test
    public void startHandlerEnablesStopDisablesStart () {
        Button otherButton = mock (Button.class);
        MainActivityImpl.StartListener listener = subject.new StartListener (otherButton);
        clickListenerDisablesSelfEnablesOther (listener, otherButton);
    }

    @Test
    public void stopHandlerEnablesStartDisablesStop () {
        Button otherButton = mock (Button.class);
        MainActivityImpl.StopListener listener = subject.new StopListener (otherButton);
        clickListenerDisablesSelfEnablesOther (listener, otherButton);
    }

    @Test
    public void startHandlerSetsInProgress () {
        Button thisButton = mock (Button.class);
        Button otherButton = mock (Button.class);
        MainActivityImpl.StartListener listener = subject.new StartListener (otherButton);
        subject.manager = mock (SensorManager.class);
        subject.inProgress = false;

        listener.onClick (thisButton);

        assertEquals (true, subject.inProgress);
    }

    @Test
    public void stopHandlerClearsInProgress () {
        Button thisButton = mock (Button.class);
        Button otherButton = mock (Button.class);
        MainActivityImpl.StopListener listener = subject.new StopListener (otherButton);
        subject.manager = mock (SensorManager.class);
        subject.inProgress = true;

        listener.onClick (thisButton);

        assertEquals (false, subject.inProgress);
    }

    @Test
    public void startHandlerRegistersController () {
        Button thisButton = mock (Button.class);
        Button otherButton = mock (Button.class);
        MainActivityImpl.StartListener listener = subject.new StartListener (otherButton);
        subject.manager = mock (SensorManager.class);
        subject.accelerometer = mock (Sensor.class);
        subject.controller = null;

        listener.onClick (thisButton);

        verify (subject.manager).registerListener (subject.controller, subject.accelerometer, 100000);
        assertNotNull (subject.controller);
    }

    @Test
    public void stopHandlerUnregistersController () {
        Button thisButton = mock (Button.class);
        Button otherButton = mock (Button.class);
        MainActivityImpl.StopListener listener = subject.new StopListener (otherButton);
        subject.manager = mock (SensorManager.class);
        subject.accelerometer = mock (Sensor.class);
        subject.controller = mock (SquareController.class);

        listener.onClick (thisButton);

        verify (subject.manager).unregisterListener (subject.controller, subject.accelerometer);
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

    private void clickListenerDisablesSelfEnablesOther (View.OnClickListener listener, Button otherButton) {
        Button thisButton = mock (Button.class);
        subject.manager = mock (SensorManager.class);

        listener.onClick (thisButton);

        verify (thisButton).setEnabled (false);
        verify (otherButton).setEnabled (true);
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
