package com.example.probaapiapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView name = findViewById(R.id.name);

        String url = "https://mensatreff-api.spyfly.xyz/mensas";
        StringRequest request = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray array = object.getJSONArray("response");
                    Integer size = array.length();
                    JSONObject innerObj = array.getJSONObject(1);
                    name.setText(size.toString());

                    String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String tomorrow=currentDate;

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
                                tomorrow= arrayDays.getJSONObject(j+1).getString("date");
                                JSONObject today = arrayDays.getJSONObject(j);
                                if(today.getString("closed").equals("false")){
                                    date.setText(Html.fromHtml("today is"+"<b>"+" opened"+"</b>" + "<br>"));
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
                        Toast.makeText(MainActivity.this,
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