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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class TimeSelect extends AppCompatActivity {

    public static final String LOG_TAG = "ChildFragment1(Recruit)";
    public static final String QUEUE_TAG = "VolleyRequest";

    protected RequestQueue mQueue = null;
    JSONObject mResult = null;
    ArrayList<TimeSelectInfo> timeInfoArrayList = new ArrayList<TimeSelectInfo>();
    protected MyAdapterTime myAdapterTi = new MyAdapterTime(timeInfoArrayList);
    SessionManager mSession = SessionManager.getInstance(TimeSelect.this);


    RecyclerView mRecyclerViewTime;
    RecyclerView.LayoutManager mLayoutManagerTime;

    Button TimeSelectButton;
    static final ArrayList<String> arr_time = new ArrayList<>();
    static final ArrayList<String> base_time = new ArrayList<String>(
            Arrays.asList("09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00")
    );



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_select);


        arr_time.clear();

        TimeSelectButton = (Button) findViewById(R.id.TimeSelectButton);
        TimeSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent CashIntent = new Intent(TimeSelect.this, Cash.class);
                TimeSelect.this.startActivity(CashIntent);
            }
        });

        mRecyclerViewTime = (RecyclerView)findViewById(R.id.TimeRecycler);
        mRecyclerViewTime.setAdapter(myAdapterTi);
        mRecyclerViewTime.setHasFixedSize(true);
        mLayoutManagerTime = new LinearLayoutManager(this);
        mRecyclerViewTime.setLayoutManager(mLayoutManagerTime);


        /*timeInfoArrayList.add(new TimeSelectInfo("09:00", "예약가능", "좋음",true));
        timeInfoArrayList.add(new TimeSelectInfo("10:00", "예약가능", "나쁨",true));
        timeInfoArrayList.add(new TimeSelectInfo("11:00", "예약불가", "보통",true));
        timeInfoArrayList.add(new TimeSelectInfo("12:00", "예약가능", "좋음",false));
        timeInfoArrayList.add(new TimeSelectInfo("13:00", "예약가능", "좋음",false));
        timeInfoArrayList.add(new TimeSelectInfo("14:00", "예약가능", "나쁨",false));
        timeInfoArrayList.add(new TimeSelectInfo("15:00", "예약가능", "좋음",false));
        timeInfoArrayList.add(new TimeSelectInfo("16:00", "예약가능", "나쁨",false));
        timeInfoArrayList.add(new TimeSelectInfo("17:00", "예약가능", "나쁨",false));
        timeInfoArrayList.add(new TimeSelectInfo("18:00", "예약가능", "좋음",false));
        timeInfoArrayList.add(new TimeSelectInfo("19:00", "예약가능", "좋음",false));
        timeInfoArrayList.add(new TimeSelectInfo("20:00", "예약가능", "보통",false));
        timeInfoArrayList.add(new TimeSelectInfo("21:00", "예약가능", "보통",false));*/






        CookieHandler.setDefault(new CookieManager());

        mQueue = mSession.getQueue();
        requestRecruit();
    }



    // MyAdapterTime
    public class MyAdapterTime extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

        ArrayList<TimeSelectInfo> timeInfoArrayList = null;
        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvTime;
            TextView tvReserve;
            TextView tvDust;
            CheckBox cbSelect;

            public MyViewHolder(View view){
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

            final MyViewHolder myViewHolder = (MyViewHolder) holder;


            myViewHolder.tvTime.setText(timeSelectInfoArrayList.get(position).time);
            myViewHolder.tvReserve.setText(timeSelectInfoArrayList.get(position).reserve);
            myViewHolder.tvDust.setText(timeSelectInfoArrayList.get(position).dust);
            myViewHolder.cbSelect.setEnabled(timeSelectInfoArrayList.get(position).isSelected);


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

            myViewHolder.cbSelect.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    if(myViewHolder.cbSelect.isChecked()==true) {
                        arr_time.add(myViewHolder.tvTime.getText().toString());
                        Toast.makeText(
                                TimeSelect.this,
                                myViewHolder.tvTime.getText().toString(), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        arr_time.remove(myViewHolder.tvTime.getText().toString());
                    }


                }
            });

        }

        @Override
        public int getItemCount() {
            return timeSelectInfoArrayList.size();
        }
    }

    public void drawList() {
        int j;
        int k=1;
        timeInfoArrayList.clear();
        try {
            JSONArray items = mResult.getJSONArray("list");

            for (j=0; j<base_time.size(); j++)
            {
                k=1;
                for (int i = 0; i < items.length(); i++) {
                    JSONObject info = items.getJSONObject(i);
                    String place_no = info.getString("place_no");
                    String date = info.getString("date");
                    String time = info.getString("time");
                    if (place_no.equals(Menu2Fragment.c_name) && date.equals(DateSelect.c_date) && time.equals(base_time.get(j)))
                    {
                        timeInfoArrayList.add(new TimeSelectInfo(base_time.get(j),"예약불가","좋음",false));
                        k=0;
                    }
                }
                if(k==1)
                {
                    timeInfoArrayList.add(new TimeSelectInfo(base_time.get(j),"예약가능","나쁨",true));
                }

            }


        } catch (JSONException | NullPointerException e) {
            Toast.makeText(TimeSelect.this,
                    "모집 글이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            mResult = null;
        }

        myAdapterTi.notifyDataSetChanged();
    }


    private void requestRecruit() {
        String url = SessionManager.getURL() + "reserve/select_time.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mResult = response;
                        drawList();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getMessage() == null) {
                            Log.i(LOG_TAG, "서버 에러");
                            Toast.makeText(TimeSelect.this, "서버 에러",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.i(LOG_TAG, error.getMessage());
                            //Toast.makeText(getContext(), error.getMessage(),
                            //      Toast.LENGTH_LONG).show();
                        }
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }


}
