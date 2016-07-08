package app.moveinsync;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by bharat on 10/5/15.
 */
public class SavelocationActivity extends Activity {
    private String latitude;
    private String longitude;
    private EditText locationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_location);
        Log.i(getClass().getName(), "On create");
        initalizeDataFromIntent();
        initializeUIElements();
    }

    private void initializeUIElements() {
        locationName = (EditText) findViewById(R.id.etName);
        locationName.setCursorVisible(true);
        TextView tv = (TextView) findViewById(R.id.tvLatitude);
        tv.setText(Html.fromHtml("<b> Latitude : </b>" + latitude));
        tv = (TextView) findViewById(R.id.tvLongitude);
        tv.setText(Html.fromHtml("<b> Longitude : </b>" + longitude));
        Button bt = (Button) findViewById(R.id.btSaveLocation);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationName.getText().toString() == null || locationName.getText().toString().length() == 0) {
                    Toast.makeText(v.getContext(), v.getContext().getResources().getString(R.string.fill_location), Toast.LENGTH_SHORT).show();
                    return;
                }
                DatabaseHelper.getDBUtil(v.getContext(), SavedLocationObject.class).add(new SavedLocationObject(locationName.getText().toString(), latitude, longitude));
                finish();
            }
        });
    }

    private void initalizeDataFromIntent() {
        latitude = getIntent().getExtras().getString(StringConstants.latitude);
        longitude = getIntent().getExtras().getString(StringConstants.longitude);

    }
}
