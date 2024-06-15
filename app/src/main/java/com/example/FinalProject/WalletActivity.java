package com.example.FinalProject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.FinalProject.classes.SessionManager;
import com.example.FinalProject.classes.User;
import com.example.FinalProject.classes.Wallet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class WalletActivity extends AppCompatActivity {

    private int idUser;
    private SessionManager sessionManager;
    private String URL = "http://172.26.9.32/Rest-API/API/getWalletByUserId.php?id=";
    private TextView amount,doll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        sessionManager = new SessionManager(this);
        HashMap<String,Integer> users = sessionManager.getUserId();
        idUser =  users.get("ID");

        amount = findViewById(R.id.amount);
        doll = findViewById(R.id.doll);

        getWallet(idUser);
    }

    private void getWallet(int idUser) {

        StringRequest request = new StringRequest(Request.Method.GET, URL + idUser, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    Wallet wallet = new Wallet();
                    wallet.setAmount(jsonObject.getInt("amount"));
                    amount.setText(String.valueOf(wallet.getAmount()));

                }catch (JSONException e){
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                doll.setText("");
                amount.setText("No amount added");
            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(request);
    }
}