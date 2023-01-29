package com.example.probaapiapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProbaActivity extends AppCompatActivity {

    private String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    private TextView reciever;
    private TabLayout tabLayout;
    private FrameLayout simpleFrameLayout;
    private RadioGroup radioGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proba_main);

        radioGroup = findViewById(R.id.radioGroup);


        Intent intent = getIntent();
        String tomorrow = intent.getStringExtra("tomorrow");

        reciever = findViewById(R.id.received_value_id);
        String source = intent.getStringExtra("name");
        reciever.setText(source);

        simpleFrameLayout = (FrameLayout) findViewById(R.id.simpleFrameLayout);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        TabLayout.Tab firstTab = tabLayout.newTab();
        firstTab.setText("today");
        tabLayout.addTab(firstTab);

        TabLayout.Tab secondTab = tabLayout.newTab();
        secondTab.setText("tomorrow");
        tabLayout.addTab(secondTab);

        Integer idOfMensa = intent.getIntExtra("id", 0);
        final String[] date = new String[1];
        date[0]=currentDate;

        setTypeOfMenu(currentDate, intent);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        setTypeOfMenu(currentDate, intent);
                        date[0] =currentDate;
                        break;
                    case 1:
                        setTypeOfMenu(tomorrow, intent);
                        date[0] =tomorrow;
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                Toast.makeText(ProbaActivity.this, "Selected Radio Button is : " + radioButton.getText(), Toast.LENGTH_SHORT).show();
                radioButton.setOnClickListener(
                        v -> {
                            String source = radioButton.getText().toString();
                            Intent intent = new Intent(getApplicationContext(), MatchingActivity.class);
                            intent.putExtra("regime", source);
                            intent.putExtra("date", date[0]);
                            intent.putExtra("mensaId", idOfMensa);
                            startActivity(intent);
                        });
            }
        });


    }

    private void setTypeOfMenu(String date, Intent intent) {
        LinearLayout layout = findViewById(R.id.layoutInner);
        layout.removeAllViews();

        Integer idOfMensa = intent.getIntExtra("id", 0);

        String url = "https://mensatreff-api.spyfly.xyz/mensas/" + idOfMensa + "?date=" + date;
        StringRequest request = new StringRequest(
                Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray array = obj.getJSONArray("response");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject tempObj = array.getJSONObject(i);
                        TextView tempView = new TextView(ProbaActivity.this);
                        JSONObject price = tempObj.getJSONObject("prices");
                        tempView.setText(Html.fromHtml(
                                (i + 1) +
                                        "   <i>name:</i> " +
                                        tempObj.getString("name") + "<br>" +
                                        "<b>prices:</b><br>" + "for students: " + price.getString("Studierende") + "<br>" +
                                        "for employee: " + price.getString("Bedienstete") + "<br>"
                        ));
                        ImageView imageView = new ImageView(ProbaActivity.this);
                        String imageUrl = tempObj.getString("image");
                        Picasso.get().load("https:" + imageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(imageView);

                        layout.addView(imageView);
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
