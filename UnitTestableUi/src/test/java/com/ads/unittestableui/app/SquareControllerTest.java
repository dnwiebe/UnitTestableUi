package com.ads.unittestableui.app;

import android.graphics.Bitmap;
import static android.hardware.SensorManager.*;
import android.widget.ImageView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by dnwiebe on 11/6/15.
 */
public class SquareControllerTest {
    private ImageView xRadiator;
    private ImageView yRadiator;
    private ImageView zRadiator;
    private Bitmap bitmap;
    private SquareController subject;

    @Before
    public void setup () {
        xRadiator = makeRadiator ();
        yRadiator = makeRadiator ();
        zRadiator = makeRadiator ();
        bitmap = mock (Bitmap.class);
        subject = new SquareController (xRadiator, yRadiator, zRadiator);
        subject.bitmapFactory = (width, height, config) -> bitmap;
    }

    @Test
    public void constructionSetsSquares () {
        assertSame (xRadiator, subject.xRadiator);
        assertSame (yRadiator, subject.yRadiator);
        assertSame (zRadiator, subject.zRadiator);
    }

    @Test
    public void xRadiatorShouldTranslateToReds () {
        checkValueToColor (-2.0 * GRAVITY_EARTH, 0.0, 0.0, xRadiator, 0xFF000000);
        checkValueToColor (0.0 - GRAVITY_EARTH, 0.0, 0.0, xRadiator, 0xFF000000);
        checkValueToColor (0.0, 0.0, 0.0, xRadiator, 0xFF7F0000);
        checkValueToColor (GRAVITY_EARTH, 0.0, 0.0, xRadiator, 0xFFFF0000);
        checkValueToColor (2.0 * GRAVITY_EARTH, 0.0, 0.0, xRadiator, 0xFFFF0000);
    }

    @Test
    public void yRadiatorShouldTranslateToGreens () {
        checkValueToColor (0.0, -2.0 * GRAVITY_EARTH, 0.0, yRadiator, 0xFF000000);
        checkValueToColor (0.0, 0.0 - GRAVITY_EARTH, 0.0, yRadiator, 0xFF000000);
        checkValueToColor (0.0, 0.0, 0.0, yRadiator, 0xFF007F00);
        checkValueToColor (0.0, GRAVITY_EARTH, 0.0, yRadiator, 0xFF00FF00);
        checkValueToColor (0.0, 2.0 * GRAVITY_EARTH, 0.0, yRadiator, 0xFF00FF00);
    }

    @Test
    public void zRadiatorShouldTranslateToBlues () {
        checkValueToColor (0.0, 0.0, -2.0 * GRAVITY_EARTH, zRadiator, 0xFF000000);
        checkValueToColor (0.0, 0.0, 0.0 - GRAVITY_EARTH, zRadiator, 0xFF000000);
        checkValueToColor (0.0, 0.0, 0.0, zRadiator, 0xFF00007F);
        checkValueToColor (0.0, 0.0, GRAVITY_EARTH, zRadiator, 0xFF0000FF);
        checkValueToColor (0.0, 0.0, 2.0 * GRAVITY_EARTH, zRadiator, 0xFF0000FF);
    }

    private void checkValueToColor (double x, double y, double z, ImageView radiator, int expectedColor) {
        reset (radiator, bitmap);

        subject.onSensorChanged (x, y, z);

        ArgumentCaptor<Bitmap> captor = ArgumentCaptor.forClass (Bitmap.class);
        verify (radiator).setImageBitmap (captor.capture ());
        Bitmap bitmap = captor.getValue ();
        verify (bitmap).eraseColor (expectedColor);
    }

    private ImageView makeRadiator () {
        ImageView result = mock (ImageView.class);
        when (result.getWidth ()).thenReturn (100);
        when (result.getHeight ()).thenReturn (100);
        return result;
    }
}
