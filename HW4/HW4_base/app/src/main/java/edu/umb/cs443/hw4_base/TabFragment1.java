package edu.umb.cs443.hw4_base;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class TabFragment1 extends Fragment implements OnMapReadyCallback {

    public GoogleMap mMap;
    public FrameLayout mMapFrame;
    public MapFragment mFragment;
    private static final int REQUEST_FINE_LOCATION = 11;
    private RadioButton mRadioC, mRadioG, mRadioR;
    private RadioGroup group;
    private Button mClear, mGo;
    private EditText mRadius;
    public ListItems commonPlaces, places;
    public double  mLatitude = 42.3137398, mLongitude = -71.0386802;
    private int numberOfPlaces = 0;
    private ValueAnimator vAnimator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_1, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        //commonPlaces = new ListItems();
        places = new ListItems();
        initialiseCommonPlaces();
        mClear = (Button) getActivity().findViewById(R.id.clear);
        mGo = (Button) getActivity().findViewById(R.id.go);
        mGo.setVisibility(View.GONE);
        mRadius = (EditText) getActivity().findViewById(R.id.radius);
        mRadius.setVisibility(View.GONE);
        mRadioC = (RadioButton) getActivity().findViewById(R.id.coordinate);
        mRadioC.setChecked(true);
        mRadioG = (RadioButton) getActivity().findViewById(R.id.geofencing);
        mRadioR = (RadioButton) getActivity().findViewById(R.id.region);
        group = (RadioGroup) getActivity().findViewById(R.id.group1);
        mMapFrame= (FrameLayout) getActivity().findViewById(R.id.mapframe);
        mFragment = ((MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map));
        mFragment.getMapAsync(this);

        // On clicked Radios
        group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.coordinate:
                        // Todo Add your code for default post
                        mRadioG.setChecked(false);
                        mRadioR.setChecked(false);
                        mMap.clear();
                        mGo.setVisibility(View.GONE);
                        mRadius.setVisibility(View.GONE);
                        places = new ListItems();
                        initialiseCommonPlaces();
                        break;
                    case R.id.geofencing:
                        // Todo Add your method for GeoFencing post
                        mRadioC.setChecked(false);
                        mRadioR.setChecked(false);
                        places = new ListItems();
                        initialiseCommonPlaces();
                        mGo.setVisibility(View.VISIBLE);
                        mRadius.setVisibility(View.VISIBLE);
                        //Set radius
                        mGo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick (View v){
                                if (mRadioG.isChecked()&&!mRadius.getText().toString().equals(""))
                                    addBoundary();
                            }
                        });
                        mMap.clear();
                        //addBoundary();
                        break;
                    case R.id.region:
                        // Todo Add your method for Region post
                        mRadioG.setChecked(false);
                        mRadioC.setChecked(false);
                        places = new ListItems();
                        initialiseCommonPlaces();
                        mGo.setVisibility(View.VISIBLE);
                        mRadius.setVisibility(View.VISIBLE);
                        mRadius.setText("");
                        mMap.clear();
                        break;
                }
            }
        });

        // Onclicked Clear button
        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                clear();
            }
        });

    }
    //Add a circle near JFK/Umass
    public void addBoundary (){
        final Circle circle1 = mMap.addCircle(new CircleOptions()
                .center(new LatLng(42.3206049,-71.0523659))
                .radius(Integer.parseInt(mRadius.getText().toString()))
                .strokeColor(Color.RED)
                .fillColor(Color.argb(90,255,0,0))
                .strokeWidth(0));
        final Circle circle2 = mMap.addCircle(new CircleOptions()
                .center(new LatLng(42.3345334,-71.0731477))
                .radius(Integer.parseInt(mRadius.getText().toString()))
                .strokeColor(Color.RED)
                .fillColor(Color.argb(90,255,0,0))
                .strokeWidth(0));

        vAnimator = new ValueAnimator();
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.REVERSE);  /* PULSE */
        vAnimator.setIntValues(600, 800);
        vAnimator.setDuration(1300);
        vAnimator.setEvaluator(new IntEvaluator());
        vAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                // Log.e("", "" + animatedFraction);
                //circle1.setRadius(animatedFraction * 1000);
                circle1.setFillColor(Color.argb((int)(animatedFraction * 80 + 30) ,200,0,0));
                circle2.setFillColor(Color.argb((int)(animatedFraction * 80 + 30) ,200,0,0));
            }
        });
        vAnimator.start();
    }

    // Enable Region, set a random point inside a circle
    public LatLng regionRandom(LatLng center, int radius){

        System.out.println("Calculating Region");
        double y0 = center.latitude;
        double x0 = center.longitude;
        double rd = (double) radius / 111300;
        double u = Math.random();
        double v =  Math.random();

        double w = rd * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);
        LatLng rand = new LatLng(y+y0,x+x0);
        System.out.println("Region Lat Long: "+ rand.latitude+" "+ rand.longitude);
        System.out.println("Center Lat Long: "+ center.latitude+" "+ center.longitude);
        return rand;
    }

    private void initialiseCommonPlaces (){
        commonPlaces = new ListItems();
        Item stMarket, sBayCenter, bMedicalCenter, bCHSchool,jFKUmass, andrewSt, jordanHall, mFineArt;

        stMarket = new Item();
        stMarket.setStatus("I'm here in StarMarket");
        stMarket.setLat(42.3184194);
        stMarket.setLng(-71.0507056);
        commonPlaces.items.add(stMarket);

        sBayCenter = new Item();
        sBayCenter.setStatus("I'm here in South Bay Center");
        sBayCenter.setLat(42.3277698);
        sBayCenter.setLng(-71.0629418);
        commonPlaces.items.add(sBayCenter);

        jFKUmass = new Item();
        jFKUmass.setStatus("I'm here at JFK/UMASS station");
        jFKUmass.setLat(42.3206049);
        jFKUmass.setLng(-71.0523659);
        commonPlaces.items.add(jFKUmass);

        bCHSchool = new Item();
        bCHSchool.setStatus("I'm at Boston College High School  station");
        bCHSchool.setLat(42.316091);
        bCHSchool.setLng(-71.046685);
        commonPlaces.items.add(bCHSchool);

        bMedicalCenter = new Item();
        bMedicalCenter.setStatus("I'm here in Boston Medical Center");
        bMedicalCenter.setLat(42.3345334);
        bMedicalCenter.setLng(-71.0731477);
        commonPlaces.items.add(bMedicalCenter);

        jordanHall = new Item();
        jordanHall.setStatus("I'm in Jordan Hall Station");
        jordanHall.setLat(42.3408699);
        jordanHall.setLng(-71.0861617);
        commonPlaces.items.add(jordanHall);

        mFineArt = new Item();
        mFineArt.setStatus("I'm in Museum of Fine Art");
        mFineArt.setLat(42.339381);
        mFineArt.setLng(-71.0940474);
        commonPlaces.items.add(mFineArt);

        numberOfPlaces = commonPlaces.items.size();
        System.out.println("First places and commonPlaces "+places.items.size() + " " + commonPlaces.items.size());
    }

    /*** START THE MAP ***/
    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap = map;
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        try {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLatitude,mLongitude))
                    .zoom(15).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }catch (NullPointerException e){e.printStackTrace();}
        // CLICKED ON MAP
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Toast.makeText(getContext(), "Coordinate - lat: " + latLng.latitude + ", long: " +latLng.longitude ,
                        Toast.LENGTH_LONG).show();
            }
        });
        // CURRENT LOCATION CHANGED
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location latLng) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(latLng.getLatitude(),latLng.getLongitude()))
                        .zoom(15).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                // Check places
                for (Iterator<Item> i = commonPlaces.items.iterator(); i.hasNext();){
                    Item iItem = i.next();
                    Location loc1 = new Location("");
                    loc1.setLatitude(iItem.getLat());
                    loc1.setLongitude(iItem.getLng());

                    //If near common places, post a status
                    if(loc1.distanceTo(latLng) < 400.0) {
                        iItem.setTime(System.currentTimeMillis() / 1000L);
                        //Post new status
                        if(places.items.size() < numberOfPlaces) {
                            //Check if GeoFence
                            if(mRadioG.isChecked() && !mRadius.getText().toString().equals("")) {
                                int radius = Integer.parseInt(mRadius.getText().toString());
                                Location loc2 = new Location("");
                                loc2.setLatitude(42.3206049);
                                loc2.setLongitude(-71.0523659);
                                Location loc3 = new Location("");
                                loc3.setLatitude(42.3345334);
                                loc3.setLongitude(-71.0731477);
                                System.out.println(loc1.distanceTo(loc2));
                                System.out.println(loc1.distanceTo(loc3));
                                System.out.println((double) radius);
                                if(loc1.distanceTo(loc2) > (double) radius && loc1.distanceTo(loc3) > (double) radius){
                                    places.items.add(iItem);
                                    Toast.makeText(getContext(), "New status posted: " + iItem.getStatus()
                                                    + "  lat: " + iItem.getLat() + ", long: " + iItem.getLng(),
                                            Toast.LENGTH_LONG).show();
                                }else {
                                    Toast.makeText(getContext(), "Can't update this status : "
                                                    + "  lat: " + iItem.getLat() + ", long: " + iItem.getLng(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }else if(mRadioR.isChecked() && !mRadius.getText().toString().equals("")) {
                                LatLng center = new LatLng(iItem.getLat(), iItem.getLng());
                                LatLng point = regionRandom(center,Integer.parseInt(mRadius.getText().toString()));
                                iItem.setLat(point.latitude);
                                iItem.setLng(point.longitude);
                                iItem.setRegion(true);
                                iItem.setRadius(Integer.parseInt(mRadius.getText().toString()));
                                places.items.add(iItem);
                                Toast.makeText(getContext(), "New status posted: " + iItem.getStatus()
                                                + "  lat: " + iItem.getLat() + ", long: " + iItem.getLng(),
                                        Toast.LENGTH_LONG).show();
                            }else {
                                // Coordinate checked
                                places.items.add(iItem);
                                Toast.makeText(getContext(), "New status posted: " + iItem.getStatus()
                                                + "  lat: " + iItem.getLat() + ", long: " + iItem.getLng(),
                                        Toast.LENGTH_LONG).show();
                            }
                            // Write new status to file -- Download/data.txt --
                            JSONArray jsonArray = new JSONArray();
                            if (!places.items.isEmpty()) {
                                for (Iterator<Item> k = places.items.iterator(); k.hasNext(); ) {
                                    JSONObject obj = new JSONObject();
                                    Item iItem1 = k.next();
                                    try {
                                        obj.put("status", iItem1.getStatus());
                                        obj.put("time", iItem1.getTime());
                                        //System.out.println("Time: " + iItem.getTime());
                                        obj.put("lat", iItem1.getLat());
                                        obj.put("lng", iItem1.getLng());
                                        if(mRadioR.isChecked() && !mRadius.getText().toString().equals("")){
                                            obj.put("region",iItem.getRegion());
                                            obj.put("radius",iItem.getRadius());
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    jsonArray.put(obj);
                                }
                                String tmp = jsonArray.toString();
                                places.writeData(tmp);
                            }
                            i.remove();
                        }
                        break;
                    }
                }
                System.out.println("places and commonPlaces "+places.items.size() + " " + commonPlaces.items.size());
                if(commonPlaces.items.size() == 0){
                    initialiseCommonPlaces();
                 }

            }
        });
    }

    void clear(){
        places = new ListItems();
        initialiseCommonPlaces();
        mMap.clear();
    }
}
