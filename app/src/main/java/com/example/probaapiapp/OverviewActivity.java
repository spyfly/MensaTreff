package com.example.probaapiapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OverviewActivity extends AppCompatActivity {

    private TextView textView;
    private LinearLayout layoutOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overview_main);

        textView = findViewById(R.id.output);
        layoutOverview = findViewById(R.id.layoutOverview);

        Intent intent = getIntent();
        String date = intent.getStringExtra("date");
        String mensaId = intent.getStringExtra("mensaId");
        String passkey = intent.getStringExtra("passkey");

        StringBuilder src = new StringBuilder();
        String url = "https://mensatreff-api.spyfly.xyz/match/" + mensaId + "/" + date;
        JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //todo: STOPPED HERE   show matches for chosen timeslots with list of the participants(add to array and print), caching
                try {
                    JSONObject timeslots = response.getJSONObject("timeslots");
                    Iterator<String> keys = timeslots.keys();

                    while (keys.hasNext()) {
                        String key = keys.next();
                        if (timeslots.get(key) instanceof JSONObject) {
                            // src.append(timeslots.getJSONObject(key).getString("participantCount")+" ");
                            if (timeslots.getJSONObject(key).getString("participating").equals("true")) {
                                TextView textView = new TextView(OverviewActivity.this);
                                textView.setText(Html.fromHtml("<b>" + key + "</b>\n" + timeslots.getJSONObject(key).getString("participantCount") + "\n"));
                                /*JSONArray array = timeslots.getJSONArray("participantNames");
                                StringBuilder builder = new StringBuilder();
                                for (int i = 0; i < array.length(); i++) {
                                    builder.append(array.getJSONObject(i) + "  ");

                                }
                                TextView textView1 = new TextView(OverviewActivity.this);
                                textView1.setText(builder);*/
                                layoutOverview.addView(textView);
//                                layoutOverview.addView(textView1);
                            }


                        }
                    }

                    textView.setText(src);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OverviewActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + passkey);
                return params;
            }

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
