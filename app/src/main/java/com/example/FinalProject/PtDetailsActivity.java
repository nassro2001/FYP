package com.example.FinalProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.FinalProject.classes.Comments;
import com.example.FinalProject.classes.PT;
import com.example.FinalProject.classes.Rates;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PtDetailsActivity extends AppCompatActivity {

    private String URL1 = "http://172.26.9.32/Rest-API/API/getPtById.php?id=";
    private String URl_RATE = "http://172.26.9.32/Rest-API/API/getRatesByPtId.php?id=";
    private String URl_COMMENTS = "http://172.26.9.32/Rest-API/API/getCommentsByPtId.php?id=";
    private TextView username, specialty, sex, rates;
    private Button sessions;
    private ArrayList<Rates> ratesList;
    private ArrayList<Comments> commentsList;
    private VolleyAdapter volleyAdapter;
    private ListView listView;
    private static final String TAG = PtDetailsActivity.class.getName();
    private int idPT;
    private PT pt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pt_details);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            idPT = extras.getInt("idPT");
        }

        ratesList = new ArrayList<>();
        commentsList = new ArrayList<>();

        listView = (ListView) findViewById(R.id.commentsList);
        sessions = findViewById(R.id.BookSession);

        getPtDetails(idPT);
        getRates(idPT);
        getComments(idPT);

        sessions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),SessionsActivity.class);
                intent.putExtra("idPT",idPT);
                startActivity(intent);
            }
        });
    }

    private class VolleyAdapter extends ArrayAdapter<Comments> {

        public VolleyAdapter(@NonNull Context context, int resource, ArrayList<Comments> comments) {
            super(context, resource, comments);
        }

        @Override
        public int getCount() {
            return commentsList.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View view1 = getLayoutInflater().inflate(R.layout.comments_in_list,null);
            TextView username = view1.findViewById(R.id.userName);
            TextView comment = view1.findViewById(R.id.comment);

            Comments comments = commentsList.get(i);

            username.setText(comments.getUsername());
            comment.setText(comments.getComment());

            return view1;
        }
    }

    private void getComments(int idPT) {


        StringRequest request = new StringRequest(Request.Method.GET, URl_COMMENTS + idPT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject object = null;

                try {

                    object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("results");

                    for(int i = 0; i<jsonArray.length(); i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Comments comment = new Comments();

                        comment.setComment(jsonObject.getString("comment"));
                        comment.setUsername(jsonObject.getString("username"));

                        commentsList.add(comment);
                    }

                    volleyAdapter = new VolleyAdapter(getApplicationContext(), R.layout.comments_in_list, commentsList);
                    listView.setAdapter(volleyAdapter);

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(request);
    }


    private void getRates(int idPT) {

        rates = findViewById(R.id.rate);

        StringRequest request = new StringRequest(Request.Method.GET, URl_RATE + idPT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject object = null;
                int average;

                try {

                    object = new JSONObject(response);

                    JSONArray jsonArray = object.getJSONArray("results");

                    for(int i = 0; i<jsonArray.length(); i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Rates rate = new Rates();

                        rate.setRates(jsonObject.getInt("rate"));

                        ratesList.add(rate);

                    }

                    //calculate the average of the rates

                    int sum =0;

                    for (int i=0; i< ratesList.size(); i++) {
                        sum += ratesList.get(i).getRates();
                    }
                    average = sum/ratesList.size();
                    rates.setText(String.valueOf(average));

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

               rates.setText("no rates yet");

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(request);
    }


    private void getPtDetails(int idPT) {

        username = findViewById(R.id.usernane);
        specialty = findViewById(R.id.specialty);
        sex = findViewById(R.id.sex);

        StringRequest request = new StringRequest(Request.Method.GET, URL1 + idPT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);

                    pt = new PT();

                    pt.setUsername(jsonObject.getString("username"));
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
}