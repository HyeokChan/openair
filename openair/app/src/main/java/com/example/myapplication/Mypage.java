package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.ArrayList;

public class Mypage extends AppCompatActivity {

    public static final String LOG_TAG = "ChildFragment1(Recruit)";
    public static final String QUEUE_TAG = "VolleyRequest";
    SessionManager mSession = SessionManager.getInstance(Mypage.this);
    //-----------------------------------------
    protected RequestQueue mQueue = null;
    JSONObject mResult = null;
    ArrayList<my_reserveInfo> MyReserveInfoArrayList = new ArrayList<my_reserveInfo>();
    protected my_reserveAdapter mAdapter = new my_reserveAdapter(MyReserveInfoArrayList);
    //----------------------------------------
    protected RequestQueue mQueue2 = null;
    JSONObject mResult2 = null;
    ArrayList<my_write_recruitInfo> MyWriteRecInfoArrayList = new ArrayList<my_write_recruitInfo>();
    protected my_write_recAdapter mAdapter2 = new my_write_recAdapter(MyWriteRecInfoArrayList);
    //-----------------------------------------
    protected RequestQueue mQueue3 = null;
    JSONObject mResult3 = null;
    ArrayList<my_write_recruitInfo> MyWriteMatInfoArrayList = new ArrayList<my_write_recruitInfo>();
    protected my_write_matAdapter mAdapter3 = new my_write_matAdapter(MyWriteMatInfoArrayList);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        CookieHandler.setDefault(new CookieManager());
        //--------------------------------------- 내가 예약한 액티비티
        RecyclerView mRecyclerView = (RecyclerView)findViewById(R.id.reserve_recy);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Mypage.this));
        mRecyclerView.setHasFixedSize(true);
        mQueue = mSession.getQueue();
        requestReserve();
        //------------------------------------ 내가 작성한 recruit
        RecyclerView mRecyclerView2 = (RecyclerView)findViewById(R.id.write_rec_recy);
        mRecyclerView2.setAdapter(mAdapter2);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(Mypage.this));
        mRecyclerView2.setHasFixedSize(true);
        mQueue2 = mSession.getQueue();
        requestWriteRecruit();
        //----------------------------------- 내가 작성한 match
        RecyclerView mRecyclerView3 = (RecyclerView)findViewById(R.id.write_mat_recy); /////
        mRecyclerView3.setAdapter(mAdapter3);
        mRecyclerView3.setLayoutManager(new LinearLayoutManager(Mypage.this));
        mRecyclerView3.setHasFixedSize(true);
        mQueue3 = mSession.getQueue();
        requestWriteMatch();

    }


    //--------------------------------------------내가 예약한 액티비티
    public class my_reserveInfo {
        public String place_no;
        public String date;
        public String time;

        public my_reserveInfo(String place_no, String date, String time) {

            this.place_no = place_no;
            this.date = date;
            this.time = time;

        }

        public String getPlace_no() {
            return place_no;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }
    }

    public class my_reserveAdapter extends RecyclerView.Adapter<my_reserveAdapter.ViewHolder>  {

        ArrayList<my_reserveInfo> MyReserveInfoArrayList = null;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView place_no;
            TextView date;
            TextView time;


            public ViewHolder(View view){
                super(view);
                view.setOnClickListener(this);
                place_no = view.findViewById(R.id.place_no);
                date = view.findViewById(R.id.date);
                time = view.findViewById(R.id.time);

            }

            @Override
            public void onClick(View v) {

            }
        }

        public my_reserveAdapter(ArrayList<my_reserveInfo> MyReserveInfoArrayList){
            this.MyReserveInfoArrayList = MyReserveInfoArrayList;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_my_reverse, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.place_no.setText(MyReserveInfoArrayList.get(position).place_no);
            holder.date.setText(MyReserveInfoArrayList.get(position).date);
            holder.time.setText(MyReserveInfoArrayList.get(position).time);
        }

        @Override
        public int getItemCount() {
            return MyReserveInfoArrayList.size();
        }
    }

    public void drawList() {
        MyReserveInfoArrayList.clear();
        try {
            JSONArray items = mResult.getJSONArray("list");

            for (int i = 0; i < items.length(); i++) {
                JSONObject info = items.getJSONObject(i);
                String userid = info.getString("userid");
                if(mSession.getID().equals(userid))
                {
                    String place_no = info.getString("place_no");
                    String date = info.getString("date");
                    String time = info.getString("time");
                    MyReserveInfoArrayList.add(new my_reserveInfo(place_no,date ,time));
                }
            }

        } catch (JSONException | NullPointerException e) {
            mResult = null;
        }

        mAdapter.notifyDataSetChanged();
    }
    private void requestReserve() {
        String url = SessionManager.getURL() + "users/select_my_reserve.php";
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
                            Toast.makeText(Mypage.this, "서버 에러",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.i(LOG_TAG, error.getMessage());
                            Toast.makeText(Mypage.this, error.getMessage(),
                                  Toast.LENGTH_LONG).show();
                        }
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }
    //---------------------------------------------------- 내가 작성한 recruit
    public class my_write_recruitInfo {
        public String category;
        public String place;
        public String date;
        public String time;
        public String totalnum;
        public String recruitnum;

        public my_write_recruitInfo(String category, String place,String date, String time, String totalnum, String recruitnum) {

            this.category = category;
            this.place = place;
            this.date = date;
            this.time = time;
            this.totalnum = totalnum;
            this.recruitnum = recruitnum;

        }

        public String getCategory() {
            return category;
        }

        public String getPlace() {
            return place;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public String getTotalnum() {
            return totalnum;
        }

        public String getRecruitnum() {
            return recruitnum;
        }
    }

    public class my_write_recAdapter extends RecyclerView.Adapter<my_write_recAdapter.ViewHolder>  {

        ArrayList<my_write_recruitInfo> MyWriteRecInfoArrayList = null;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView category;
            TextView place;
            TextView date;
            TextView time;
            TextView totalnum;
            TextView recruitnum;


            public ViewHolder(View view){
                super(view);
                view.setOnClickListener(this);
                category = view.findViewById(R.id.category);
                place = view.findViewById(R.id.place);
                date = view.findViewById(R.id.date);
                time = view.findViewById(R.id.time);
                totalnum = view.findViewById(R.id.totalnum);
                recruitnum = view.findViewById(R.id.recruitnum);
                /////////////////////

            }

            @Override
            public void onClick(View v) {

            }
        }

        public my_write_recAdapter(ArrayList<my_write_recruitInfo> MyWriteRecInfoArrayList){
            this.MyWriteRecInfoArrayList = MyWriteRecInfoArrayList;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_write_recruit, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.category.setText(MyWriteRecInfoArrayList.get(position).category);
            holder.place.setText(MyWriteRecInfoArrayList.get(position).place);
            holder.date.setText(MyWriteRecInfoArrayList.get(position).date);
            holder.time.setText(MyWriteRecInfoArrayList.get(position).time);
            holder.totalnum.setText(MyWriteRecInfoArrayList.get(position).totalnum);
            holder.recruitnum.setText(MyWriteRecInfoArrayList.get(position).recruitnum);
        }

        @Override
        public int getItemCount() {
            return MyWriteRecInfoArrayList.size();
        }
    }

    public void drawList2() {
        MyWriteRecInfoArrayList.clear();
        try {
            JSONArray items = mResult2.getJSONArray("list");

            for (int i = 0; i < items.length(); i++) {
                JSONObject info = items.getJSONObject(i);
                String userid = info.getString("userid");
                if(mSession.getID().equals(userid))
                {
                    String category = info.getString("category");
                    String place = info.getString("place");
                    String date = info.getString("date");
                    String time = info.getString("time");
                    String totalnum = info.getInt("total_num")+"";
                    String recruitnum = info.getInt("recruit_num")+"";

                    MyWriteRecInfoArrayList.add(new my_write_recruitInfo(category,place ,date,time,totalnum,recruitnum));
                }
            }

        } catch (JSONException | NullPointerException e) {
            mResult2 = null;
        }
        mAdapter2.notifyDataSetChanged();
    }
    private void requestWriteRecruit() {
        String url = SessionManager.getURL() + "recruit/select_recruit.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mResult2 = response;
                        drawList2();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getMessage() == null) {
                            Log.i(LOG_TAG, "서버 에러");
                            Toast.makeText(Mypage.this, "서버 에러",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.i(LOG_TAG, error.getMessage());
                            Toast.makeText(Mypage.this, error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue2.add(request);
    }
    //--------------------------------------------------- 내가 작성한 match
    public class my_write_matAdapter extends RecyclerView.Adapter<my_write_matAdapter.ViewHolder>  {

        ArrayList<my_write_recruitInfo> MyWriteMatInfoArrayList = null;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView category;
            TextView place;
            TextView date;
            TextView time;
            TextView teamname;
            TextView recruitnum;


            public ViewHolder(View view){
                super(view);
                view.setOnClickListener(this);
                category = view.findViewById(R.id.category);
                place = view.findViewById(R.id.place);
                date = view.findViewById(R.id.date);
                time = view.findViewById(R.id.time);
                teamname = view.findViewById(R.id.totalnum);
                recruitnum = view.findViewById(R.id.recruitnum);
                /////////////////////

            }

            @Override
            public void onClick(View v) {

            }
        }

        public my_write_matAdapter(ArrayList<my_write_recruitInfo> MyWriteMatInfoArrayList){
            this.MyWriteMatInfoArrayList = MyWriteMatInfoArrayList;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_write_recruit, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.category.setText(MyWriteMatInfoArrayList.get(position).category);
            holder.place.setText(MyWriteMatInfoArrayList.get(position).place);
            holder.date.setText(MyWriteMatInfoArrayList.get(position).date);
            holder.time.setText(MyWriteMatInfoArrayList.get(position).time);
            holder.teamname.setText(MyWriteMatInfoArrayList.get(position).totalnum);
            holder.recruitnum.setText(MyWriteMatInfoArrayList.get(position).recruitnum);
        }

        @Override
        public int getItemCount() {
            return MyWriteMatInfoArrayList.size();
        }
    }

    public void drawList3() {
        MyWriteMatInfoArrayList.clear();
        try {
            JSONArray items = mResult3.getJSONArray("list");

            for (int i = 0; i < items.length(); i++) {
                JSONObject info = items.getJSONObject(i);
                String userid = info.getString("userid");
                if(mSession.getID().equals(userid))
                {
                    String category = info.getString("category");
                    String place = info.getString("place");
                    String date = info.getString("date");
                    String time = info.getString("time");
                    String teamname = info.getString("team_name");
                    String recruitnum = info.getInt("recruit_num")+"";

                    MyWriteMatInfoArrayList.add(new my_write_recruitInfo(category,place ,date,time,teamname,recruitnum));
                }
            }

        } catch (JSONException | NullPointerException e) {
            mResult3 = null;
        }
        mAdapter3.notifyDataSetChanged();
    }
    private void requestWriteMatch() {
        String url = SessionManager.getURL() + "match/select_match.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mResult3 = response;
                        drawList3();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getMessage() == null) {
                            Log.i(LOG_TAG, "서버 에러");
                            Toast.makeText(Mypage.this, "서버 에러",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Log.i(LOG_TAG, error.getMessage());
                            Toast.makeText(Mypage.this, error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue3.add(request);
    }

}
