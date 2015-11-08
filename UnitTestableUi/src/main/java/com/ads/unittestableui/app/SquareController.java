package com.ads.unittestableui.app;

import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.ImageView;
import static android.hardware.SensorManager.*;

/**
 * Created by dnwiebe on 11/6/15.
 */
public class SquareController implements SensorEventListener {

    ImageView xRadiator;
    ImageView yRadiator;
    ImageView zRadiator;

    // PACKAGE-PRIVATE FACTORY INNER INTERFACE
    // Necessary because Bitmap.createBitmap is a static factory method in android.*
    interface BitmapFactory {
        Bitmap create (int width, int height, Bitmap.Config config);
    }
    BitmapFactory bitmapFactory = Bitmap::createBitmap; // rhs is untested and untestable code

    public SquareController (ImageView xRadiator, ImageView yRadiator, ImageView zRadiator) {
        this.xRadiator = xRadiator;
        this.yRadiator = yRadiator;
        this.zRadiator = zRadiator;
    }

    // ROUTING METHOD
    // Necessary because SensorEvent constructor is non-public; can't create in test
    @Override
    public void onSensorChanged (SensorEvent event) {
        // Untested and untestable code
        onSensorChanged (event.values[0], event.values[1], event.values[2]);
    }

    public void onSensorChanged (double x, double y, double z) {
        updateRadiator (xRadiator, x, 16);
        updateRadiator (yRadiator, y, 8);
        updateRadiator (zRadiator, z, 0);
    }

    @Override
    public void onAccuracyChanged (Sensor sensor, int accuracy) {}

    private void updateRadiator (ImageView radiator, double value, int shift) {
        int component = makeColorComponent (value);
        int color = makeOpaquePrimaryColorFromComponent (component, shift);
        Bitmap bitmap = makeSolidColorBitmapToCover (radiator, color);
        radiator.setImageBitmap (bitmap);
    }

    private static int makeOpaquePrimaryColorFromComponent (int component, int shift) {
        return (component << shift) | 0xFF000000;
    }

    private static int makeColorComponent (double value) {
        double zeroToOne = ((value / GRAVITY_EARTH) + 1.0) / 2.0;
        double zeroTo255 = zeroToOne * 255;
        if (zeroTo255 < 0.0) {zeroTo255 = 0.0;}
        if (zeroTo255 > 255.0) {zeroTo255 = 255.0;}
        return (int)zeroTo255;
    }

    private Bitmap makeSolidColorBitmapToCover (ImageView radiator, int color) {
        Bitmap bitmap = bitmapFactory.create (radiator.getWidth (), radiator.getHeight (), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor (color);
        return bitmap;
    }
}
