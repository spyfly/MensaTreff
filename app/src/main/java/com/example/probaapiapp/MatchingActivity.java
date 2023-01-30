package com.example.probaapiapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MatchingActivity extends AppCompatActivity {

    private EditText userName;
    private EditText loginPass;
    private Button postBtn;
    private Button loginBtn;
    private TextView responseData;
    private ProgressBar loadingPB;
    private LinearLayout layoutMatching;
    private LinearLayout layoutLogin;
    private LinearLayout layoutTimeslot;
    private Button addTimeslotsBtn;

    private CheckBox checkBox3;
    private CheckBox checkBox4;
    private CheckBox checkBox5;
    private CheckBox checkBox6;
    private CheckBox checkBox7;
    private CheckBox checkBox8;
    private CheckBox checkBox9;
    private CheckBox checkBox10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching_main);

        Intent intent = getIntent();
        String regime = intent.getStringExtra("regime");
        Integer mensaId = intent.getIntExtra("mensaId", 0);
        String date = intent.getStringExtra("date");

        userName = findViewById(R.id.userName);
        postBtn = findViewById(R.id.getName);
        responseData = findViewById(R.id.idTVResponse);
        loadingPB = findViewById(R.id.idLoadingPB);
        layoutMatching = findViewById(R.id.layoutMatching);
        layoutLogin = findViewById(R.id.layoutLogin);
        loginPass = findViewById(R.id.loginPass);
        loginBtn = findViewById(R.id.loginBtn);
        layoutTimeslot = findViewById(R.id.layoutTimeslot);
        addTimeslotsBtn = findViewById(R.id.addTimeslotsBtn);
        checkBox3 = findViewById(R.id.checkbox3);
        checkBox4 = findViewById(R.id.checkbox4);
        checkBox5 = findViewById(R.id.checkbox5);
        checkBox6 = findViewById(R.id.checkbox6);
        checkBox7 = findViewById(R.id.checkbox7);
        checkBox8 = findViewById(R.id.checkbox8);
        checkBox9 = findViewById(R.id.checkbox9);
        checkBox10 = findViewById(R.id.checkbox10);

        if (regime.equals("registration")) {
            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userName.getText().toString().isEmpty()) {
                        Toast.makeText(MatchingActivity.this, "Please enter user name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    postUserName(userName.getText().toString());
                    layoutMatching.setVisibility(View.GONE);
                    layoutLogin.setVisibility(View.VISIBLE);
                }
            });
        } else {
            layoutMatching.setVisibility(View.GONE);
            layoutLogin.setVisibility(View.VISIBLE);
        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginPass.getText().toString().isEmpty()) {
                    Toast.makeText(MatchingActivity.this, "Please enter passkey", Toast.LENGTH_SHORT).show();
                    return;
                }
                login(loginPass.getText().toString(), mensaId.toString(), date);
            }
        });
    }

    private void login(String passkey, String mensaId, String date) {
        String url = "https://mensatreff-api.spyfly.xyz/match/" + mensaId + "/" + date;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                layoutLogin.setVisibility(View.GONE);
                layoutTimeslot.setVisibility(View.VISIBLE);
                addTimeslotsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addTimeslots(v, passkey, mensaId, date);
                        Intent intent = new Intent(getApplicationContext(), OverviewActivity.class);
                        intent.putExtra("date", date);
                        intent.putExtra("mensaId", mensaId);
                        intent.putExtra("passkey", passkey);
                        startActivity(intent);
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MatchingActivity.this,
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

    private void addTimeslots(View v, String passkey, String mensaId, String date) {
        if (checkBox3.isChecked()) {
            postTimeslots(checkBox3.getText().toString(), passkey, mensaId, date);
        }
        if (checkBox4.isChecked()) {
            postTimeslots(checkBox4.getText().toString(), passkey, mensaId, date);
        }
        if (checkBox5.isChecked()) {
            postTimeslots(checkBox5.getText().toString(), passkey, mensaId, date);
        }
        if (checkBox6.isChecked()) {
            postTimeslots(checkBox6.getText().toString(), passkey, mensaId, date);
        }
        if (checkBox7.isChecked()) {
            postTimeslots(checkBox7.getText().toString(), passkey, mensaId, date);
        }
        if (checkBox8.isChecked()) {
            postTimeslots(checkBox8.getText().toString(), passkey, mensaId, date);
        }
        if (checkBox9.isChecked()) {
            postTimeslots(checkBox9.getText().toString(), passkey, mensaId, date);
        }
        if (checkBox10.isChecked()) {
            postTimeslots(checkBox10.getText().toString(), passkey, mensaId, date);
        }
    }

    private void postTimeslots(String timeslot, String passkey, String mensaId, String date) {
        String url = "https://mensatreff-api.spyfly.xyz/match/" + mensaId + "/" + date + "/" + timeslot;
        RequestQueue queue = Volley.newRequestQueue(MatchingActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MatchingActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MatchingActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + passkey);
                return params;
            }
        };
        queue.add(request);
    }

    private void postUserName(String name) {
        String url = "https://mensatreff-api.spyfly.xyz/user";
        loadingPB.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(MatchingActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingPB.setVisibility(View.GONE);
                userName.setText("");
                Toast.makeText(MatchingActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject respObj = new JSONObject(response);

                    String name = respObj.getString("username");
                    String password = respObj.getString("passkey");

                    responseData.setText("Name : " + name + "\npasskey : " + password + "\n PLEASE save the passkey!!!!!!!!");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MatchingActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", name);
                return params;
            }
        };
        queue.add(request);
    }
}
