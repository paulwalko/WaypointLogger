package walko.paul.waypointlogger;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;
import android.os.Environment;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
import android.widget.EditText;


public class ShowLocationActivity extends Activity implements LocationListener {
    private TextView latituteField;
    private TextView longitudeField;
    private LocationManager locationManager;
    private String provider;
    public static final String LOG_TAG = "myLogs";
    double lat = 0;
    double lon = 0;
    EditText label;


    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        latituteField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);
        label = (EditText)findViewById(R.id.label);
        final Button button = (Button) findViewById(R.id.Button);
        final Button refresh = (Button) findViewById(R.id.refresh);
        updateLocation();

        refresh.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                updateLocation();
            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                BufferedWriter output;
                if (isExternalStorageWritable()) {
                    FileWriter f;
                    try {
                        output = new BufferedWriter(new FileWriter(Environment.getExternalStorageDirectory() + "/waypoints.csv", true));
                        String st = "\"";
                        output.append(label.getText().toString() + "," + st + lat + "," + lon + st);
                        output.newLine();
                        output.close();

                        /*f = new FileWriter(Environment.getExternalStorageDirectory() + "/waypoints" + ".csv", true);
                        f.write(lat + ",," + lon);
                        f.flush();
                        f.close();*/
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void updateLocation(){
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            latituteField.setText("Location not available");
            longitudeField.setText("Location not available");
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;

    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lon));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
} 

