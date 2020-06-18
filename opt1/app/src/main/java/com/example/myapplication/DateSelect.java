package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DateSelect extends AppCompatActivity {

    Button btDataSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);

        btDataSelect = (Button) findViewById(R.id.DateSelectButton);
        btDataSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TimeIntent = new Intent(DateSelect.this, TimeSelect.class);
                DateSelect.this.startActivity(TimeIntent);
            }
        });
    }
}
