package com.example.FinalProject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class DanceActivity extends AppCompatActivity {

    private ImageButton zumba;
    private ImageButton hiphop;
    private ImageButton ujam;
    private ImageButton oriental;
    private ImageButton fusion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dance);

        zumba = findViewById(R.id.zumba);
        hiphop = findViewById(R.id.hiphop);
        ujam = findViewById(R.id.ujam);
        oriental = findViewById(R.id.oriental);
        fusion = findViewById(R.id.fusion);


        zumba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),
                        PtActivity.class));
            }
        });

        hiphop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),
                        PtActivity.class));
            }
        });

        ujam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),
                        PtActivity.class));
            }
        });

        oriental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),
                        PtActivity.class));
            }
        });

        fusion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),
                        PtActivity.class));
            }
        });
    }
}