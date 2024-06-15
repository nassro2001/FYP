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
import com.example.FinalProject.classes.SessionManager;
import com.example.FinalProject.classes.SessionManagerPt;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText username,password;
    private Button Login,Register,registerPT;
    private ProgressBar loading;
    private String URL_LOGIN = "http://172.26.9.32/Rest-API/API/Login.php";
    private String URL_LOGIN_PT = "http://172.26.9.32/Rest-API/API/LoginPT.php";
    SessionManager sessionManager;
    SessionManagerPt ptSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        ptSession = new SessionManagerPt(this);

        //get the elements

        username = findViewById(R.id.UsernameInput);
        password = findViewById(R.id.PasswordInput);
        Login = findViewById(R.id.LoginBtn);
        Register = findViewById(R.id.RegisterBtn);
        loading = findViewById(R.id.progressBar);
        registerPT = findViewById(R.id.registerPtBtn);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCredentials();
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        registerPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterPtActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkSession();
    }

    private void checkSession() {

        int userID = sessionManager.getSession();
        int ptID = ptSession.getSession();
        if(userID != -1){
            moveToProfile();
        }
        else if(ptID != -1){
            moveToProfilePt();
        }
    }


    private void checkCredentials() {
        String usernameInput = username.getText().toString();
        String passwordInput = password.getText().toString();

        if(usernameInput.isEmpty() || usernameInput.length()<2){
            showError(username,"The username is Invalid");
        }
        else if(passwordInput.isEmpty()){
            showError(password,"The password is Invalid");
        }
        else{
            Login(usernameInput, passwordInput);
        }
    }

    private void Login(String username, String password) {

        loading.setVisibility(View.VISIBLE);
        Login.setVisibility(View.GONE);
        Register.setVisibility(View.GONE);
        registerPT.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("message");
                    if(success.equals("Login")){

                        int id = jsonObject.getInt("idUser");
                        Toast.makeText(getApplicationContext(), "Login User succeed", Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);

                        sessionManager.createSession(id);
                        moveToProfile();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               LoginPT(username,password);
            }
        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();

                param.put("username",username);
                param.put("pass", password);

                return param;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);

    }

    private void LoginPT(String username, String password) {

        loading.setVisibility(View.VISIBLE);
        Login.setVisibility(View.GONE);
        Register.setVisibility(View.GONE);
        registerPT.setVisibility(View.GONE);

        StringRequest request = new StringRequest(Request.Method.POST, URL_LOGIN_PT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("message");
                    if(success.equals("Login")){

                        int id = jsonObject.getInt("idPT");
                        Toast.makeText(getApplicationContext(), "Login PT succeed", Toast.LENGTH_SHORT).show();
                        loading.setVisibility(View.GONE);

                        ptSession.createSession(id);
                        sessionManager.removeSession();
                        moveToProfilePt();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    loading.setVisibility(View.GONE);
                    Login.setVisibility(View.VISIBLE);
                    Register.setVisibility(View.VISIBLE);
                    registerPT.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Username or Password incorrect "+e.toString() , Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.setVisibility(View.GONE);
                Login.setVisibility(View.VISIBLE);
                Register.setVisibility(View.VISIBLE);
                registerPT.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Username or Password incorrect " + error.toString() , Toast.LENGTH_LONG).show();
            }
        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();

                param.put("username",username);
                param.put("pass", password);

                return param;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void moveToProfile() {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void moveToProfilePt() {
        Intent intent = new Intent(getApplicationContext(), ProfilePtActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showError(EditText editText, String s) {
        editText.setError(s);
        editText.requestFocus();

    }
}