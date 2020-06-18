package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSelect extends AppCompatActivity {

    Button btDataSelect;
    static String c_date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_select);


        CalendarView calendar = (CalendarView) findViewById(R.id.DateCalendarView);

        long now = System.currentTimeMillis();
        Date today = new Date(now);
        SimpleDateFormat todsim = new SimpleDateFormat("yyyy-MM-dd");
        c_date = todsim.format(today);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                //Toast.makeText(DateSelect.this, "" + year + "/" +
                //      (month + 1) + "/" + dayOfMonth, 0).show();
                c_date = year + "-" + (month+1) + "-" + dayOfMonth;
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(c_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                c_date = new SimpleDateFormat("yyyy-MM-dd").format(date);
                Toast.makeText(DateSelect.this, c_date, 0).show();

            }
        });

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