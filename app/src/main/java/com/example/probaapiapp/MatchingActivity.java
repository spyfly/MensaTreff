package com.example.probaapiapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching_main);

        Intent intent = getIntent();
        String regime = intent.getStringExtra("regime");

        userName = findViewById(R.id.userName);
        postBtn = findViewById(R.id.getName);
        responseData = findViewById(R.id.idTVResponse);
        loadingPB = findViewById(R.id.idLoadingPB);
        layoutMatching = findViewById(R.id.layoutMatching);
        layoutLogin = findViewById(R.id.layoutLogin);
        loginPass = findViewById(R.id.loginPass);
        loginBtn = findViewById(R.id.loginBtn);

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
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loginPass.getText().toString().isEmpty()) {
                        Toast.makeText(MatchingActivity.this, "Please enter passkey", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    login(loginPass.getText().toString());
                }
            });
        }
    }

    private void login(String passkey) {
        String url = "https://mensatreff-api.spyfly.xyz/user";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String username = obj.getString("username");
                    responseData.setText("Name : " + username);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
