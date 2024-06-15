package com.example.FinalProject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.FinalProject.classes.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CommentsRatesActivity extends AppCompatActivity {

    private int idUser, idPT;
    private boolean opened;
    private String ptName;
    private final String URL_RATES = "http://172.26.9.32/Rest-API/API/createRate.php";
    private final String URL_COMMENTS = "http://172.26.9.32/Rest-API/API/createComment.php";
    private TextView pt;
    private EditText rate, comment;
    private Button btn_add;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_rates);

        sessionManager = new SessionManager(this);
        HashMap<String,Integer> users = sessionManager.getUserId();
        idUser =  users.get("ID");

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            idPT = extras.getInt("idPT");
            ptName = extras.getString("pt_name");
            opened = extras.getBoolean("opened");
        }

        rate = findViewById(R.id.rates);
        comment = findViewById(R.id.comments);
        pt = findViewById(R.id.PT_name);
        btn_add = findViewById(R.id.addCommentRate);
        pt.setText(ptName);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!opened){
                    Toast.makeText(CommentsRatesActivity.this,
                            "You have to open a call with the PT to rate and comment " , Toast.LENGTH_LONG).show();
                }
                else {
                    checkElements();
                }
            }
        });

    }

    private void checkElements() {

        String rateInput = rate.getText().toString().trim();
        String commentInput = comment.getText().toString().trim();
        if(rateInput.isEmpty()){
            showError(rate,"Put your rate");
        }
        else if(commentInput.isEmpty()){
            showError(comment,"Put your comment");
        }
        else{
            addRate(idUser,idPT);
        }
    }

    //add the rate

    private void addRate(int idUser, int idPT) {

        String rateInput = rate.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, URL_RATES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("message");

                    if(success.equals("success")){
                        addComment(idUser,idPT);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("rate", rateInput);
                params.put("idUser", String.valueOf(idUser));
                params.put("idPT", String.valueOf(idPT));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(request);
    }

    //add the comment

    private void addComment(int idUser, int idPT) {

        String commentInput = comment.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, URL_COMMENTS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("message");

                    if(success.equals("success")){
                        Toast.makeText(CommentsRatesActivity.this,
                                "Rate and Comment added successfully " , Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("comment", commentInput);
                params.put("idUser", String.valueOf(idUser));
                params.put("idPT", String.valueOf(idPT));

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