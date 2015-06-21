package dmays.potentialarcher;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class PotentialArcherApplication extends Application {

    public static final String TAG = PotentialArcherApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());
    }
}
