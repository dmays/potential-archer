package dmays.potentialarcher.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import dmays.potentialarcher.util.BundleUtil;

public class LocationsTable extends SQLiteTable {

    public static final String TAG = LocationsTable.class.getSimpleName();

    public static final String TABLE_NAME = "locations";

    public static final String LAT = "lat";

    public static final String LON = "lon";

    public static final String TIME = "capture_time";

    public static final String ACCURACY = "accuracy";

    public static final String ALTITUDE = "altitude";

    public static final String BEARING = "bearing";

    public static final String SPEED = "speed";

    public static final String PROVIDER = "provider";

    public static final String[] ALL_COLUMNS = {
            _ID,
            LAT,
            LON,
            TIME,
            ACCURACY,
            ALTITUDE,
            BEARING,
            SPEED,
            PROVIDER
    };

    public static final String DATABASE_PROVIDER = "DATABASE_PROVIDER";

    @Override
    @SuppressWarnings("StringBufferReplaceableByString")
    public String getSchema() {
        StringBuilder sb = new StringBuilder("CREATE TABLE ").append(TABLE_NAME).append(" (");
        sb.append(_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(LAT).append(" REAL NOT NULL, ");
        sb.append(LON).append(" REAL NOT NULL, ");
        sb.append(TIME).append(" LONG NOT NULL, ");
        sb.append(ACCURACY).append(" REAL NOT NULL, ");
        sb.append(ALTITUDE).append(" REAL NOT NULL, ");
        sb.append(BEARING).append(" REAL NOT NULL, ");
        sb.append(SPEED).append(" REAL NOT NULL, ");
        sb.append(PROVIDER).append(" TEXT NOT NULL");
        sb.append(");");
        return sb.toString();
    }

    @Override
    public void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpdate: nothing to do");
    }

    @Override
    public String getName() {
        return TABLE_NAME;
    }

    @Override
    public String[] getAllColumns() {
        return ALL_COLUMNS;
    }

    /**
     * Copies appropriate values from a Location object into a ContentValues object for writing to database.
     *
     * @param location
     *         Location to be stored in database.
     *
     * @return ContentValues of the Location.
     */
    public static ContentValues toValues(Location location) {
        ContentValues value = new ContentValues();
        value.put(LAT, location.getLatitude());
        value.put(LON, location.getLongitude());
        value.put(TIME, location.getTime());
        value.put(ACCURACY, location.getAccuracy());
        value.put(ALTITUDE, location.getAltitude());
        value.put(BEARING, location.getBearing());
        value.put(SPEED, location.getSpeed());
        value.put(PROVIDER, location.getProvider());
        if (location.getExtras() != null) {
            BundleUtil.logBundleMap(location.getExtras());
        }

        return value;
    }

    /**
     * Recreates a Location from a Cursor.
     *
     * @param cursor
     *         Cursor moved to a valid Location saved in this table.
     *
     * @return Location recreated from cursor.
     */
    public static Location fromCursor(Cursor cursor) {
        Location loc = new Location(DATABASE_PROVIDER);
        loc.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(LAT)));
        loc.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(LON)));
        loc.setTime(cursor.getLong(cursor.getColumnIndexOrThrow(TIME)));
        loc.setAccuracy(cursor.getFloat(cursor.getColumnIndexOrThrow(ACCURACY)));
        loc.setAltitude(cursor.getDouble(cursor.getColumnIndexOrThrow(ALTITUDE)));
        loc.setBearing(cursor.getFloat(cursor.getColumnIndexOrThrow(BEARING)));
        loc.setSpeed(cursor.getFloat(cursor.getColumnIndexOrThrow(SPEED)));
        String provider = cursor.getString(cursor.getColumnIndexOrThrow(PROVIDER));

        // null check needed for migration to version 2 of db.  Could probably do away with after a time.
        if (provider != null) {
            loc.setProvider(provider);
        }

        return loc;
    }
}