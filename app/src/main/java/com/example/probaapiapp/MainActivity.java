package com.example.probaapiapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView name = findViewById(R.id.name);

        Button button = findViewById(R.id.buttonReload);
        button.setOnClickListener(v -> {
            finish();
            startActivity(getIntent());
        });

        String url = "https://mensatreff-api.spyfly.xyz/mensas";
        JsonObjectRequest request = new JsonObjectRequest(
                url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                   // JSONObject obj = new JSONObject(response);
                    JSONArray array = response.getJSONArray("response");
                    Integer size = array.length();
                    JSONObject innerObj = array.getJSONObject(1);
                    name.setText(size.toString());

                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String tomorrow = currentDate;

                    LinearLayout layout = findViewById(R.id.layout);
                    for (int i = 0; i < array.length(); i++) {
                        LinearLayout innerLayout = new LinearLayout(MainActivity.this);
                        JSONObject currentObj = array.getJSONObject(i);

                        Button nameOfMensa = new Button(MainActivity.this);
                        TextView furtherInformationAboutMensa = new TextView(MainActivity.this);
                        TextView date = new TextView(MainActivity.this);
                        date.setText("today is closed");
                        JSONArray arrayDays = currentObj.getJSONArray("days");
                        for (int j = 0; j < arrayDays.length(); j++) {
                            if (arrayDays.getJSONObject(j).getString("date").equals(currentDate)) {
                                tomorrow = arrayDays.getJSONObject(j + 1).getString("date");
                                JSONObject today = arrayDays.getJSONObject(j);
                                if (today.getString("closed").equals("false")) {
                                    date.setText(Html.fromHtml("today is" + "<b>" + " opened" + "</b>" + "<br>"));
                                }
                            }
                        }
                        furtherInformationAboutMensa.setText(Html.fromHtml("<i>" + "Address: " + "</i>" + currentObj.getString("address")));
                        nameOfMensa.setText(currentObj.getString("name")); //may does not work
                        nameOfMensa.setId(currentObj.getInt("id"));
                        String finalTomorrow = tomorrow;
                        nameOfMensa.setOnClickListener(
                                v -> {
                                    String source = nameOfMensa.getText().toString();
                                    Integer idOfCurrentMensa = nameOfMensa.getId();
                                    Intent intent = new Intent(getApplicationContext(), ProbaActivity.class);
                                    intent.putExtra("name", source);
                                    intent.putExtra("id", idOfCurrentMensa);
                                    intent.putExtra("tomorrow", finalTomorrow);
                                    startActivity(intent);
                                });
                        layout.addView(nameOfMensa);
                        layout.addView(furtherInformationAboutMensa);
                        layout.addView(date);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                        Toast.makeText(MainActivity.this,
                                        error.getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                }
        ){
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}