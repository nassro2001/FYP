package com.example.FinalProject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class FitnessActivity extends AppCompatActivity {

    private ImageButton lose;
    private ImageButton muscle;
    private ImageButton boxe;
    private ImageButton tone;
    private ImageButton core;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);

        lose = findViewById(R.id.lose);
        muscle = findViewById(R.id.muscle);
        boxe = findViewById(R.id.boxe);
        tone = findViewById(R.id.tone);
        core = findViewById(R.id.improve);

        lose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),
                        PtActivity.class));
            }
        });

        muscle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),
                        PtActivity.class));
            }
        });

        boxe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),
                        PtActivity.class));
            }
        });
        tone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),
                        PtActivity.class));
            }
        });

        core.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),
                        PtActivity.class));
            }
        });
    }
}