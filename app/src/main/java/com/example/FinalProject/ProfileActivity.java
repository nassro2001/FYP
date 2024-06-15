package com.example.FinalProject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.FinalProject.classes.SessionManager;
import com.example.FinalProject.classes.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private final int GALLERY_REQ_CODE = 1000;
    private static final String TAG = ProfileActivity.class.getName();
    private TextView username, sex, height, weight, bodyFat;
    private Button settings,logOut;
    private FloatingActionButton editPicture,VCbtn;
    private int idUser;
    private User user;
    private String URL = "http://172.26.9.32/Rest-API/API/getUserById.php?id=";
    CircleImageView profile;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sessionManager = new SessionManager(this);
        menuSelect();
        HashMap<String,Integer> users = sessionManager.getUserId();
        idUser =  users.get("ID");

        getUserDetails(idUser);

        editPicture = findViewById(R.id.editProfile);
        VCbtn = findViewById(R.id.VCBtn);
        profile = findViewById(R.id.profilePic);
        logOut = findViewById(R.id.logOutUser);
        settings = findViewById(R.id.settings);

        editPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQ_CODE);
            }
        });

        VCbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),VideoCallActivity.class));
            }
        });
        
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),UpdateUserActivity.class);
                intent.putExtra("id",user.getId());
                intent.putExtra("username",user.getUsername());
                intent.putExtra("email",user.getEmail());
                intent.putExtra("password",user.getPassword());
                intent.putExtra("DOB",user.getDOB());
                intent.putExtra("sex",user.getSex());
                intent.putExtra("weight",user.getWeight());
                intent.putExtra("height",user.getHeight());
                intent.putExtra("BF",user.getBodyFat());
                startActivity(intent);

            }
        });

    }

    private void logOut() {

        sessionManager.removeSession();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_REQ_CODE){
                profile.setImageURI(data.getData());
            }
        }
    }
    private void getUserDetails(int id){

        username = findViewById(R.id.username);
        sex = findViewById(R.id.sex);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        bodyFat = findViewById(R.id.bodyFat);
        settings = findViewById(R.id.settings);

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


    private void menuSelect() {
        //initialize and assign variable

        bottomNavigationView = findViewById(R.id.bottomNav);

        //set profile selected
        bottomNavigationView.setSelectedItemId(R.id.profile);
        //perform itemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.profile:
                        return true;
                    case R.id.calendar:
                        startActivity(new Intent(getApplicationContext(),
                                CalendarActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),
                                HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;

            }
        });
    }
}