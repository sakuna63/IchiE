package com.example.aktsk.ichie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;


public class MainActivity extends ActionBarActivity implements LocationListener, OnMapReadyCallback, OnMapClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    // 更新時間(目安)
    private static final int LOCATION_UPDATE_MIN_TIME = 0;
    // 更新距離(目安)
    private static final float LOCATION_UPDATE_MIN_DISTANCE = 0;

    private LocationManager locationManager;
    private GoogleMap googleMap;
    private int height;
    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        height = bitmap.getHeight();
        width = bitmap.getWidth();
    }

    private void requestLocationUpdates() {
//        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        if (isNetworkEnabled) {
//            locationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER,
//                    LOCATION_UPDATE_MIN_TIME,
//                    LOCATION_UPDATE_MIN_DISTANCE,
//                    this
//            );
//            Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//            if (location != null) {
//                showLocation(location);
//            }
//        } else {
//            String message = "Networkが無効になっています。";
//            showMessage(message);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng sydney = new LatLng(-33.867, 151.206);

        this.googleMap = googleMap;
        googleMap.setOnMapClickListener(this);
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        CameraPosition cameraPosition = googleMap.getCameraPosition();
        float zoomLevel = cameraPosition.zoom;
        LatLng centerPosition = cameraPosition.target;
        Logger.log(zoomLevel);

        float ratio = meterPerPix(googleMap);
        Logger.log(ratio);
        float meters = width * ratio;
        Logger.log(meters);

        googleMap.addGroundOverlay(
                new GroundOverlayOptions()
                        .position(centerPosition, meters)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
        );
//        googleMap.addMarker(
//                new MarkerOptions()
//                        .position(latLng)
//                        .flat(true)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher))
//        );
    }

    public static float meterPerPix(GoogleMap map) {
        LatLng base = map.getCameraPosition().target;
        final float OFFSET_LON = 0.05f;

        Location baseLoc = new Location("");
        baseLoc.setLatitude(base.latitude);
        baseLoc.setLongitude(base.longitude);

        Location dest = new Location("");
        dest.setLatitude(base.latitude);
        dest.setLongitude(base.longitude + OFFSET_LON);

        // メートル返る
        float dis = baseLoc.distanceTo(dest);

        Projection proj = map.getProjection();
        Point basePt = proj.toScreenLocation(base);
        Point destPt = proj.toScreenLocation(new LatLng(dest.getLatitude(), dest.getLongitude()));
        float pixDis = Math.abs(destPt.x - basePt.x);

        return dis / pixDis;
    }
}
