package com.example.FinalProject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.FinalProject.classes.Availabilities;
import com.example.FinalProject.classes.MyNotification;
import com.example.FinalProject.classes.Reminder10minBroadcast;
import com.example.FinalProject.classes.Reminder15minBroadcast;
import com.example.FinalProject.classes.Reminder5minBroadcast;
import com.example.FinalProject.classes.ReminderBroadcast;
import com.example.FinalProject.classes.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AvailabilitiesActivity extends AppCompatActivity {

    private int idPT, nbrSessions,idUser,price,amount;
    private int counter = 0;
    private VolleyAdapter volleyAdapter;
    private ListView listView;
    private TextView selectedItems, calendar;
    private Button btnBook;
    private ArrayList<Availabilities> avaibAl;
    private ArrayList<String> calAl;
    private final String URL = "http://172.26.9.32/Rest-API/API/getPtAvaib.php?id=";
    private final String URL_BOOK = "http://172.26.9.32/Rest-API/API/createSession.php";
    private final String URL_WALLET = "http://172.26.9.32/Rest-API/API/getWalletByUserId.php?id=";
    private final String URL_UPDATE_WALLET = "http://172.26.9.32/Rest-API/API/updateWallet.php";
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaibilities);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            idPT = extras.getInt("idPT");
            nbrSessions = extras.getInt("sessionsSelected");
            price = extras.getInt("price");
        }

        sessionManager = new SessionManager(getApplicationContext());
        avaibAl = new ArrayList<>();
        calAl = new ArrayList<>();

        HashMap<String,Integer> users = sessionManager.getUserId();
        idUser =  users.get("ID");
        listView = (ListView)findViewById(R.id.listview_avaib);
        btnBook = findViewById(R.id.btnBook);
        calendar = findViewById(R.id.btnCalendar);
        TextView txtSess = (TextView) findViewById(R.id.nbrSess);
        TextView title = (TextView) findViewById(R.id.Title);
        selectedItems = findViewById(R.id.nbrSelected);
        txtSess.setText(String.valueOf(nbrSessions));
        selectedItems.setText("0");
        if(nbrSessions == 1){
            title.setText("Chose your session");
        }

        getAvaibs(idPT);
        getWallet(idUser);

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Integer.valueOf((String) selectedItems.getText())<nbrSessions){
                    Toast.makeText(getApplicationContext(),"You have to select the number of sessions desire",Toast.LENGTH_LONG).show();
                }

                else if(amount < price){
                    Toast.makeText(getApplicationContext(),"Your credit is not enough to purchase these sessions",Toast.LENGTH_LONG).show();
                }

                else {

                    for (int i = 0; i < calAl.size(); i++) {
                        addSessions(idPT, idUser, calAl.get(i));
                        sendNotif(i);
                        setReminder15min(getTimeinmillis(calAl.get(i)));
                        setReminder10min(getTimeinmillis(calAl.get(i)));
                        setReminder5min(getTimeinmillis(calAl.get(i)));
                        setReminderTimeIsUp(getTimeinmillis(calAl.get(i)));
                    }
                    updateWallet(idUser);

                }
            }
        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),CalendarActivity.class));
            }
        });
    }

    private void setReminder15min(long timeInMillis) {

        Intent intent = new Intent(getApplicationContext(), Reminder15minBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                timeInMillis - 900000,
                pendingIntent);

    }

    private void setReminder10min(long timeInMillis) {

        Intent intent = new Intent(getApplicationContext(), Reminder10minBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                timeInMillis - 600000,
                pendingIntent);

    }

    private void setReminder5min(long timeInMillis) {

        Intent intent = new Intent(getApplicationContext(), Reminder5minBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                timeInMillis - 300000,
                pendingIntent);

    }

    private void setReminderTimeIsUp(long timeInMillis) {

        Intent intent = new Intent(getApplicationContext(), ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent);

    }

    private void sendNotif(int counter) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyNotification.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle("PTT App")
                .setContentText("You have a new session !")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(counter, builder.build());
    }

    private void updateWallet(int idUser) {

        StringRequest request = new StringRequest(Request.Method.POST, URL_UPDATE_WALLET, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("message");

                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(AvailabilitiesActivity.this, "Update error " + e.toString(), Toast.LENGTH_LONG).show();

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
                params.put("id", String.valueOf(idUser));
                params.put("amount", String.valueOf(price));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(request);
    }

    private void addSessions(int idPT, int idUser, String session) {

        StringRequest request = new StringRequest(Request.Method.POST, URL_BOOK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("message");

                    if(success.equals("success")){
                        Toast.makeText(getApplicationContext(), "Sessions were add successfully check your calendar", Toast.LENGTH_LONG).show();
                        calendar.setVisibility(View.VISIBLE);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "adding sessions error " + e.toString(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "Register crash error " + error.toString(), Toast.LENGTH_LONG).show();

            }
        })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("date", session);
                params.put("user_id", String.valueOf(idUser));
                params.put("pt_id", String.valueOf(idPT));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        requestQueue.add(request);
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

    private void getWallet(int idUser) {

        StringRequest request = new StringRequest(Request.Method.GET, URL_WALLET + idUser, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    amount =jsonObject.getInt("amount");

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


    private class VolleyAdapter extends ArrayAdapter<Availabilities> {

        public VolleyAdapter(@NonNull Context context, int resource, ArrayList<Availabilities> availabilities) {
            super(context, resource, availabilities);
        }
        @Override
        public int getCount() {
            return avaibAl.size();
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View view1 = getLayoutInflater().inflate(R.layout.avaib_in_list,null);
            CheckBox checkBox = (CheckBox) view1.findViewById(R.id.avaibCheck);

            Availabilities avaib = avaibAl.get(i);

            checkBox.setText(parseDateToddMMyyyy(avaib.getAvailability()));

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton cb, boolean b) {

                    //check if the max of sessions is not reached if it is it disabled the others cb

                    if(counter >= nbrSessions && b){
                        cb.setChecked(false);
                    }

                    //else if the cb is checked the counter increments

                    else if(b){
                        counter++;
                        selectedItems.setText(String.valueOf(counter));
                        calAl.add(avaib.getAvailability());
                    }

                    //else id the cb is unchecked the counter decrement

                    else if(!b){
                        counter--;
                        selectedItems.setText(String.valueOf(counter));
                        calAl.remove((String) avaib.getAvailability());
                    }
                }
            });
            return view1;
        }
    }

    private void getAvaibs(int idPT) {

        StringRequest request = new StringRequest(Request.Method.GET, URL + idPT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                JSONObject object = null;

                try {

                    object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("results");

                    for(int i = 0; i<jsonArray.length(); i++){

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Availabilities avaib = new Availabilities();

                        avaib.setIdPT(jsonObject.getInt("idPT"));
                        avaib.setAvailability(jsonObject.getString("availabilities"));

                        avaibAl.add(avaib);
                    }

                    volleyAdapter = new VolleyAdapter(getApplicationContext(), R.layout.avaib_in_list, avaibAl);
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