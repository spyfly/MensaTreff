package com.example.probaapiapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //todo: STOPPED HERE   show matches for chosen timeslots with list of the participants(add to array and print), caching
                try {
                    JSONObject object = new JSONObject(response);
                    JSONObject timeslots = object.getJSONObject("timeslots");
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
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}
