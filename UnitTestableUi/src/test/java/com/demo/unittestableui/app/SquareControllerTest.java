package com.demo.unittestableui.app;

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

    private static final double MINUS_2_GS = -2.0 * GRAVITY_EARTH;
    private static final double MINUS_1_G = -1.0 * GRAVITY_EARTH;
    private static final double ZERO_G = 0.0;
    private static final double PLUS_1_G = GRAVITY_EARTH;
    private static final double PLUS_2_GS = 2.0 * GRAVITY_EARTH;

    @Before
    public void setup () {
        xRadiator = makeRadiator ();
        yRadiator = makeRadiator ();
        zRadiator = makeRadiator ();
        bitmap = mock (Bitmap.class);
        subject = new SquareController (xRadiator, yRadiator, zRadiator);
        // PACKAGE-PRIVATE FACTORY INNER INTERFACE
        // Necessary because Bitmap.createBitmap is a static factory method in android.*
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
        checkValueToColor (MINUS_2_GS, 0.0, 0.0, xRadiator, 0xFF000000);
        checkValueToColor (MINUS_1_G, 0.0, 0.0, xRadiator, 0xFF000000);
        checkValueToColor (ZERO_G, 0.0, 0.0, xRadiator, 0xFF7F0000);
        checkValueToColor (PLUS_1_G, 0.0, 0.0, xRadiator, 0xFFFF0000);
        checkValueToColor (PLUS_2_GS, 0.0, 0.0, xRadiator, 0xFFFF0000);
    }

    @Test
    public void yRadiatorShouldTranslateToGreens () {
        checkValueToColor (0.0, MINUS_2_GS, 0.0, yRadiator, 0xFF000000);
        checkValueToColor (0.0, MINUS_1_G, 0.0, yRadiator, 0xFF000000);
        checkValueToColor (0.0, ZERO_G, 0.0, yRadiator, 0xFF007F00);
        checkValueToColor (0.0, PLUS_1_G, 0.0, yRadiator, 0xFF00FF00);
        checkValueToColor (0.0, PLUS_2_GS, 0.0, yRadiator, 0xFF00FF00);
    }

    @Test
    public void zRadiatorShouldTranslateToBlues () {
        checkValueToColor (0.0, 0.0, MINUS_2_GS, zRadiator, 0xFF000000);
        checkValueToColor (0.0, 0.0, MINUS_1_G, zRadiator, 0xFF000000);
        checkValueToColor (0.0, 0.0, ZERO_G, zRadiator, 0xFF00007F);
        checkValueToColor (0.0, 0.0, PLUS_1_G, zRadiator, 0xFF0000FF);
        checkValueToColor (0.0, 0.0, PLUS_2_GS, zRadiator, 0xFF0000FF);
    }

    @Test
    public void justToCoverAnEmptyButRequiredMethod () {
        subject.onAccuracyChanged (null, 0);
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
