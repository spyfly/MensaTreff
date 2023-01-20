package com.example.probaapiapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProbaActivity extends AppCompatActivity {

    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    TextView reciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proba_main);

        reciever = findViewById(R.id.received_value_id);
        Intent intent = getIntent();
        Integer idOfMensa = intent.getIntExtra("id", 0);
        String source = intent.getStringExtra("name");
        reciever.setText(source);

        String url = "https://mensatreff-api.spyfly.xyz/mensas/" + idOfMensa + "?date=" + currentDate;
        //System.out.println("\n!!!!!!!!!!!!!!!!!!!" + url);
        StringRequest request = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray array = obj.getJSONArray("response");

                    LinearLayout layout = findViewById(R.id.layoutProba);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject tempObj = array.getJSONObject(i);
//                        ImageView image = new ImageView(ProbaActivity.this);
//
//                        Picasso
//                                .with(ProbaActivity.this)
//                                .load("https:"+tempObj
//                                        .getString("image"))
//                                .into(image);
//                        //image.setMaxWidth(10);
//                        layout.addView(image);

                        /*InputStream is = (InputStream) new URL(tempObj.getJSONObject("image").toString()).getContent();
                        Drawable d = Drawable.createFromStream(is, "src name");
                        image.setImageDrawable(d);
                        layout.addView(image);
    */

                        TextView tempView = new TextView(ProbaActivity.this);
                        JSONObject price = tempObj.getJSONObject("prices");
                        tempView.setText(Html.fromHtml(
                                (i + 1) +
                                        "   <i>name:</i> " +
                                        tempObj.getString("name") + "<br>" +
                                        "<b>prices:</b><br>" + "for students: " + price.getString("Studierende") + "<br>" +
                                        "for employee: " + price.getString("Bedienstete") + "<br>"
                        ));

                        layout.addView(tempView);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ProbaActivity.this,
                                error.getMessage(),
                                Toast.LENGTH_SHORT)
                        .show();
            }
        }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}
