package com.sakada.whatstheweather;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.media.AsyncPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = (EditText)findViewById(R.id.cityName);
        resultText = (TextView)findViewById(R.id.resultText);

    }

    public void findWeather(View view){
        Log.i("City Name", cityName.getText().toString());

//        Hide Keyboard;
        InputMethodManager mgr = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" +
                    encodedCityName +
                    "&appid=44db6a862fba0b067b1930da0d769e98");

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
        }


    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            try {

                String result = "";
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try{

                String message = "";

                JSONObject jsonObject = new JSONObject(result);
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather Content", result);
                JSONArray arr = new JSONArray(weatherInfo);
                for (int i=0; i<arr.length(); i++){
                    JSONObject jsonPath = arr.getJSONObject(i);

//                    Log.i("main", jsonPath.getString("main"));
//                    Log.i("description", jsonPath.getString("description"));
                    String main = "";
                    String description = "";

                    main = jsonPath.getString("main");
                    description = jsonPath.getString("description");

                    if(main != "" && description != ""){
                        message += main + " : " + description + "\r\n";
                    }

                }
                if(message != ""){
                    resultText.setText(message);
                }else{
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
