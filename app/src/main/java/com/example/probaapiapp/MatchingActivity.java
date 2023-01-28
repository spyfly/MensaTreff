package com.example.probaapiapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MatchingActivity extends AppCompatActivity {

    private EditText userName;
    private Button postBtn;
    private TextView responseData;
    private ProgressBar loadingPB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching_main);


        Intent intent = getIntent();
        String regime = intent.getStringExtra("regime");

        userName=findViewById(R.id.userName);
        postBtn=findViewById(R.id.getName);
        responseData=findViewById(R.id.idTVResponse);
        loadingPB=findViewById(R.id.idLoadingPB);

        postBtn.setText(regime);

        if(regime.equals("registration")){
            postBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(userName.getText().toString().isEmpty()){
                        Toast.makeText(MatchingActivity.this, "Please enter user name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    postUserName(userName.getText().toString());
                }
            });
        } else {

        }

    }

    private void postUserName(String name) {
        String url="https://mensatreff-api.spyfly.xyz/user";
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
