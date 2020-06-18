package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class Reserve extends AppCompatActivity {

    final Context mContext = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);

        Intent intent = getIntent();

        String image = intent.getExtras().getString("image");
        String linfo = intent.getExtras().getString("linfo");
        String number = intent.getExtras().getString("number");
        String location = intent.getExtras().getString("location");
        String address = intent.getExtras().getString("address");

        NetworkImageView ivImage = (NetworkImageView)findViewById(R.id.inimage);
        ivImage.setImageUrl(SessionManager.getURL() + "image/" + image, VolleySingleton.getInstance(mContext).getImageLoader());

        TextView tvlinfo = (TextView)findViewById(R.id.inlinfo);
        tvlinfo.setText(linfo);

        TextView tvnumber = (TextView)findViewById(R.id.innumber);
        tvnumber.setText(number);

        TextView tvlocation = (TextView)findViewById(R.id.inlocation);
        tvlocation.setText(location);

        TextView tvaddress = (TextView)findViewById(R.id.inaddress);
        tvaddress.setText(address);


        Button btReserve = (Button) findViewById(R.id.ReserveButton);
        btReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent DateIntent = new Intent(Reserve.this, DateSelect.class);
                Reserve.this.startActivity(DateIntent);
            }
        });

        Button btReview = (Button) findViewById(R.id.ReviewButton);
        btReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ReviewIntent = new Intent(Reserve.this, ReviewActivity.class);
                Reserve.this.startActivity(ReviewIntent);
            }
        });


    }
}