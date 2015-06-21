package dmays.potentialarcher.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import java.util.ArrayList;

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

        ArrayList<Location> locations = new ArrayList<>();
        db.beginTransaction();

        String[] almostAllColumns = new String[ALL_COLUMNS.length - 1];
        System.arraycopy(ALL_COLUMNS, 0, almostAllColumns, 0, ALL_COLUMNS.length - 1);
        Cursor cursor = db.query(TABLE_NAME, almostAllColumns, null, null, null, null, null);
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                // get previous locations from table
                do {
                    // adding provider column for db version 2
                    Location location = fromCursor(cursor);
                    Log.i(TAG, "Recreated location from cursor: " + location.toString());
                    if (location.getProvider().equals(DATABASE_PROVIDER)) {
                        location.setProvider(LocationManager.GPS_PROVIDER);
                    }
                    locations.add(location);
                } while (cursor.moveToNext());

                dropTable(db);
                onCreate(db);

                // add previous locations to new table
                Log.i(TAG, "populating new table");
                for (Location location : locations) {
                    long rowId = db.insert(TABLE_NAME, null, toValues(location));
                    if (rowId == -1) {
                        Log.e(TAG, "Error inserting location: " + location.toString());
                    }
                }

                db.setTransactionSuccessful();
            } else {
                Log.i(TAG, "No records to update, recreating table.");
                dropTable(db);
                onCreate(db);
            }
        } else {
            Log.wtf(TAG, "Cursor is null while updating");
            // TODO: if this occurs a major failure is indicated -- how to proceed?
        }

        db.endTransaction();
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
        value.put(LocationsTable.LAT, location.getLatitude());
        value.put(LocationsTable.LON, location.getLongitude());
        value.put(LocationsTable.TIME, location.getTime());
        value.put(LocationsTable.ACCURACY, location.getAccuracy());
        value.put(LocationsTable.ALTITUDE, location.getAltitude());
        value.put(LocationsTable.BEARING, location.getBearing());
        value.put(LocationsTable.SPEED, location.getSpeed());
        value.put(LocationsTable.PROVIDER, location.getProvider());
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
        loc.setLatitude(cursor.getDouble(cursor.getColumnIndexOrThrow(LocationsTable.LAT)));
        loc.setLongitude(cursor.getDouble(cursor.getColumnIndexOrThrow(LocationsTable.LON)));
        loc.setTime(cursor.getLong(cursor.getColumnIndexOrThrow(LocationsTable.TIME)));
        loc.setAccuracy(cursor.getFloat(cursor.getColumnIndexOrThrow(LocationsTable.ACCURACY)));
        loc.setAltitude(cursor.getDouble(cursor.getColumnIndexOrThrow(LocationsTable.ALTITUDE)));
        loc.setBearing(cursor.getFloat(cursor.getColumnIndexOrThrow(LocationsTable.BEARING)));
        loc.setSpeed(cursor.getFloat(cursor.getColumnIndexOrThrow(LocationsTable.SPEED)));
        String provider = cursor.getString(cursor.getColumnIndexOrThrow(LocationsTable.PROVIDER));

        // null check needed for migration to version 2 of db.  Could probably do away with after a time.
        if (provider != null) {
            loc.setProvider(provider);
        }

        return loc;
    }
}