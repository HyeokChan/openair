package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class TimeSelect extends AppCompatActivity {

    RecyclerView mRecyclerViewTime;
    RecyclerView.LayoutManager mLayoutManagerTime;

    Button TimeSelectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_select);

        TimeSelectButton = (Button) findViewById(R.id.TimeSelectButton);
        TimeSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent CashIntent = new Intent(TimeSelect.this, Cash.class);
                TimeSelect.this.startActivity(CashIntent);
            }
        });

        mRecyclerViewTime = (RecyclerView)findViewById(R.id.TimeRecycler);
        mRecyclerViewTime.setHasFixedSize(true);
        mLayoutManagerTime = new LinearLayoutManager(this);
        mRecyclerViewTime.setLayoutManager(mLayoutManagerTime);

        ArrayList<TimeSelectInfo> timeInfoArrayList = new ArrayList<TimeSelectInfo>();
        timeInfoArrayList.add(new TimeSelectInfo("09:00", "예약가능", "좋음"));
        timeInfoArrayList.add(new TimeSelectInfo("10:00", "예약가능", "나쁨"));
        timeInfoArrayList.add(new TimeSelectInfo("11:00", "예약가능", "보통"));
        timeInfoArrayList.add(new TimeSelectInfo("12:00", "예약불가", "좋음"));
        MyAdapterTime myAdapterTi = new MyAdapterTime(timeInfoArrayList);
        mRecyclerViewTime.setAdapter(myAdapterTi);
    }

    // MyAdapterTime
    public class MyAdapterTime extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvTime;
            TextView tvReserve;
            TextView tvDust;
            CheckBox cbSelect;

            MyViewHolder(View view){
                super(view);
                view.setOnClickListener(this);
                tvTime = view.findViewById(R.id.TimeTime);
                tvReserve = view.findViewById(R.id.TimeReserve);
                tvDust = view.findViewById(R.id.TimeDust);
                cbSelect = view.findViewById(R.id.TimeCheck);
            }

            @Override
            public void onClick(View v) {

            }

        }

        private ArrayList<TimeSelectInfo> timeSelectInfoArrayList;

        MyAdapterTime(ArrayList<TimeSelectInfo> timeSelectArrayList){
            this.timeSelectInfoArrayList = timeSelectArrayList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_time, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            MyViewHolder myViewHolder = (MyViewHolder) holder;

            myViewHolder.tvTime.setText(timeSelectInfoArrayList.get(position).time);
            myViewHolder.tvReserve.setText(timeSelectInfoArrayList.get(position).reserve);
            myViewHolder.tvDust.setText(timeSelectInfoArrayList.get(position).dust);
            if (myViewHolder.tvDust.getText().toString().equals("좋음"))
            {
                myViewHolder.tvTime.setBackgroundColor(Color.parseColor("#E0FFFF"));
                myViewHolder.tvReserve.setBackgroundColor(Color.parseColor("#E0FFFF"));
                myViewHolder.tvDust.setBackgroundColor(Color.parseColor("#E0FFFF"));
                myViewHolder.cbSelect.setBackgroundColor(Color.parseColor("#E0FFFF"));
            }
            else if (myViewHolder.tvDust.getText().toString().equals("보통"))
            {
                myViewHolder.tvTime.setBackgroundColor(Color.parseColor("#FFA500"));
                myViewHolder.tvReserve.setBackgroundColor(Color.parseColor("#FFA500"));
                myViewHolder.tvDust.setBackgroundColor(Color.parseColor("#FFA500"));
                myViewHolder.cbSelect.setBackgroundColor(Color.parseColor("#FFA500"));
            }
            else if (myViewHolder.tvDust.getText().toString().equals("나쁨"))
            {
                myViewHolder.tvTime.setBackgroundColor(Color.parseColor("#FF6347"));
                myViewHolder.tvReserve.setBackgroundColor(Color.parseColor("#FF6347"));
                myViewHolder.tvDust.setBackgroundColor(Color.parseColor("#FF6347"));
                myViewHolder.cbSelect.setBackgroundColor(Color.parseColor("#FF6347"));
            }
            if (myViewHolder.tvReserve.getText().toString().equals("예약불가"))
            {
                myViewHolder.cbSelect.setClickable(false);
            }
        }

        @Override
        public int getItemCount() {
            return timeSelectInfoArrayList.size();
        }
    }
}
