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

public class UpdateUserActivity extends AppCompatActivity {

    private ProgressBar loading;
    private EditText username,email,password,confirmPass,DOB,sex,height,weight,bodyFat;
    private Button update, wallet;
    private int id;
    private static String URL = "http://172.26.9.32/Rest-API/API/updateUser.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        loading = findViewById(R.id.loading);
        username = findViewById(R.id.UsernameInput);
        email = findViewById(R.id.EmailInput);
        password = findViewById(R.id.PasswordInput);
        confirmPass = findViewById(R.id.ConfirmPassInput);
        DOB = findViewById(R.id.DateInput);
        sex = findViewById(R.id.SexInput);
        height = findViewById(R.id.SpecialtyInput);
        weight = findViewById(R.id.WeightInput);
        bodyFat = findViewById(R.id.BodyFatInput);
        update = findViewById(R.id.Update);
        wallet = findViewById(R.id.wallet);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            id = extras.getInt("id");
            username.setText(extras.getString("username"));
            email.setText(extras.getString("email"));
            password.setText(extras.getString("password"));
            sex.setText(extras.getString("sex"));
            confirmPass.setText(extras.getString("password"));
            DOB.setText(extras.getString("DOB"));
            height.setText(String.valueOf(extras.getInt("height")));
            weight.setText(String.valueOf(extras.getInt("weight")));
            bodyFat.setText(String.valueOf(extras.getInt("BF")));
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCredentials();
            }
        });

        wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),WalletActivity.class));
            }
        });

    }

    private void checkCredentials() {

        String usernameInput = username.getText().toString();
        String emailInput = email.getText().toString();
        String passwordInput = password.getText().toString();
        String confirmPassInput = confirmPass.getText().toString();
        String DOBInput = DOB.getText().toString();
        String sexInput = sex.getText().toString();
        String heightInput = height.getText().toString();
        String weightInput = weight.getText().toString();
        String bodyFatInput = bodyFat.getText().toString();

        if(usernameInput.isEmpty() || usernameInput.length()<3){
            showError(username,"Your username is not valid");
        }
        else if(emailInput.isEmpty() || !emailInput.contains("@")){
            showError(email,"Your email is not valid");
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
        else if (heightInput.isEmpty()){
            showError(height,"Your height is not valid");
        }
        else if (weightInput.isEmpty()){
            showError(weight,"Your weight is not valid");
        }
        else if (bodyFatInput.isEmpty()){
            showError(bodyFat,"Your body fat is not valid");
        }
        else{
            updateUser();
        }
    }

    private void updateUser() {

        loading.setVisibility(View.VISIBLE);
        update.setVisibility(View.GONE);

        String usernameInput = username.getText().toString().trim();
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();
        String DOBInput = DOB.getText().toString().trim();
        String sexInput = sex.getText().toString().trim().toUpperCase(Locale.ROOT);
        String heightInput = height.getText().toString().trim();
        String weightInput = weight.getText().toString().trim();
        String bodyFatInput = bodyFat.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("message");

                    if(success.equals("success")){
                        Toast.makeText(UpdateUserActivity.this, "Update succeed", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(),
                                LoginActivity.class));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(UpdateUserActivity.this, "Update error " + e.toString(), Toast.LENGTH_LONG).show();
                    loading.setVisibility(View.GONE);
                    update.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(UpdateUserActivity.this, "Update crash error " + error.toString(), Toast.LENGTH_LONG).show();
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
                params.put("email", emailInput);
                params.put("DOB", DOBInput);
                params.put("pass", passwordInput);
                params.put("sex", sexInput);
                params.put("weight", weightInput);
                params.put("height", heightInput);
                params.put("bodyFat", bodyFatInput);

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