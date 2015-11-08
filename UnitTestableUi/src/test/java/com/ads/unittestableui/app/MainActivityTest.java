package com.ads.unittestableui.app;

import android.os.Bundle;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Created by dnwiebe on 11/5/15.
 */
public class MainActivityTest {

    private MainActivity subject;
    private MainActivityImpl impl;
    private Bundle bundle;

    @Before
    public void setup () {
        subject = new MainActivity ();
        impl = mock (MainActivityImpl.class);
        subject.impl = impl;
        subject.closeValve ();
        bundle = new Bundle ();
    }

    @Test
    public void delegatesOnCreateToImpl () {
        subject.onCreate (bundle);

        verify (impl).onCreate (bundle);
    }

    @Test
    public void delegatesOnStartToImpl () {
        subject.onStart ();

        verify (impl).onStart ();
    }

    @Test
    public void delegatesOnStopToImpl () {
        subject.onStop ();

        verify (impl).onStop ();
    }
}
