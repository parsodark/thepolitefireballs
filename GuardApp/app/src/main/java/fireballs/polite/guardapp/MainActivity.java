package fireballs.polite.guardapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends FragmentActivity implements login.OnFragmentInteractionListener, SignIn.OnFragmentInteractionListener, SignUp.OnFragmentInteractionListener {

    Boolean peopleTouched = false;
    Button help;
    String urlstr = "https://1hiutgaba7.execute-api.us-east-1.amazonaws.com/prod/GetAlarms";
    String ackAlarm = "https://1hiutgaba7.execute-api.us-east-1.amazonaws.com/prod/AckAlert";
    HashMap<String, ImageButton> alarms = new HashMap<>();
    String username;
    String password;
    String fichierUtilisateurs = "utilisateurs.JSON";
    JSONArray JSONUtilisateurs = new JSONArray();
    HashMap<String,String> userMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fillMapWithJSON();

        //FragmentTransaction transaction = getFragmentManager().beginTransaction();

        //transaction.add(R.id.LoginFragment, log);
        //transaction.add(R.id.SignInFragment, signIn);
        //transaction.add(R.id.SignUpFragment, signUp);

        //transaction.commit();
        setContentView(R.layout.activity_main);
        setContentView(R.layout.fragment_login);
    }

    public void onClick(View arg0) throws JSONException {
        //sign up
        if (arg0.getTag().equals("button5"))
           setContentView(R.layout.fragment_sign_up);
        //sign in
        else if (arg0.getTag().equals("button4"))
            setContentView(R.layout.fragment_sign_in);
        // sign up confirmer
        else if (arg0.getTag().equals("button2")) {
            username = ((TextView) findViewById(R.id.usernameSignIn)).getText().toString();
            password = ((TextView) findViewById(R.id.passwordSignIn)).getText().toString();
            JSONObject obj = new JSONObject();
            obj.put("username", username);
            obj.put("password", password);
            JSONUtilisateurs.put(obj);
            mapToFile();
            setContentView(R.layout.activity_main);
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_main);
            if(rl != null) {
                help = new Button(this);
                help.setText("I'll help!");
                help.setBackgroundColor(Color.parseColor("#06e80a"));
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(600, 50, 0, 0);
                rl.addView(help, lp);
                help.setVisibility((View.INVISIBLE));
                Timer timer = new Timer();
                timer.schedule(new getAlarms(), 0, 5000);
            }
        }
        // sign in confirmer
        else if (arg0.getTag().equals("button1")) {
            username = ((TextView) findViewById(R.id.usernameSignUp)).getText().toString();
            password = ((TextView) findViewById(R.id.passwordSignUp)).getText().toString();
            if ((userMap.containsKey(username) && userMap.get(username).equals(password)) ||
                    (username.equals("Cotton") && password.equals("fireballs")))
            {
                setContentView(R.layout.activity_main);
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_main);
            if(rl != null) {
                help = new Button(this);
                help.setText("I'll help!");
                help.setBackgroundColor(Color.parseColor("#06e80a"));
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(600, 50, 0, 0);
                rl.addView(help, lp);
                help.setVisibility((View.INVISIBLE));
                Timer timer = new Timer();
                timer.schedule(new getAlarms(), 0, 5000);
            }
            }
        }

    }


    public void fillMapWithJSON()
    {
        JSONParser parser = new JSONParser();

        try {

            Object obj = parser.parse(new FileReader(
                    fichierUtilisateurs));

            JSONArray jsonObjects = (JSONArray) obj;

            for (int i = 0; i < jsonObjects.length(); i++)
            {
                JSONObject object = (JSONObject)jsonObjects.get(i);
                String username = object.getString("username");
                String password = object.getString("password");
                userMap.put(username,password);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mapToFile()
    {

        try (FileWriter file = new FileWriter(fichierUtilisateurs)) {
            file.write(JSONUtilisateurs.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    public void addRescuPoint(double lat, double lng, String description, int ack)
    {
        if (ack != 0) {
            if (alarms.containsKey(description) && alarms.get(description).getTag() != "#06e80a")
            {
                alarms.get(description).setEnabled(false);
                alarms.get(description).setVisibility(View.INVISIBLE);
                alarms.remove(description);
            }
        }
        else {
            ImageButton alarm;
            double pourcentX = (885688.64d * lng + 65200321.29d) / 1329d;
            double pourcentY = (-1198004.95d * lat + 54515344.40d) / 1233d;

            int width = findViewById(R.id.polyMap).getWidth();
            int height = findViewById(R.id.polyMap).getHeight();
            int positionx = findViewById(R.id.polyMap).getLeft();
            int positiony = findViewById(R.id.polyMap).getTop();

            RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_main);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            if (!alarms.containsKey(description)) {
                alarm = new ImageButton(this);
                alarm.setBackgroundColor(Color.TRANSPARENT);
                alarm.setColorFilter(Color.parseColor("#e80606"));
                alarm.setImageResource(R.drawable.cast_abc_scrubber_control_to_pressed_mtrl_000);
                alarm.setScaleX(0.9f);
                alarm.setScaleY(0.9f);
                alarm.setContentDescription(description);
                alarm.setOnTouchListener(new ImageButton.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent arg1) {
                        if (!peopleTouched && v.getTag() != "#06e80a") {
                            peopleTouched = true;
                            help.setVisibility(View.VISIBLE);
                            help.setContentDescription(v.getContentDescription());
                            help.setOnTouchListener(new Button.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent arg1) {
                                    v.setVisibility(View.INVISIBLE);
                                    peopleTouched = false;
                                    alarms.get(help.getContentDescription()).setColorFilter(Color.parseColor("#06e80a"));
                                    alarms.get(help.getContentDescription()).setTag("#06e80a");
                                    final String toSend = "{\"id\":" + help.getContentDescription() + "}";
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String response = performPostCall(ackAlarm, toSend);
                                        }
                                    }).start();

                                    return true;
                                }
                            });
                        }
                        return true;
                    }
                });

                int longueurx = alarm.getDrawable().getMinimumWidth();
                int longueury = alarm.getDrawable().getMinimumHeight();
                lp.setMargins((int) ((positionx + (width * pourcentX)) - (longueurx / 2)), (int) ((positiony + (height * pourcentY)) - (longueury / 2)), 0, 0);
                rl.addView(alarm, lp);
                alarms.put(description, alarm);
            } else {
                alarm = alarms.get(description);
                int longueurx = alarm.getDrawable().getMinimumWidth();
                int longueury = alarm.getDrawable().getMinimumHeight();
                lp.setMargins((int) ((positionx + (width * pourcentX)) - (longueurx / 2)), (int) ((positiony + (height * pourcentY)) - (longueury / 2)), 0, 0);
                alarm.setLayoutParams(lp);
            }
        }
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
                    response += line;
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
            response = Integer.toString(responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        SignUp details = new SignUp();
        details.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
    }


    class getAlarms extends TimerTask {
        public void run() {
            String response = performGetCall(urlstr);
            try{
            JSONArray alarmsJson = new JSONArray(response);
            for(int i = 0; i< alarmsJson.length(); i++)
            {
                JSONObject alarm = alarmsJson.getJSONObject(i);
                final String id = alarm.get("ID").toString();
                final double longitude = alarm.getDouble("longitude");
                final double latitude = alarm.getDouble("latitude");
                final int ack = alarm.getInt("ack");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addRescuPoint(latitude, longitude, id, ack);
                        }
                    });
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }
    }

}
