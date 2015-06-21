package dmays.potentialarcher.ui;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import dmays.potentialarcher.R;
import dmays.potentialarcher.adapter.LocationListViewAdapter;
import dmays.potentialarcher.persistence.LocationDbHelper;

public class DataViewActivity extends AppCompatActivity {

    private static final String TAG = DataViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        TextView messageTextView = (TextView) findViewById(R.id.message);
        ListView listView = (ListView) findViewById(R.id.data);

        LocationDbHelper locationDbHelper = new LocationDbHelper(this);

        Cursor cursor = locationDbHelper.queryAll();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                Log.i(TAG, "Cursor has " + cursor.getCount() + " records.");
                LocationListViewAdapter adapter = new LocationListViewAdapter(this, cursor);
                listView.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
            } else {
                Log.i(TAG, "Cursor contains no records");
                progressBar.setVisibility(View.GONE);
                messageTextView.setText(R.string.no_data);
                messageTextView.setVisibility(View.VISIBLE);
            }
        } else {
            Log.wtf(TAG, "Cursor is null?!");
            progressBar.setVisibility(View.GONE);
            messageTextView.setText(R.string.no_data);
            messageTextView.setVisibility(View.VISIBLE);
        }
    }
}
