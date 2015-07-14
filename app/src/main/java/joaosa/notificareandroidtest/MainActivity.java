package joaosa.notificareandroidtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;

import android.support.v4.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeMap;

public class MainActivity extends FragmentActivity {

    private MainActivity thisActivity = this;
    private BroadcastReceiver broadcastReceiver;
    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpMapIfNeeded();
        IntentFilter intentFilter = new IntentFilter("restResult");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String result = intent.getStringExtra("result");
                try {
                    handleRestResult(new JSONObject(result));
                } catch (Exception e) {

                }
            }
        };
        this.registerReceiver(broadcastReceiver, intentFilter);
        RestAuthAsyncTask restAuthAsyncTask = new RestAuthAsyncTask(thisActivity);
        restAuthAsyncTask.execute("https://push.notifica.re/region");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        IntentFilter intentFilter = new IntentFilter("restResult");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String result = intent.getStringExtra("result");
                try {
                    handleRestResult(new JSONObject(result));
                } catch (Exception e) {

                }
            }
        };
        this.registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.broadcastReceiver);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (googleMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

    }

    public void handleRestResult(JSONObject result) {

        googleMap.clear();

        try {
            JSONArray regionsArray = result.getJSONArray("regions");

            ArrayList<Region> regionsList = new ArrayList<Region>();
            Region lastRegion = new Region ("", 0.0, new double[2]);

            double regionRadius;
            String regionName;
            double regionCoordinates[] = new double[2];

            for (int i = 0; i < regionsArray.length(); i++) {
                JSONObject regionObject = regionsArray.getJSONObject(i);
                regionRadius = Double.valueOf(regionObject.getString("distance"));
                regionName = regionObject.getString("name");
                JSONObject regionGeometryObject = regionObject.getJSONObject("geometry");
                JSONArray regionCoordinatesArray = regionGeometryObject.getJSONArray("coordinates");
                regionCoordinates = new double[2];
                regionCoordinates[0] = Double.valueOf(regionCoordinatesArray.get(0).toString());
                regionCoordinates[1] = Double.valueOf(regionCoordinatesArray.get(1).toString());
                regionsList.add(new Region(regionName, regionRadius, regionCoordinates));
                if(i == 0) {
                    lastRegion = new Region(regionName, regionRadius, regionCoordinates);
                }
            }

            double maxDistance = -1.0;
            double distance = 0.0;

            TreeMap<Double, ArrayList<Region>> kClosest = new TreeMap<Double, ArrayList<Region>>();

            int size = 0;

            for (int i = 0; i < regionsList.size(); i++) {
                distance = Math.sqrt((regionsList.get(i).getCoordinates()[0] - lastRegion.getCoordinates()[0]) * (regionsList.get(i).getCoordinates()[0] - lastRegion.getCoordinates()[0]) + (regionsList.get(i).getCoordinates()[1] - lastRegion.getCoordinates()[1]) * (regionsList.get(i).getCoordinates()[1] - lastRegion.getCoordinates()[1]));

                if (size < 5) {
                    if (kClosest.get(distance) == null) {
                        kClosest.put(distance, new ArrayList<Region>());
                    }

                    kClosest.get(distance).add(regionsList.get(i));
                    size++;

                    maxDistance = Math.max(distance, maxDistance);
                } else if (distance < maxDistance) {

                    kClosest.lastEntry().getValue().remove(0);
                    size--;

                    if (kClosest.lastEntry().getValue().isEmpty()) {
                        kClosest.remove(kClosest.lastEntry().getKey());
                    }

                    if (kClosest.get(distance) == null) {
                        kClosest.put(distance, new ArrayList<Region>());
                    }

                    kClosest.get(distance).add(regionsList.get(i));
                    size++;

                    maxDistance = kClosest.lastEntry().getKey();
                }
            }

            ArrayList<Region> finalRegionsList = new ArrayList<Region>();

            for (ArrayList<Region> kclosest : kClosest.values()) {
                finalRegionsList.addAll(kclosest);
            }

            for (int i = 0; i < finalRegionsList.size(); i++) {
                LatLng regionLatLng = new LatLng(finalRegionsList.get(i).getCoordinates()[1], finalRegionsList.get(i).getCoordinates()[0]);

                MarkerOptions markerOptions = new MarkerOptions().position(regionLatLng).title(finalRegionsList.get(i).getName()).snippet("Region");
                Marker marker = googleMap.addMarker(markerOptions);

                CircleOptions circleOptions = new CircleOptions().center(regionLatLng).radius(finalRegionsList.get(i).getRadius()).strokeColor(Color.BLACK).fillColor(0x55ff0000).strokeWidth(2);
                Circle circle = googleMap.addCircle(circleOptions);
            }

        } catch (Exception e) {

        }

    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
}
