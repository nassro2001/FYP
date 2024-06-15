package com.example.FinalProject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.Locale;
import java.util.Map;

public class UpdatePtActivity extends AppCompatActivity {

    private EditText username,password,confirmPass,DOB,sex,specialty;
    private ProgressBar loading;
    private Button update;
    private int id;
    private static String URL = "http://172.26.9.32A/Rest-API/API/updatePT.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pt);

        loading = findViewById(R.id.loading);
        username = findViewById(R.id.UsernameInput);
        password = findViewById(R.id.PasswordInput);
        confirmPass = findViewById(R.id.ConfirmPassInput);
        DOB = findViewById(R.id.DateInput);
        sex = findViewById(R.id.SexInput);
        update = findViewById(R.id.Update);
        specialty = findViewById(R.id.SpecialtyInput);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            username.setText(extras.getString("username"));
            DOB.setText(extras.getString("DOB"));
            password.setText(extras.getString("password"));
            confirmPass.setText(extras.getString("password"));
            sex.setText(extras.getString("sex"));
            specialty.setText(extras.getString("specialty"));
            id = extras.getInt("id");
        }
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCredentials();
            }
        });
    }

    private void checkCredentials() {

        String usernameInput = username.getText().toString();
        String passwordInput = password.getText().toString();
        String confirmPassInput = confirmPass.getText().toString();
        String DOBInput = DOB.getText().toString();
        String sexInput = sex.getText().toString();
        String specialtyInput = specialty.getText().toString();

        if(usernameInput.isEmpty() || usernameInput.length()<3){
            showError(username,"Your username is not valid");
        }
        else if(passwordInput.isEmpty() || passwordInput.length()<4){
            showError(password,"Your password is not valid");
        }
        else if(confirmPassInput.isEmpty() || !confirmPassInput.equals(passwordInput)){
            showError(confirmPass,"Your confirmation password is not valid");
        }
        else if(DOBInput.isEmpty()){
            showError(DOB,"Your DOB is not valid");
        }
        else if (sexInput.isEmpty()){
            showError(sex,"Your Sex is not valid");
        }
        else if (specialtyInput.isEmpty()){
            showError(specialty,"Your body fat is not valid");
        }
        else{
            updateProfile();
        }
    }

    private void updateProfile() {

        loading.setVisibility(View.VISIBLE);
        update.setVisibility(View.GONE);

        String usernameInput = username.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String DOBInput = DOB.getText().toString().trim();
        String sexInput = sex.getText().toString().trim().toUpperCase(Locale.ROOT);
        String specialtyInput = specialty.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("message");

                    if(success.equals("success")){
                        Toast.makeText(UpdatePtActivity.this, "Update succeed", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(),
                                ProfilePtActivity.class));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(UpdatePtActivity.this, "Update error " + e.toString(), Toast.LENGTH_LONG).show();
                    loading.setVisibility(View.GONE);
                    update.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(UpdatePtActivity.this, "Update crash error " + error.toString(), Toast.LENGTH_LONG).show();
                loading.setVisibility(View.GONE);
                update.setVisibility(View.VISIBLE);

            }
        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",String.valueOf(id));
                params.put("username", usernameInput);
                params.put("pass", passwordInput);
                params.put("DOB", DOBInput);
                params.put("specialty", specialtyInput);
                params.put("sex", sexInput);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(request);
    }


    private void showError(EditText editText, String s) {
        editText.setError(s);
        editText.requestFocus();
    }
}