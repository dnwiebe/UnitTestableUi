package com.demo.unittestableui.app;

import android.hardware.SensorManager;
import android.view.View;
import android.widget.Button;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by dnwiebe on 1/7/16.
 */
public class ListenersTest {

  private MainActivity activity;
  private MainActivityImpl impl;
  private Button thisButton;
  private Button otherButton;

  @Before
  public void setup () {
    activity = mock (MainActivity.class);
    impl = mock (MainActivityImpl.class);
    impl.manager = mock (SensorManager.class);
    thisButton = mock (Button.class);
    otherButton = mock (Button.class);
  }

  @Test
  public void startHandlerEnablesStopDisablesStart () {
    MainActivityImpl.StartListener subject = impl.new StartListener(activity, otherButton);

    clickListenerDisablesSelfEnablesOther (subject, otherButton);
  }

  @Test
  public void stopHandlerEnablesStartDisablesStop () {
    MainActivityImpl.StopListener subject = impl.new StopListener(activity, otherButton);

    clickListenerDisablesSelfEnablesOther (subject, otherButton);
  }

  @Test
  public void startHandlerSetsInProgress () {
    MainActivityImpl.StartListener subject = impl.new StartListener(activity, otherButton);
    impl.inProgress = false;

    subject.onClick(thisButton);

    assertEquals(true, impl.inProgress);
  }

  @Test
  public void stopHandlerClearsInProgress () {
    MainActivityImpl.StopListener subject = impl.new StopListener(activity, otherButton);
    impl.inProgress = true;

    subject.onClick(thisButton);

    assertEquals (false, impl.inProgress);
  }

  private void clickListenerDisablesSelfEnablesOther (View.OnClickListener listener, Button otherButton) {
    Button thisButton = mock (Button.class);

    listener.onClick (thisButton);

    verify (thisButton).setEnabled (false);
    verify (otherButton).setEnabled (true);
  }
}
