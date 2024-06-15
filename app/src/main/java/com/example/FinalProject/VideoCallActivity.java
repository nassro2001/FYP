package com.example.FinalProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.FinalProject.classes.SessionManager;
import com.example.FinalProject.classes.usersSessions;

import org.jitsi.meet.sdk.JitsiMeet;
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

public class VideoCallActivity extends AppCompatActivity {

    private ArrayList<Calendar> calList = new ArrayList<>();
    private String URL = "http://172.26.9.32/Rest-API/API/getCalendarByUserId.php?id=";
    private ListView VC_list;
    private SessionManager sessionManager;
    private boolean opened = false;
    private int idUser;
    private VolleyAdapter volleyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        sessionManager = new SessionManager(this);
        HashMap<String,Integer> users = sessionManager.getUserId();
        idUser =  users.get("ID");

        VC_list = (ListView) findViewById(R.id.VC_list);

        getCalendar(idUser);
    }

    private void getCalendar(int idUser) {

        StringRequest request = new StringRequest(Request.Method.GET, URL + idUser, new Response.Listener<String>() {
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
                        calendar.setPtname(jsonObject.getString("ptname"));
                        calendar.setId(jsonObject.getInt("idSession"));
                        calendar.setPtID(jsonObject.getInt("pt_id"));
                        calList.add(calendar);
                    }

                    volleyAdapter = new VolleyAdapter(getApplicationContext(), R.layout.calls_in_list, calList);
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

            View view1 = getLayoutInflater().inflate(R.layout.calls_in_list,null);
            TextView date = view1.findViewById(R.id.date);
            Button btn_open = view1.findViewById(R.id.open_call);
            Button btn_cr = view1.findViewById(R.id.add_CR);

            Calendar calendar = calList.get(i);

            date.setText(parseDateToddMMyyyy(calendar.getDateTime()));

            btn_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    opened = true;
                    try{
                        JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                                .setServerURL(new URL("https://meet.jit.si"))
                                .setRoom("room " + String.valueOf(calendar.getId()))
                                .setVideoMuted(true)
                                .setAudioMuted(true)
                                .build();

                        JitsiMeetActivity.launch(VideoCallActivity.this,options);
                        Reschedule(calendar.getPtID());
                    }catch (MalformedURLException e){
                        e.printStackTrace();
                    }
                }
            });

            btn_cr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(),CommentsRatesActivity.class);
                    intent.putExtra("idPT",calendar.getPtID());
                    intent.putExtra("pt_name",calendar.getPtname());
                    intent.putExtra("opened",opened);
                    startActivity(intent);
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

    public void Reschedule(int idPT){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // Setting Alert Dialog Title
        alertDialogBuilder.setTitle("Reschedule Session");
        // Icon Of Alert Dialog
        alertDialogBuilder.setIcon(R.drawable.ic_baseline_access_time_24);
        // Setting Alert Dialog Message
        alertDialogBuilder.setMessage("Do you want to reschedule another session ?");
        alertDialogBuilder.setCancelable(false);

        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(getApplicationContext(),SessionsActivity.class);
                intent.putExtra("idPT",idPT);
                startActivity(intent);
            }
        });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
