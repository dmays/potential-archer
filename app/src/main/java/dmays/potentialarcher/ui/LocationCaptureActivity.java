package dmays.potentialarcher.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import dmays.potentialarcher.persistence.LocationDbHelper;
import dmays.potentialarcher.persistence.LocationsTable;
import dmays.potentialarcher.R;

public class LocationCaptureActivity extends AppCompatActivity implements LocationListener {

    public static final String TAG = LocationCaptureActivity.class.getSimpleName();

    private static final boolean SHOULD_MOCK_LOCATION = false;

    private static final long MOCK_LOCATION_INTERVAL = TimeUnit.SECONDS.toMillis(30);

    private static double sMockLatitude = 20.856874;

    private static double sMockLongitude = -86.622137;

    private static float sMockBearing = 247.6f;

    private static final SecureRandom RANDOM = new SecureRandom();

    private final Handler mHandler = new Handler();
    private final Runnable mLocationMockingRunnable = new Runnable() {
        @Override
        public void run() {

            // help: i'm trapped in a floating faraday cage
            float adjustment = RANDOM.nextFloat();
            boolean adjustDown = RANDOM.nextBoolean();
            if (adjustDown) {
                adjustment = 0.0f - adjustment;
            }

            sMockBearing = sMockBearing + adjustment;
            sMockLatitude = sMockLatitude + adjustment;
            sMockLongitude = sMockLongitude + adjustment;

            Location mockLocation = new Location("MOCK");
            mockLocation.setLatitude(sMockLatitude);
            mockLocation.setLongitude(sMockLongitude);
            mockLocation.setBearing(sMockBearing);
            mockLocation.setSpeed(1.0f);
            mockLocation.setAccuracy(-1.0f);
            mockLocation.setAltitude(-1.0);
            mockLocation.setTime(new Date().getTime());
            onLocationChanged(mockLocation);

            mHandler.postDelayed(this, MOCK_LOCATION_INTERVAL);
        }
    };

    private LocationManager mLocationManager;
    private LocationDbHelper mLocationDbHelper;

    private TextView mLatTextView;
    private TextView mLonTextView;
    private TextView mTimeTextView;
    private TextView mAccTextView;
    private TextView mAltTextView;
    private TextView mBearTextView;
    private TextView mSpeedTextView;
    private TextView mRowsTextView;

    private boolean mLocationUpdatesRequested;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_capture);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationDbHelper = new LocationDbHelper(this);

        mLatTextView = (TextView) findViewById(R.id.lat);
        mLonTextView = (TextView) findViewById(R.id.lon);
        mTimeTextView = (TextView) findViewById(R.id.time);
        mAccTextView = (TextView) findViewById(R.id.acc);
        mAltTextView = (TextView) findViewById(R.id.alt);
        mBearTextView = (TextView) findViewById(R.id.bear);
        mSpeedTextView = (TextView) findViewById(R.id.speed);
        mRowsTextView = (TextView) findViewById(R.id.rows);

        long rows = mLocationDbHelper.countRows();
        mRowsTextView.setText(String.valueOf(rows));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (SHOULD_MOCK_LOCATION) {
            mHandler.post(mLocationMockingRunnable);
        } else {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.i(TAG, "GPS provider enabled, requesting updates.");
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TimeUnit.SECONDS.toMillis(1), 0.0f, this);
                mLocationUpdatesRequested = true;
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.enable_gps)
                       .setMessage(R.string.enable_gps_message)
                       .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                               startActivity(myIntent);
                           }
                       })
                       .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               finish();
                           }
                       });

                builder.show();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (SHOULD_MOCK_LOCATION) {
            mHandler.removeCallbacks(mLocationMockingRunnable);
        } else if (mLocationUpdatesRequested) {
            mLocationManager.removeUpdates(this);
            mLocationUpdatesRequested = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // save location to database
        Log.i(TAG, "onLocationChanged: " + location.toString());
        long rowId = mLocationDbHelper.insert(LocationsTable.TABLE_NAME, LocationsTable.toValues(location));
        Log.i(TAG, rowId != -1 ? "location inserted as row " + rowId : "location not inserted");

        // update display
        mLatTextView.setText(String.valueOf(location.getLatitude()));
        mLonTextView.setText(String.valueOf(location.getLongitude()));
        mTimeTextView.setText(new Date(location.getTime()).toString());
        mAccTextView.setText(String.valueOf(location.getAccuracy()));
        mAltTextView.setText(String.valueOf(location.getAltitude()));
        mBearTextView.setText(String.valueOf(location.getBearing()));
        mSpeedTextView.setText(String.valueOf(location.getSpeed()));
        mRowsTextView.setText(String.valueOf(rowId));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Status Changed to: " + status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Provider enabled: " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Provider disabled: " + provider, Toast.LENGTH_SHORT).show();
    }
}
