package com.ads.unittestableui.app;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by dnwiebe on 11/6/15.
 */
public class ValvedActivityTest {
    @Test
    public void normalOperationProducesException () {
        ValvedActivity subject = new ValvedActivity ();

        checkMethodThrowsException ("onCreate", () -> subject.onCreate (null));
        checkMethodThrowsException ("onStart", subject::onStart);
        checkMethodThrowsException ("onRestart", subject::onRestart);
        checkMethodThrowsException ("onResume", subject::onResume);
        checkMethodThrowsException ("onPause", subject::onPause);
        checkMethodThrowsException ("onStop", subject::onStop);
        checkMethodThrowsException ("onDestroy", new Runnable () {
            @Override
            public void run () {
                subject.onDestroy ();
            }
        });
    }

    @Test
    public void closingValveEliminatesException () {
        ValvedActivity subject = new ValvedActivity ();
        subject.closeValve ();;

        subject.onCreate (null);
        subject.onStart ();
        subject.onRestart ();
        subject.onResume ();
        subject.onPause ();
        subject.onStop ();
        subject.onDestroy ();
        // no exception: pass
    }

    private void checkMethodThrowsException (String name, Runnable caller) {
        try {
            caller.run ();
            fail ();
        }
        catch (RuntimeException e) {
            assertTrue (e.getMessage ().startsWith ("Method " + name + " in android.app.Activity not mocked."));
        }
    }
}
