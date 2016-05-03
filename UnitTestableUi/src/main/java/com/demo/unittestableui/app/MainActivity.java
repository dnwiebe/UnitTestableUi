package com.demo.unittestableui.app;

import android.os.Bundle;

public class MainActivity extends ValvedActivity {

    MainActivityImpl impl = new MainActivityImpl ();

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        impl.onCreate (this, savedInstanceState);
    }

    @Override
    public void onStart () {
        super.onStart ();
        impl.onStart (this);
    }

    @Override
    public void onStop () {
        super.onStop ();
        impl.onStop ();
    }
}
