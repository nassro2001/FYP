package com.example.FinalProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.FinalProject.classes.Sessions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SessionsActivity extends AppCompatActivity {

    private int idPT;
    private ListView listView;
    private ArrayList<Sessions> sessionsAl;
    private static final String TAG = SessionsActivity.class.getName();
    private String URL = "http://172.26.9.32/Rest-API/API/getSessions.php";
    private VolleyAdapter volleyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);

        sessionsAl = new ArrayList<>();
        listView = (ListView)findViewById(R.id.sessionsList);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            idPT = extras.getInt("idPT");
        }
        getSessions();

    }

    private void getSessions() {

        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject object = null;

                try {

                    object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("results");

                    for(int i = 0; i<jsonArray.length(); i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Sessions sessions = new Sessions();

                        sessions.setNbrSessions(jsonObject.getInt("numberSessions"));
                        sessions.setType(jsonObject.getString("type"));
                        sessions.setPrice(jsonObject.getInt("price"));

                        sessionsAl.add(sessions);
                    }

                    volleyAdapter = new VolleyAdapter(getApplicationContext(), R.layout.sessions_in_list, sessionsAl);
                    listView.setAdapter(volleyAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Intent intent = new Intent(getApplicationContext(), AvailabilitiesActivity.class);
                            intent.putExtra("idPT",idPT);
                            intent.putExtra("sessionsSelected",sessionsAl.get(i).getNbrSessions());
                            intent.putExtra("price",sessionsAl.get(i).getPrice());
                            startActivity(intent);

                        }
                    });

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


    //Adapter to adapt the arraylist to the listview

    private class VolleyAdapter extends ArrayAdapter<Sessions> {

        public VolleyAdapter(@NonNull Context context, int resource, ArrayList<Sessions> sessions) {
            super(context, resource, sessions);
        }
        @Override
        public int getCount() {
            return sessionsAl.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View view1 = getLayoutInflater().inflate(R.layout.sessions_in_list,null);
            TextView nbrSess = view1.findViewById(R.id.nbrSessions);
            TextView type = view1.findViewById(R.id.type);
            TextView price = view1.findViewById(R.id.price);
            TextView sess = view1.findViewById(R.id.sess);

            Sessions sessions = sessionsAl.get(i);

            if(sessions.getNbrSessions() > 1){
                sess.setText("Sessions");
            }

            nbrSess.setText(String.valueOf(sessions.getNbrSessions()));
            type.setText(sessions.getType());
            price.setText(String.valueOf(sessions.getPrice()));

            return view1;
        }
    }
}