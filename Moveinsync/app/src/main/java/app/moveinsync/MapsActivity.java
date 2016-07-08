package app.moveinsync;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private MapFragment mMap; // Might be null if Google Play services APK is not available.
    private EditText locationName;
    private ImageView searchLocation;
    private ImageView saveLocation;
    private ImageView savedLocation;
    private MarkerOptions marker = new MarkerOptions();
    private Geocoder geocoder;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.i(getClass().getName(), "On create");
        setUpMapIfNeeded();
        marker = new MarkerOptions();
        geocoder = new Geocoder(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        initializeUIElements();
    }

    private void initializeUIElements() {
        locationName = (EditText) findViewById(R.id.etSearchLocation);
        locationName.setCursorVisible(true);
        searchLocation = (ImageView) findViewById(R.id.btSearchLocation);
        saveLocation = (ImageView) findViewById(R.id.btSaveLocatione);
        saveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(marker.getPosition()==null)
                {
                    Toast.makeText(v.getContext(),"No location selected",Toast.LENGTH_SHORT);
                    return;
                }
                Intent intent = new Intent(v.getContext(), SavelocationActivity.class);
                intent.putExtra(StringConstants.latitude, marker.getPosition().latitude + "");
                intent.putExtra(StringConstants.longitude, marker.getPosition().longitude + "");
                v.getContext().startActivity(intent);
            }
        });
        searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == locationName.getText().toString() || locationName.getText().toString().trim().length() == 0) {

                    Toast.makeText(v.getContext(), "Please enter search location", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!determineConnectivity()) {
                    Toast.makeText(v.getContext(), "Please check your net connection", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    List<Address> addresses = geocoder.getFromLocationName(locationName.getText().toString(), 1);
                    if (addresses == null || addresses.size() == 0) {
                        Toast.makeText(v.getContext(), "No such location found, Please try another location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    marker.position(new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                setMapLocation();
            }
        });
        savedLocation = (ImageView) findViewById(R.id.btSavedLocation);
        savedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        List<SavedLocationObject> list = DatabaseHelper.getDBUtil(this, SavedLocationObject.class).fetch(0, 1000, null);
        final SavedLocationAdapter savedLocationAdapter = new SavedLocationAdapter(this, R.layout.saved_location_object, list);
        dialog.setTitle("Saved Location");
        if (list != null && list.size() > 0) {
            dialog.setAdapter(savedLocationAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SavedLocationObject item = savedLocationAdapter.getItem(which);
                    marker.position(new LatLng(Double.parseDouble(item.getLatitude()), Double.parseDouble(item.getLongitude())));
                    setMapLocation();

                }
            });
        } else {
            dialog.setMessage("No location saved yet");
        }
        dialog.show();
    }

    private void setUpMapIfNeeded() {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();

            }
        }
    }

    private void setUpMap() {
        if (!determineConnectivity()) {
            Toast.makeText(this, "Please check your netconnection", Toast.LENGTH_SHORT).show();
            return;
        }
        mMap.getMapAsync(this);
    }

    private void setMapLocation() {

        map.addMarker(marker);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                marker.getPosition(), 15));

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setMyLocationEnabled(true);
        Location location = ((LocationManager) getSystemService(LOCATION_SERVICE))
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location == null) {
            marker.title("Location").draggable(true);
        }
        else {
            marker.title("Location").position(new LatLng(location.getLatitude(), location.getLongitude())).draggable(true);
            googleMap.addMarker(marker);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(location.getLatitude(), location.getLongitude()), 15));
        }


        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker1) {

                marker.position(marker1.getPosition());
//                        mMap.getMap().addMarker(new MarkerOptions().position(marker.getPosition()));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15));
            }
        });

    }

    private boolean determineConnectivity() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isAvailable() && info.isConnected();
    }
}