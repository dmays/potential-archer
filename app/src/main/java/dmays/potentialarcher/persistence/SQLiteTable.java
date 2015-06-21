package dmays.potentialarcher.persistence;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public abstract class SQLiteTable {

    public final static String TAG = SQLiteTable.class.getSimpleName();

    /**
     * Frequently used name for a common primary key field
     */
    public final static String _ID = "_id";

    /**
     * Actions to take when this table is created.
     * @param db an instance of the SQLiteDatabase this table is stored in.
     */
    public void onCreate(SQLiteDatabase db) {
        String sql = getSchema();
        Log.d(TAG, "Creating table " + getName() + ": " + sql);
        db.execSQL(sql);
    }

    public abstract String getSchema();

    /**
     * Actions to take for this table when the database is updated.
     * @param db an instance of the SQLiteDatabase this table is stored in.
     * @param oldVersion previous version of the database.
     * @param newVersion new version of the database.
     */
    public abstract void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * Get the name for this table.
     * @return the name of this table.
     */
    public abstract String getName();

    /**
     * Get an array of all column names.
     * @return an array of all column names.
     */
    public abstract String[] getAllColumns();

    /**
     * Get the number of rows already in this table.
     * @param db database this table is stored in.
     * @return number of rows in this table.
     */
    public long countRows(SQLiteDatabase db) {
        // TODO: this seems raw and dirty, is there a better way?
        Cursor cursor = db.query(getName(), getPrimaryKeyFields(), null, null, null, null, null);
        if (cursor != null) {
            int count = cursor.getCount();
            cursor.close();
            return count;
        } else {
            return 0;
        }
    }

    /**
     * Pretty self explanatory.
     * @return an array of the primary key field(s)
     */
    public String[] getPrimaryKeyFields() {
        return new String[]{_ID};
    }

    /**
     * Drops this table from the database.
     * @param db an instance of the SQLiteDatabase this table is stored in.
     */
    protected void dropTable(SQLiteDatabase db) {
        Log.i(TAG, "Dropping table " + getName());
        db.execSQL("DROP TABLE " + getName() + ";");
    }
}