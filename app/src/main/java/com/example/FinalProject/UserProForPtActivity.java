package com.example.FinalProject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.FinalProject.classes.User;

import org.json.JSONException;
import org.json.JSONObject;

public class UserProForPtActivity extends AppCompatActivity {

    private int iduser;
    private User user;
    private String URL = "http://172.26.9.32/Rest-API/API/getUserById.php?id=";
    private static final String TAG = UserProForPtActivity.class.getName();
    private TextView username, sex, height, weight, bodyFat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_pro_for_pt);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            iduser = extras.getInt("idUser");
        }
        System.out.println(iduser);

        getUserDetails(iduser);
    }

    private void getUserDetails(int id){

        username = findViewById(R.id.username);
        sex = findViewById(R.id.sex);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        bodyFat = findViewById(R.id.bodyFat);

        StringRequest request = new StringRequest(Request.Method.GET, URL + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    user = new User();

                    user.setId(jsonObject.getInt("idUser"));
                    user.setUsername(jsonObject.getString("username"));
                    user.setEmail(jsonObject.getString("email"));
                    user.setDOB(jsonObject.getString("DOB"));
                    user.setPassword(jsonObject.getString("pass"));
                    user.setSex(jsonObject.getString("sex"));
                    user.setWeight(jsonObject.getInt("weight"));
                    user.setHeight(jsonObject.getInt("height"));
                    user.setBodyFat(jsonObject.getInt("bodyFat"));

                    username.setText(user.getUsername());
                    sex.setText(user.getSex());
                    weight.setText(String.valueOf(user.getWeight()));
                    height.setText(String.valueOf(user.getHeight()));
                    bodyFat.setText(String.valueOf(user.getBodyFat()));


                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG,error.getMessage());

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(request);

    }
}