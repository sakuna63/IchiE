package com.example.aktsk.ichie;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.koushikdutta.ion.Ion;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class MapActivity extends ActionBarActivity implements LocationListener, OnMapReadyCallback, OnMapClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    // 更新時間(目安)
    private static final int LOCATION_UPDATE_MIN_TIME = 1;
    // 更新距離(目安)
    private static final float LOCATION_UPDATE_MIN_DISTANCE = 1;
    private static final float ZOOM_LEVEL = 16;

    private LocationManager locationManager;
    private GoogleMap googleMap;
    private int height;
    private int width;
    private ProgressDialog progress;
    private LatLng prevPosition;
    private double add = 0;
    private Bitmap bitmap;
    private Circle circle;
    private boolean isStarted = false;
    private float lev;
    private LatLng pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

//        getSupportActionBar().setIcon(R.drawable.logo);
//        getSupportActionBar().setTitle();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        requestLocationUpdates();

        final MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        ImageModel item = getIntent().getParcelableExtra("model");
        if (item.getGood() == -1) {
            progress = new ProgressDialog(this);
            progress.setMessage("位置情報を取得中");
            progress.show();

            // フリーラン
            final View btn = findViewById(R.id.btn);
            final TextView label = (TextView) findViewById(R.id.label_btn);
            label.setText("GO");
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    isStarted = true;
                    label.setText("FIN");
                    btn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            googleMap.snapshot(new SnapshotReadyCallback() {
                                @Override
                                public void onSnapshotReady(final Bitmap bitmap) {
                                    ImageView im = new ImageView(getApplicationContext());
                                    im.setImageBitmap(bitmap);
                                    new Builder(MapActivity.this)
                                            .setTitle("結果")
                                            .setView(im)
                                            .setPositiveButton("シェア", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                                    intent.setType("image/jpeg");
                                                    intent.putExtra(Intent.EXTRA_STREAM, getImageUri(getApplicationContext(), bitmap));
                                                    startActivity(intent);
                                                }
                                            })
                                            .show();
                                }
                            });
                        }
                    });
                }
            });
        }

        Future<Bitmap> f = Ion.with(this).load("http://" + item.getUrl()).asBitmap();
        try {
            bitmap = f.get();
            height = bitmap.getHeight();
            width = bitmap.getWidth();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
//        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.smile);

        final ImageView imageView = (ImageView) findViewById(R.id.image);
        imageView.setImageBitmap(bitmap);
//        }
        progress = new ProgressDialog(this);
        progress.setMessage("位置情報を取得中");
        progress.show();

        final View btn = findViewById(R.id.btn);
        final TextView label = (TextView) findViewById(R.id.label_btn);
        label.setText("SET");
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                googleMap.snapshot(new SnapshotReadyCallback() {
//                    @Override
//                    public void onSnapshotReady(Bitmap bitmap) {
//                        ImageView imageView = new ImageView(MapActivity.this);
//                        imageView.setImageBitmap(bitmap);
//                        imageView.setLayoutParams(new LayoutParams(100, 100));
//                        new Builder(MapActivity.this)
//                                .setView(imageView)
//                                .show();
//                    }
//                });
                new Builder(MapActivity.this)
                        .setTitle("確認")
                        .setMessage("ここに画像をセットしますか？")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pos = googleMap.getCameraPosition().target;
                                lev = googleMap.getCameraPosition().zoom;
                                setOverlay();
                                label.setText("GO");
//                                btn.setText("スタート");
                                btn.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        isStarted = true;
                                        label.setText("FIN");
                                        btn.setOnClickListener(new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                googleMap.snapshot(new SnapshotReadyCallback() {
                                                    @Override
                                                    public void onSnapshotReady(final Bitmap bitmap) {
                                                        ImageView im = new ImageView(getApplicationContext());
                                                        im.setImageBitmap(bitmap);
                                                        new Builder(MapActivity.this)
                                                                .setTitle("結果")
                                                                .setView(im)
                                                                .setPositiveButton("シェア", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        Intent intent = new Intent(Intent.ACTION_SEND);
                                                                        intent.setType("image/jpeg");
                                                                        intent.putExtra(Intent.EXTRA_STREAM, getImageUri(getApplicationContext(), bitmap));
                                                                        startActivity(intent);
                                                                    }
                                                                })
                                                                .show();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        }).setNegativeButton("キャンセル", null).show();
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void requestLocationUpdates() {
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        String providerName = isNetworkEnabled ? LocationManager.NETWORK_PROVIDER : LocationManager.GPS_PROVIDER;
        locationManager.requestLocationUpdates(
                providerName,
                LOCATION_UPDATE_MIN_TIME,
                LOCATION_UPDATE_MIN_DISTANCE,
                this
        );
    }

    @Override
    public void onLocationChanged(Location location) {
        if (googleMap == null) {
            return;
        }

        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }

//        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM_LEVEL));
        if (circle != null) {
            circle.remove();
        }
        circle = googleMap.addCircle(new CircleOptions()
                .center(position)
                .radius(5)
                .strokeColor(getResources().getColor(android.R.color.holo_green_dark))
                .fillColor(getResources().getColor(android.R.color.holo_green_dark)));


        if (isStarted) {
            return;
        }

        googleMap.addCircle(
                new CircleOptions()
                        .center(position)
                        .fillColor(getResources().getColor(android.R.color.holo_blue_bright))
                        .strokeColor(getResources().getColor(android.R.color.holo_blue_bright))
                        .radius(3)
        );
        if (prevPosition != null) {
            googleMap.addPolyline(
                    new PolylineOptions()
                            .add(prevPosition)
                            .add(position)
                            .color(getResources().getColor(android.R.color.holo_orange_dark))
                            .width(3)
            );
        }
        prevPosition = position;
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {  }

    @Override
    public void onProviderEnabled(String provider) {  }

    @Override
    public void onProviderDisabled(String provider) {  }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapClickListener(this);
//        LatLng latLng = new LatLng(35.648911, 139.702034);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));

//        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    private void setOverlay() {
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
                        .bearing(cameraPosition.bearing)
                        .image(BitmapDescriptorFactory.fromResource(R.drawable.smile))
//                        .image(BitmapDescriptorFactory.fromBitmap(bitmap))
        );

        findViewById(R.id.image).setVisibility(View.GONE);
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
