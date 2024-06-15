package com.example.FinalProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.FinalProject.classes.Calendar;
import com.example.FinalProject.classes.PT;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PtActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<PT> ptAl;
    private static final String TAG = PtActivity.class.getName();
    private String URL = "http://172.26.9.32/Rest-API/API/getPT.php";
    private String URL_SEARCH = "http://172.26.9.32/Rest-API/API/searchPT.php?s=";
    private VolleyAdapter volleyAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pt);

        ptAl = new ArrayList<>();

        listView = (ListView) findViewById(R.id.pt_listview);
        searchView = (SearchView) findViewById(R.id.search);
        getPT();

        searchView.setQueryHint("Search here!!");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                getPtBySearch(s);
                return false;
            }
        });
    }

    //method to get the pts by search

    private void getPtBySearch(String s) {

        //we instanciate a new al each time we invoke the method

        TextView mess = (TextView)findViewById(R.id.message);
        ptAl = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, URL_SEARCH + s, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                mess.setVisibility(View.GONE);
                JSONObject object = null;

                try {

                    object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("results");

                    for(int i = 0; i<jsonArray.length(); i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        PT pt = new PT();

                        pt.setId(jsonObject.getInt("idPT"));
                        pt.setUsername(jsonObject.getString("username"));
                        pt.setSpecialty(jsonObject.getString("specialty"));

                        ptAl.add(pt);
                        System.out.println(ptAl.size());
                    }
                    System.out.println(s);
                    volleyAdapter = new VolleyAdapter(getApplicationContext(), R.layout.pt_in_list, ptAl);
                    listView.setAdapter(volleyAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            Intent intent = new Intent(getApplicationContext(),PtDetailsActivity.class);

                            intent.putExtra("idPT",ptAl.get(i).getId());

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

                ptAl = new ArrayList<>();
                volleyAdapter = new VolleyAdapter(getApplicationContext(), R.layout.pt_in_list, ptAl);
                listView.setAdapter(volleyAdapter);
                mess.setVisibility(View.VISIBLE);

            }
        });


        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(request);
    }

    //Adapter to adapt the arraylist to the listview

    private class VolleyAdapter extends ArrayAdapter<PT> {

        public VolleyAdapter(@NonNull Context context, int resource, ArrayList<PT> pt) {
            super(context, resource, pt);
        }
        @Override
        public int getCount() {
            return ptAl.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View view1 = getLayoutInflater().inflate(R.layout.pt_in_list,null);
            TextView pt_name = view1.findViewById(R.id.pt_name);
            TextView spe = view1.findViewById(R.id.spe);
            Button btn = view1.findViewById(R.id.btn);
            Button btn2 = view1.findViewById(R.id.btn2);

            PT pt = ptAl.get(i);

            pt_name.setText(pt.getUsername());
            spe.setText(pt.getSpecialty());
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),SessionsActivity.class);
                    intent.putExtra("idPT",pt.getId());
                    startActivity(intent);
                }
            });

            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),PtDetailsActivity.class);

                    intent.putExtra("idPT",pt.getId());

                    startActivity(intent);
                }
            });

            return view1;
        }
    }

    //we get all the pts in the page

    private void getPT() {

        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject object = null;

                try {

                    object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("results");

                    for(int i = 0; i<jsonArray.length(); i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        PT pt = new PT();

                        pt.setId(jsonObject.getInt("idPT"));
                        pt.setUsername(jsonObject.getString("username"));
                        pt.setSpecialty(jsonObject.getString("specialty"));

                        ptAl.add(pt);
                    }

                    volleyAdapter = new VolleyAdapter(getApplicationContext(), R.layout.pt_in_list, ptAl);
                    listView.setAdapter(volleyAdapter);

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