package dmays.potentialarcher.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import dmays.potentialarcher.R;
import dmays.potentialarcher.persistence.LocationDbHelper;

public class MenuActivity extends AppCompatActivity {

    public static final String TAG = MenuActivity.class.getSimpleName();

    private int mExportFormatChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);

        Button mCaptureButton = (Button) findViewById(R.id.capture);
        Button mExamineButton = (Button) findViewById(R.id.examine);
        Button mExportButton = (Button) findViewById(R.id.export);

        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, LocationCaptureActivity.class);
                startActivity(intent);
            }
        });

        mExamineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, DataViewActivity.class);
                startActivity(intent);
            }
        });

        mExportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExportFormatChoice = 0;
                AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this)
                        .setCancelable(true)
                        .setSingleChoiceItems(R.array.export_formats, mExportFormatChoice, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mExportFormatChoice = which;
                            }
                        })
                        .setPositiveButton(R.string.export, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LocationDbHelper dbHelper = new LocationDbHelper(MenuActivity.this);
                                switch (mExportFormatChoice) {
                                    case 0:
                                        dbHelper.exportToCsv();
                                        break;
                                    case 1:
                                        dbHelper.exportToSql();
                                        break;
                                    default:
                                        Toast.makeText(MenuActivity.this, R.string.unknown_selection, Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "Unknown selection for export format type: " + which);
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });
    }
}
