package com.example.FinalProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.EventLog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    SessionManager sessionManager;
    private int idUser;
    private TextView month;
    CompactCalendarView compactCalendar;
    private ListView listView;
    private VolleyAdapter volleyAdapter;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    private static final String TAG = CalendarActivity.class.getName();
    private ArrayList<Calendar> calList = new ArrayList<>();
    private ArrayList<usersSessions> ulist;
    private String URL = "http://172.26.9.32/Rest-API/API/getCalendarByUserId.php?id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        menuSelect();
        sessionManager = new SessionManager(this);
        HashMap<String,Integer> users = sessionManager.getUserId();
        idUser =  users.get("ID");

        listView = (ListView)findViewById(R.id.ptlist);
        month = (TextView)findViewById(R.id.month);
        Date date = new Date();
        month.setText(dateFormatMonth.format(date));
        compactCalendar = (CompactCalendarView)findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        getCalendarByUserId(idUser);

    }


    private void getCalendarByUserId(int idUser) {

        StringRequest request = new StringRequest(Request.Method.GET, URL + idUser, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject object = null;
                long dateTimeMilli;

                try {

                    object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("results");

                    for(int i = 0; i<jsonArray.length(); i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Calendar calendar = new Calendar();

                        calendar.setDateTime(jsonObject.getString("date"));
                        calendar.setPtname(jsonObject.getString("ptname"));
                        calendar.setId(jsonObject.getInt("idSession"));
                        calList.add(calendar);
                        dateTimeMilli = getTimeinmillis(calendar.getDateTime());
                        addEvents(dateTimeMilli);
                    }

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

    private void addEvents(long dateTimeMilli) {

        Event ev = new Event(Color.BLUE, dateTimeMilli,"Session");
        compactCalendar.addEvent(ev);

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                String time;
                ulist = new ArrayList<>();

                for(int i=0; i<calList.size(); i++){

                    if(dateClicked.toLocaleString().equals(parseDate(calList.get(i).getDateTime())+" 12:00:00 AM")){

                        usersSessions uSess = new usersSessions();
                        time = parseTo12(calList.get(i).getDateTime().substring(11));
                        uSess.setTime(time);
                        uSess.setIdUser(calList.get(i).getUserID());
                        uSess.setUser(calList.get(i).getPtname());
                        uSess.setIdSession(calList.get(i).getId());
                        ulist.add(uSess);
                    }
                }
                volleyAdapter = new VolleyAdapter(getApplicationContext(), R.layout.comments_in_list, ulist);
                listView.setAdapter(volleyAdapter);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

                 month.setText(dateFormatMonth.format(firstDayOfNewMonth));

            }
        });
    }

    private class VolleyAdapter extends ArrayAdapter<usersSessions> {

        public VolleyAdapter(@NonNull Context context, int resource, ArrayList<usersSessions> usersSessions) {
            super(context, resource, usersSessions);
        }

        @Override
        public int getCount() {
            return ulist.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int in, View view, ViewGroup viewGroup) {

            View view1 = getLayoutInflater().inflate(R.layout.users_in_list,null);
            TextView username = view1.findViewById(R.id.usernameSession);
            TextView time = view1.findViewById(R.id.time);

            usersSessions uSess = ulist.get(in);

            username.setText(uSess.getUser());
            time.setText(uSess.getTime());
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(getApplicationContext(),DeleteActivity.class);
                    intent.putExtra("ptname",ulist.get(in).getUser());
                    intent.putExtra("id",ulist.get(in).getIdSession());
                    startActivity(intent);
                }
            });

            return view1;
        }
    }

    private long getTimeinmillis(String datetime) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(datetime);
            timeInMilliseconds = mDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    private String parseDate(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "MMM dd, yyyy";
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

    private String parseTo12(String time){
        String finaltime = null;
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
            final Date dateObj = sdf.parse(time);
            finaltime = new SimpleDateFormat("h:mm a").format(dateObj);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        return finaltime;
    }

    private void menuSelect() {
        //initialize and assign variable

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);


        //set calendar selected
        bottomNavigationView.setSelectedItemId(R.id.calendar);
        //perform itemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.calendar:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),
                                HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),
                                ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;

            }
        });
    }
}