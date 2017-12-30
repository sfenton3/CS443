package edu.umb.cs443.hw4_base;

/**
 * Created by nomad on 12/5/16.
 */

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class TabFragment2 extends Fragment implements OnMapReadyCallback {
    public GoogleMap mMap1;
    public FrameLayout mMapFrame;
    public MapFragment mFragment;
    private Button update;
    private ListItems places;
    private static final int REQUEST_FINE_LOCATION = 11;
    public double  mLatitude = 42.3137398, mLongitude = -71.0386802;

    private String mytag="MyTag";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_2, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapFrame = (FrameLayout) getActivity().findViewById(R.id.mapframe1);
        mFragment = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map1));
        mFragment.getMapAsync(this);
        update = (Button) getActivity().findViewById(R.id.update);
        //Update map
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                loadData(mMap1);
            }
        });
    }
    // Read data then add Makers and Polygons
    public void loadData(GoogleMap map){
        Log.i(mytag,"Loading data");
        places = new ListItems();
        String data = places.readData();
        JSONParser decodeData = new JSONParser();
        if(data != null) {
            try {
                places.items = decodeData.getData(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else Log.i(mytag,"no data");
        if(!places.items.isEmpty()){
            Log.i(mytag,"applying data");
            int k = 1;
            GoogleMap mMap = map;
            mMap1.clear();
            ArrayList<LatLng> pinPoints = new ArrayList<LatLng>();
            for (Iterator<Item> i = places.items.iterator(); i.hasNext(); ) {
                Item iItem = i.next();
                if(iItem.getRegion()){
                     Circle circle1 = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(iItem.getLat(), iItem.getLng()))
                            .radius(iItem.getRadius())
                            .strokeColor(Color.BLUE)
                            .fillColor(Color.argb(90,10,150,40))
                            .strokeWidth(3));
                }else  {
                    // adding marker
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(iItem.getLat(),
                            iItem.getLng())).title(k+". "+iItem.getStatus());
                    // Blue color icon marker
                    marker.icon(BitmapDescriptorFactory.defaultMarker
                            (BitmapDescriptorFactory.HUE_BLUE));
                    mMap1.addMarker(marker);
                    LatLng lL = new LatLng(iItem.getLat(), iItem.getLng());
                    pinPoints.add(lL);
                    k++;
                }
            }
            mMap1.addPolyline((new PolylineOptions()
                    .addAll(pinPoints)
                    .geodesic(true)
                    .width(4).color(Color.BLUE).geodesic(true)));
        } else Log.i(mytag, "can't apply data");

    }

    /*** START THE MAP ***/
    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap1 = map;
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return;
        }
        this.mMap1.setMyLocationEnabled(true);
        this.mMap1.getUiSettings().setZoomControlsEnabled(true);

        try {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLatitude,mLongitude))
                    .zoom(12).build();
            this.mMap1.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }catch (NullPointerException e){e.printStackTrace();}
        this.mMap1.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getContext(), "New Coordinate updated",
                        Toast.LENGTH_LONG).show();
            }
        });
        loadData(this.mMap1);
    }

}
