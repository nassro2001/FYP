package com.example.FinalProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.FinalProject.classes.Calendar;
import com.example.FinalProject.classes.SessionManager;
import com.example.FinalProject.classes.SessionManagerPt;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class VideoCallPTActivity extends AppCompatActivity {

    private ArrayList<Calendar> calList = new ArrayList<>();
    private int idPT;
    private String URL = "http://172.26.9.32/Rest-API/API/getCalendarByPtId.php?id=";
    private ListView VC_list;
    private SessionManagerPt sessionManagerPt;
    private VolleyAdapter volleyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_ptactivity);
        sessionManagerPt = new SessionManagerPt(this);
        HashMap<String,Integer> user = sessionManagerPt.getUserId();
        idPT =  user.get("IDPT");

        VC_list = (ListView) findViewById(R.id.VC_lis_pt);

        getCalendar(idPT);
    }

    private void getCalendar(int idPT) {

        StringRequest request = new StringRequest(Request.Method.GET, URL + idPT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject object = null;

                try {

                    object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("results");

                    for(int i = 0; i<jsonArray.length(); i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Calendar calendar = new Calendar();

                        calendar.setDateTime(jsonObject.getString("date"));
                        calendar.setId(jsonObject.getInt("idSession"));
                        calendar.setUsername(jsonObject.getString("username"));
                        calList.add(calendar);
                    }

                    volleyAdapter = new VolleyAdapter(getApplicationContext(), R.layout.calls_in_list_pt, calList);
                    VC_list.setAdapter(volleyAdapter);

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

    private class VolleyAdapter extends ArrayAdapter<Calendar> {

        public VolleyAdapter(@NonNull Context context, int resource, ArrayList<Calendar> cal) {
            super(context, resource, cal);
        }

        @Override
        public int getCount() {
            return calList.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View view1 = getLayoutInflater().inflate(R.layout.calls_in_list_pt,null);
            TextView date = view1.findViewById(R.id.date);
            TextView username = view1.findViewById(R.id.username);
            Button btn_open = view1.findViewById(R.id.open_call);

            Calendar calendar = calList.get(i);

            date.setText(parseDateToddMMyyyy(calendar.getDateTime()));
            username.setText(calendar.getUsername());

            btn_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                .setServerURL(new URL("https://meet.jit.si"))
                                .setRoom("room " + String.valueOf(calendar.getId()))
                                .setVideoMuted(true)
                                .setAudioMuted(true)
                                .build();

                        JitsiMeetActivity.launch(VideoCallPTActivity.this,options);
                    }catch (MalformedURLException e){
                        e.printStackTrace();
                    }
                }
            });

            return view1;
        }
    }
    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd-MMM-yyyy h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }
}