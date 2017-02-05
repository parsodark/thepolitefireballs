package com.politefireball.geolocation;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    final boolean RELEASE = true;
    TextView text;
    Button help;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        if(RELEASE)
        {
            TextView t = (TextView)findViewById(R.id.fuckthis);
            t.setVisibility(View.INVISIBLE);
        }

        fab.getRootView().setBackgroundColor(Color.parseColor("#dddddd"));


        RelativeLayout rl = (RelativeLayout) findViewById(R.id.content_main);
        help = new Button(this);
        help.setTextSize(20);
        help.setText("Help me!");
        help.setHeight(1000);
        help.setWidth(1000);
        help.setBackgroundColor(Color.parseColor("#ff0000"));
        help.setTextColor(Color.parseColor("#ffffff"));

        help.setOnTouchListener(new Button.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent arg1) {
                int action = arg1.getAction();

                if(action == MotionEvent.ACTION_DOWN) {

                    help.setBackgroundColor(Color.parseColor("#dd0000"));

                    return true;

                } else if (action == MotionEvent.ACTION_UP) {

                    // check logine functionality.
                    help.setBackgroundColor(Color.parseColor("#ff0000"));
                    help.setText("Waiting for help...");
                    autoUpdate();
                    return true;


                }

                return false;
            }
        });


        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 200,0,0);
        rl.addView(help, lp);


        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        /*if(ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            if(ContextCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_COARSE_LOCATION))
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }*/

        //ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  }, 0 );
        ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, 0 );
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, locationListener);

    }

    private LocationManager locationManager = null;
    private Location myLocation = null;

    private void makeUseOfNewLocation(Location location) {
        myLocation = location;
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

    private void postAlarm(int id, final double lon, final double lat, double alt, final int increm)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView t = (TextView)findViewById(R.id.fuckthis);
                // RelativeLayout rl = (RelativeLayout) findViewById(R.id.content_main);
                //text = new TextView(MainActivity.this);
                //t.setText("long : " + lon + " lat: " + lat + " increm: " + increm);
                //RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                       // RelativeLayout.LayoutParams.WRAP_CONTENT,
                       // RelativeLayout.LayoutParams.WRAP_CONTENT);
                //lp.setMargins(600, 50,0,0);
                //rl.addView(text, lp);
            }
        });

        String urlstr = "https://1hiutgaba7.execute-api.us-east-1.amazonaws.com/prod/SetAlarm";
        //String urlstr = "https://posttestserver.com/post.php";
        HashMap<String, String> postDataParams = new HashMap<String, String>();
        postDataParams.put("id", Integer.toString(id));
        postDataParams.put("lon", Double.toString(lon));
        postDataParams.put("lat", Double.toString(lat));
        String toSend = "{\"id\":" + (String)postDataParams.get("id") + ",\"longitude\":" + (String)postDataParams.get("lon") + ",\"latitude\":" + (String)postDataParams.get("lat") + "}";

        String resp = performPostCall(urlstr, toSend);
    }

    private String getAlarmAck(int id) {
        String urlstr = "https://1hiutgaba7.execute-api.us-east-1.amazonaws.com/prod/GetAlertStatus";

        String toSend = "{\"id\":" + Integer.toString(id) + "}";
        String resp = performPostCall(urlstr, toSend);
        return resp;
    }

    private String getGuards()
    {
        String urlstr = "https://1hiutgaba7.execute-api.us-east-1.amazonaws.com/prod/GetGuards";
        String resp = performGetCall(urlstr);
        return resp;
    }

    public String performPostCall(String requestURL,
                                   String toSend) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //String test = "test";
            //.setRequestProperty("Content-length", test.getBytes("UTF-8").length + "");

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            //writer.write(getPostDataString(postDataParams));


            //{"id":"123","longitude":70.7,"latitude":4.4}

            writer.write(toSend);
            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
            //response = Integer.toString(responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public String performGetCall(String requestURL) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public void autoUpdate()
    {
        boolean well = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!well) {
            TextView t = (TextView)findViewById(R.id.fuckthis);
            t.setText("rip");
        } else {
            TextView t = (TextView)findViewById(R.id.fuckthis);
            t.setText("good enough..");
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                int megaID = (int)(Math.random()*2147483647);
                //help.setText(Integer.toString(megaID));
                int x = 0;
                while (myLocation == null)
                {
                    try {
                    Thread.sleep(1000);
                    }
                    catch(InterruptedException e)
                    {
                    }
                }
                double lon = myLocation.getLongitude();
                double lat = myLocation.getLatitude();
                double alt = myLocation.getAltitude();
                try {
                    postAlarm(megaID, lon, lat, alt, x);
                } catch (Exception e) {
                }

                while(true)
                {
                    try {

                        final String alarmAck = getAlarmAck(megaID);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(alarmAck.equals("1")) {
                                    help.setBackgroundColor(Color.parseColor("#00ff00"));
                                    help.setTextColor(Color.parseColor("#000000"));
                                    help.setText("Help is on its way");
                                }
                            }
                        });
                        x += 1;
                    } catch (Exception e) {
                    }
                    try {
                        Thread.sleep(1000);
                    }
                    catch(InterruptedException e)
                    {
                    }

                }
            }
        }).start();
    }

}
