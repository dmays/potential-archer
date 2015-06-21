package dmays.potentialarcher.adapter;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dmays.potentialarcher.R;
import dmays.potentialarcher.persistence.LocationsTable;

public class LocationListViewAdapter extends CursorAdapter {

    public static final String TAG = LocationListViewAdapter.class.getSimpleName();

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd MMM yy HH:mm:ss", Locale.getDefault());

    public LocationListViewAdapter(Context context, Cursor c) {
        super(context, c, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_location, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView lat = (TextView) view.findViewById(R.id.lat);
        TextView lon = (TextView) view.findViewById(R.id.lon);
        TextView alt = (TextView) view.findViewById(R.id.alt);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView acc = (TextView) view.findViewById(R.id.acc);
        TextView bear = (TextView) view.findViewById(R.id.bear);
        TextView spd = (TextView) view.findViewById(R.id.spd);

        Location loc = LocationsTable.fromCursor(cursor);

        lat.setText(String.valueOf(loc.getLatitude()));
        lon.setText(String.valueOf(loc.getLongitude()));
        alt.setText(String.valueOf(loc.getAltitude()));
        time.setText(SDF.format(new Date(loc.getTime())));
        acc.setText(String.valueOf(loc.getAccuracy()));
        bear.setText(String.valueOf(loc.getBearing()));
        spd.setText(String.valueOf(loc.getSpeed()));
    }
}
