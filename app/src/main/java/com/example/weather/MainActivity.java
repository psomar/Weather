package com.example.weather;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private final String URL_WEATHER = "http://api.openweathermap.org/data/2.5/weather?q=%s&appid=40ee66fac4e676e53e1401bc8874d4cf&units=metric&lang=ru";

    private EditText editTextFindCity;
    private TextView textViewShowInfoOfWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextFindCity = findViewById(R.id.editTextFindCity);
        textViewShowInfoOfWeather = findViewById(R.id.textViewInfoOfWeather);
    }

    public void onClickShowWeather(View view) {
        String city = editTextFindCity.getText().toString().trim();
        if (!city.isEmpty()) {
            DownloadJSONTask task = new DownloadJSONTask();
            String url = String.format(URL_WEATHER, city);
            task.execute(url);
        }
    }

    private class DownloadJSONTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String city = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String weather = String.format("%s\nТемпература: %s\nНа улице: %s", city, temp, description);
                textViewShowInfoOfWeather.setText(weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}