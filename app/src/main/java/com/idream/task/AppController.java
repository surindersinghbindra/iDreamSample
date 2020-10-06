package com.idream.task;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.FirebaseApp;

import timber.log.Timber;

public class AppController extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseApp.initializeApp(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }
    }

    public static class ReleaseTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

         //   Crashlytics.logException(t);
            // log your crash to your favourite
            // Sending crash report to Firebase CrashAnalytics

            // FirebaseCrash.report(message);
            // FirebaseCrash.report(new Exception(message));
        }
    }
}
