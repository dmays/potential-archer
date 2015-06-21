package dmays.potentialarcher.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import dmays.potentialarcher.R;

public class LocationDbHelper extends SQLiteOpenHelper {

    public static final String TAG = LocationDbHelper.class.getSimpleName();

    public static final int DB_VERSION = 2;

    public static final String DB_NAME = "locations";

    private static final SQLiteTable LOCATIONS_TABLE = new LocationsTable();

    private final Context mContext;

    public LocationDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        LOCATIONS_TABLE.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOCATIONS_TABLE.onUpdate(db, oldVersion, newVersion);
    }

    public long insert(String table, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();

        boolean bulk = db.inTransaction();
        if (!bulk) {
            db.beginTransaction();
        }

        try {
            long result = db.insert(table, null, values);
            if (-1 != result) {
                db.setTransactionSuccessful();
            }
            return result;
        } finally {
            if (!bulk) {
                db.endTransaction();
            }
        }
    }

    public Cursor queryAll() {
        SQLiteDatabase db = getReadableDatabase();

        boolean bulk = db.inTransaction();
        if (!bulk) {
            db.beginTransaction();
        }

        try {
            Cursor cursor = getReadableDatabase().query(LocationsTable.TABLE_NAME, LocationsTable.ALL_COLUMNS, null, null, null, null, null);
            db.setTransactionSuccessful();
            return cursor;
        } catch (SQLiteException e) {
            Log.e(TAG, "Could not query locations", e);
            return null;
        } finally {
            if (!bulk) {
                db.endTransaction();
            }
        }
    }

    public long countRows(Class<? extends SQLiteTable> tableClass) {
        long count = -1;
        try {
            SQLiteTable table = tableClass.newInstance();
            count = table.countRows(getReadableDatabase());
        } catch (InstantiationException | IllegalAccessException e) {
            Log.e(TAG, "Could not count rows in " + tableClass.getSimpleName(), e);
        }

        return count;
    }

    public long countRows() {
        return LOCATIONS_TABLE.countRows(getReadableDatabase());
    }

    public void exportToSql() {
        // header
        StringBuilder sb = new StringBuilder("PRAGMA foreign_keys=OFF;\nBEGIN TRANSACTION;\n");

        // create a table
        sb.append(LOCATIONS_TABLE.getSchema()).append("\n");

        // insert records into current table
        Cursor cursor = queryAll();
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    do {
                        Location loc = LocationsTable.fromCursor(cursor);
                        sb.append("INSERT INTO \"").append(LOCATIONS_TABLE.getName()).append("\" VALUES(");
                        sb.append(cursor.getInt(cursor.getColumnIndexOrThrow(LocationsTable._ID))).append(',');
                        sb.append(loc.getLatitude()).append(',');
                        sb.append(loc.getLongitude()).append(',');
                        sb.append(loc.getTime()).append(',');
                        sb.append(loc.getAccuracy()).append(',' );
                        sb.append(loc.getAltitude()).append(',' );
                        sb.append(loc.getBearing()).append(',' );
                        sb.append(loc.getSpeed()).append(',' );
                        sb.append('\'').append(loc.getProvider()).append('\'');
                        sb.append(");\n");
                    } while (cursor.moveToNext());

                    // commit the transaction
                    sb.append("COMMIT;\n");

                    File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "database.sql");
                    FileWriter fileWriter = new FileWriter(outFile);
                    BufferedWriter out = new BufferedWriter(fileWriter);
                    out.write(sb.toString());
                    out.close();
                    Log.i(TAG, "Database exported to " + outFile.getAbsolutePath());
                    Toast.makeText(mContext, String.format(mContext.getString(R.string.var_database_exported_message), outFile.getAbsolutePath()), Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Log.e(TAG, "Couldn't export", e);
                Toast.makeText(mContext, R.string.unable_to_write_file, Toast.LENGTH_SHORT).show();
            } finally {
                cursor.close();
            }
        }
    }

    public void exportToCsv() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < LocationsTable.ALL_COLUMNS.length; i++) {
            if (i != 0) {
                sb.append(',');
            }
            sb.append("\"").append(LocationsTable.ALL_COLUMNS[i]).append("\"");
        }
        sb.append('\n');
        Cursor cursor = queryAll();
        if (cursor != null) {
            try {
                File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "database.csv");
                FileWriter fileWriter = new FileWriter(outFile);
                BufferedWriter out = new BufferedWriter(fileWriter);
                if (cursor.moveToFirst()) {
                    do {
                        Location loc = LocationsTable.fromCursor(cursor);
                        sb.append(cursor.getInt(cursor.getColumnIndexOrThrow(LocationsTable._ID))).append(',');
                        sb.append(loc.getLatitude()).append(',');
                        sb.append(loc.getLongitude()).append(',');
                        sb.append(loc.getTime()).append(',');
                        sb.append(loc.getAccuracy()).append(',' );
                        sb.append(loc.getAltitude()).append(',' );
                        sb.append(loc.getBearing()).append(',' );
                        sb.append(loc.getSpeed()).append(',' );
                        sb.append(loc.getProvider()).append('\n');
                    } while (cursor.moveToNext());
                    out.write(sb.toString());
                    out.close();
                    Log.i(TAG, "Database exported to " + outFile.getAbsolutePath());
                    Toast.makeText(mContext, String.format(mContext.getString(R.string.var_database_exported_message), outFile.getAbsolutePath()), Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Log.e(TAG, "Couldn't export", e);
                Toast.makeText(mContext, R.string.unable_to_write_file, Toast.LENGTH_SHORT).show();
            } finally {
                cursor.close();
            }
        }
    }
}
