package com.ads.unittestableui.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by dnwiebe on 11/5/15.
 */

// VALVE SUPERCLASS
// Necessary to prevent calls to unmocked superclass from throwing exceptions
public class ValvedActivity extends Activity {
    private boolean superValveOpen = true;

    public void closeValve () {
        superValveOpen = false;
    }

    @Override
    public void onCreate (Bundle bundle) {if (superValveOpen) {super.onCreate (bundle);}}

    @Override
    public void onStart () {if (superValveOpen) {super.onStart ();}}

    @Override
    public void onRestart () {if (superValveOpen) {super.onRestart ();}}

    @Override
    public void onPause () {if (superValveOpen) {super.onPause ();}}

    @Override
    public void onResume () {if (superValveOpen) {super.onResume ();}}

    @Override
    public void onStop () {if (superValveOpen) {super.onStop ();}}

    @Override
    public void onDestroy () {if (superValveOpen) {super.onDestroy ();}}
}
