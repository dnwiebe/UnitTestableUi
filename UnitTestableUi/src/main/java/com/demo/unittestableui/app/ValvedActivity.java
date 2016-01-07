package com.demo.unittestableui.app;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by dnwiebe on 11/5/15.
 */

// VALVE SUPERCLASS
// Necessary to prevent calls to unmocked superclass from throwing exceptions
public class ValvedActivity extends Activity {
    private boolean valveIsOpen = true;

    public void closeValve () {
        valveIsOpen = false;
    }

    @Override
    public void onCreate (Bundle bundle) {if (valveIsOpen) {super.onCreate (bundle);}}

    @Override
    public void onStart () {if (valveIsOpen) {super.onStart ();}}

    @Override
    public void onRestart () {if (valveIsOpen) {super.onRestart ();}}

    @Override
    public void onPause () {if (valveIsOpen) {super.onPause ();}}

    @Override
    public void onResume () {if (valveIsOpen) {super.onResume ();}}

    @Override
    public void onStop () {if (valveIsOpen) {super.onStop ();}}

    @Override
    public void onDestroy () {if (valveIsOpen) {super.onDestroy ();}}
}
