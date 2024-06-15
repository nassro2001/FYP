package com.example.FinalProject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

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
import com.example.FinalProject.classes.PT;
import com.example.FinalProject.classes.SessionManagerPt;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilePtActivity extends AppCompatActivity {

    private int idPT;
    private TextView username, specialty, sex;
    CircleImageView profile;
    private static final String TAG = ProfileActivity.class.getName();
    SessionManagerPt sessionManager;
    private PT pt;
    private Button updatePro, logOut;
    private FloatingActionButton editPicture, vcBtn;
    private final int GALLERY_REQ_CODE = 1000;
    private String URL = "http://172.26.9.32/Rest-API/API/getPtById.php?id=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pt);
        menuSelect();
        sessionManager = new SessionManagerPt(this);
        HashMap<String,Integer> user = sessionManager.getUserId();
        idPT =  user.get("IDPT");
        
        getPtDetails(idPT);

        vcBtn = findViewById(R.id.VCBtnPT);
        profile = findViewById(R.id.profilePic);
        editPicture = findViewById(R.id.edit);
        updatePro = findViewById(R.id.updatePro);
        logOut = findViewById(R.id.logOut);

        editPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQ_CODE);
            }
        });

        vcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),VideoCallPTActivity.class));
            }
        });

        updatePro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), UpdatePtActivity.class);
                intent.putExtra("username",pt.getUsername());
                intent.putExtra("DOB",pt.getDOB());
                intent.putExtra("password",pt.getPassword());
                intent.putExtra("sex",pt.getSex());
                intent.putExtra("specialty",pt.getSpecialty());
                intent.putExtra("id",pt.getId());
                startActivity(intent);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutPt();
            }
        });
    }

    private void logOutPt() {
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

    private void getPtDetails(int idPT) {

        username = findViewById(R.id.usernane);
        specialty = findViewById(R.id.specialty);
        sex = findViewById(R.id.sex);

        StringRequest request = new StringRequest(Request.Method.GET, URL + idPT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    pt = new PT();

                    pt.setId(jsonObject.getInt("idPT"));
                    pt.setUsername(jsonObject.getString("username"));
                    pt.setPassword(jsonObject.getString("password"));
                    pt.setDOB(jsonObject.getString("DOB"));
                    pt.setSex(jsonObject.getString("sex"));
                    pt.setSpecialty(jsonObject.getString("specialty"));

                    username.setText(pt.getUsername());
                    sex.setText(pt.getSex());
                    specialty.setText(pt.getSpecialty());


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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);


        //set calendar selected
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
                                CalendarPtActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;

            }
        });
    }
}