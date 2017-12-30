package edu.umb.cs443;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;




public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        mFragment.getMapAsync(this);

        setClick();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap = map;

    }

    public void setClick() {

        Button b = (Button) findViewById(R.id.button);
        final EditText e = (EditText) findViewById(R.id.editText);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String weather = e.getText().toString();
                        try {
                            int x = Integer.valueOf(weather);
                            setWeather("zip=" + weather);
                        } catch (Exception e) {
                            setWeather("q=" + weather);
                        }
                    }
                }).start();
            }
        });
    }

    public void setWeather(String middle) {
        TextView textView = (TextView) findViewById(R.id.textView);

        String start = "http://api.openweathermap.org/data/2.5/weather?";
        String end = "&APPID=f9a0da7858696d1453d0faa23006c2d9";
        String target = start + middle + end;

        InputStream stream = null;
        String result;


        try {
            URL url = new URL(target);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();

            stream = request.getInputStream();
            result = readStream(stream, 1500);

            JSONObject js = new JSONObject(result);

            double lon = js.getJSONObject("coord").getDouble("lon");
            double lat = js.getJSONObject("coord").getDouble("lat");
            double temp = js.getJSONObject("main").getDouble("temp");

            String finTemp = String.valueOf((int) (temp - 273.15));

            //Gets the file path for the png file
            JSONArray array = js.getJSONArray("weather");
            String path = array.getJSONObject(0).getString("icon");

            setImage(path);

            try {
                setMap(lat, lon);
            } catch (Exception e) {
            }

            textView.setText(finTemp + "C");

        } catch (Exception e) {

        }


    }

    public void setImage(String path){
        InputStream streamPng = null;
        ImageView img = (ImageView) findViewById(R.id.imageView);


        try {
            URL urlpng = new URL("http://openweathermap.org/img/w/" + path + ".png");
            HttpURLConnection requestPng = (HttpURLConnection) urlpng.openConnection();
            requestPng.connect();

            streamPng = requestPng.getInputStream();
            Bitmap bm = BitmapFactory.decodeStream(streamPng);

            Drawable drawable = new BitmapDrawable(getResources(), bm);
            img.setBackground(drawable);


        }catch (Exception e){}


    }

    public void setMap(double lat, double lon) {

        final double l = lat;
        final double ll = lon;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(l, ll), 10));
            }
        });


    }

    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        // Read InputStream using the UTF-8 charset.
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        // Create temporary buffer to hold Stream data with specified max length.
        char[] buffer = new char[maxLength];
        // Populate temporary buffer with Stream data.
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            int pct = (100 * numChars) / maxLength;
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            // The stream was not empty.
            // Create String that is actual length of response body if actual length was less than
            // max length.
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        return result;
    }

}




