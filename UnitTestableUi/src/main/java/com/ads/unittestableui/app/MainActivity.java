package com.ads.unittestableui.app;

import android.os.Bundle;

public class MainActivity extends ValvedActivity {

    MainActivityImpl impl = new MainActivityImpl (this);

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        impl.onCreate (savedInstanceState);
    }

    @Override
    public void onStart () {
        super.onStart ();
        impl.onStart ();
    }

    @Override
    public void onStop () {
        super.onStop ();
        impl.onStop ();
    }
}
