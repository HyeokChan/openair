package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class InfoAppActivity extends AppCompatActivity {

    private TextView Infotitle;
    private TextView info1;
    private TextView info2;
    private TextView info3;
    private TextView info4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appinfo);
        Infotitle = (TextView)findViewById(R.id.InfoApptitle);
        info1 = (TextView)findViewById(R.id.Info1);
        info2 = (TextView)findViewById(R.id.Info1);
        info3 = (TextView)findViewById(R.id.Info1);
        info4 = (TextView)findViewById(R.id.Info4);

    }
}
