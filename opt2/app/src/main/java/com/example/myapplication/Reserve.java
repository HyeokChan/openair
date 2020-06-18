package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Reserve extends AppCompatActivity {

    Button btReserve;
    Button btReview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        btReserve = (Button) findViewById(R.id.ReserveButton);
        btReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent DateIntent = new Intent(Reserve.this, DateSelect.class);
                Reserve.this.startActivity(DateIntent);
            }
        });

        btReview = (Button) findViewById(R.id.ReviewButton);
        btReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ReviewIntent = new Intent(Reserve.this, ReviewActivity.class);
                Reserve.this.startActivity(ReviewIntent);
            }
        });


    }
}