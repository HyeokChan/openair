package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;

public class Menu1Fragment extends Fragment {

    public static final String LOG_TAG = "Menu2Fragment";
    public static final String QUEUE_TAG = "VolleyRequest";
    SessionManager mSession = SessionManager.getInstance(getContext());
    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;
    JSONObject mResult = null;
    ArrayList<weatherInfo> weatherInfoArrayList = new ArrayList<weatherInfo>();
    protected WeatherAdapter mAdapter = new WeatherAdapter(weatherInfoArrayList);


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup) inflater.inflate(R.layout.fragment_menu1, container, false);

        final RecyclerView mRecyclerView = (RecyclerView)v.findViewById(R.id.WeatherRecycler);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setHasFixedSize(true);

        mQueue = Volley.newRequestQueue(this.getContext());
        mImageLoader = new ImageLoader(mQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);
                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });

        CookieHandler.setDefault(new CookieManager());

        mQueue = mSession.getQueue();
        requestWeather();
        return v;

    }

    public class weatherInfo {
        public String date; // 야외활동지 분류
        public String time; // 야외활동지 분류
        public String PM10level;
        public String PM25level;

        public weatherInfo(String date, String time, String PM10level, String PM25level) {
            this.date = date;
            this.time = time;
            this.PM10level = PM10level;
            this.PM25level = PM25level;
        }

        public String getDate() {
            return date;
        }

        public String getTime() {
            return time;
        }

        public String getPM10level () {
            return PM10level;
        }
        public String getPM25level () {
            return PM25level;
        }
    }

    public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {

        ArrayList<weatherInfo> weatherInfoArrayList = null;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView date;
            TextView time;
            NetworkImageView ivImage;
            TextView PM10dust;
            NetworkImageView ivImage2;
            TextView PM25dust;

            public ViewHolder(View view){
                super(view);
                view.setOnClickListener(this);
                date = view.findViewById(R.id.date);
                time = view.findViewById(R.id.time);
                PM10dust = view.findViewById(R.id.PM10dust);
                ivImage = (NetworkImageView)view.findViewById(R.id.WeatherImage);
                PM25dust = view.findViewById(R.id.PM25dust);
                ivImage2 = (NetworkImageView)view.findViewById(R.id.WeatherImage2);

            }
            @Override
            public void onClick(View v) {

            }
        }

        public WeatherAdapter(ArrayList<weatherInfo> weatherInfoArrayList){
            this.weatherInfoArrayList = weatherInfoArrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_weather, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.date.setText(weatherInfoArrayList.get(position).date);
            holder.time.setText(weatherInfoArrayList.get(position).time);
            holder.PM10dust.setText(weatherInfoArrayList.get(position).PM10level);
            if(holder.PM10dust.getText().toString().equals("좋음"))
            {
                holder.ivImage.setImageUrl(SessionManager.getURL() + "weatherImage/good.png", mImageLoader);
            }
            else if(holder.PM10dust.getText().toString().equals("보통"))
            {
                holder.ivImage.setImageUrl(SessionManager.getURL() + "weatherImage/nomal.png", mImageLoader);
            }
            else if(holder.PM10dust.getText().toString().equals("한때 나쁨"))
            {
                holder.ivImage.setImageUrl(SessionManager.getURL() + "weatherImage/somebad.png", mImageLoader);
            }
            else if(holder.PM10dust.getText().toString().equals("나쁨"))
            {
                holder.ivImage.setImageUrl(SessionManager.getURL() + "weatherImage/bad.png", mImageLoader);
            }
            else if(holder.PM10dust.getText().toString().equals("매우 나쁨"))
            {
                holder.ivImage.setImageUrl(SessionManager.getURL() + "weatherImage/verybad.png", mImageLoader);
            }

            holder.PM25dust.setText(weatherInfoArrayList.get(position).PM25level);
            if(holder.PM25dust.getText().toString().equals("좋음"))
            {
                holder.ivImage2.setImageUrl(SessionManager.getURL() + "weatherImage/good.png", mImageLoader);
            }
            else if(holder.PM25dust.getText().toString().equals("보통"))
            {
                holder.ivImage2.setImageUrl(SessionManager.getURL() + "weatherImage/nomal.png", mImageLoader);
            }
            else if(holder.PM25dust.getText().toString().equals("한때 나쁨"))
            {
                holder.ivImage2.setImageUrl(SessionManager.getURL() + "weatherImage/somebad.png", mImageLoader);
            }
            else if(holder.PM25dust.getText().toString().equals("나쁨"))
            {
                holder.ivImage2.setImageUrl(SessionManager.getURL() + "weatherImage/bad.png", mImageLoader);
            }
            else if(holder.PM25dust.getText().toString().equals("매우 나쁨"))
            {
                holder.ivImage2.setImageUrl(SessionManager.getURL() + "weatherImage/verybad.png", mImageLoader);
            }
        }

        @Override
        public int getItemCount() {
            return weatherInfoArrayList.size();
        }


    }

    public void drawList() {
        weatherInfoArrayList.clear();
        try {
            JSONArray items = mResult.getJSONArray("list");

            for (int i = 0; i < items.length(); i++) {
                JSONObject info = items.getJSONObject(i);
                String date = info.getString("date");
                String time = info.getString("time");
                String PM10level = info.getString("PM10level");
                String PM25level = info.getString("PM25level");

                weatherInfoArrayList.add(new weatherInfo(date, time, PM10level, PM25level));
            }

        } catch (JSONException | NullPointerException e) {
            Toast.makeText(getContext(),
                    "일별 예보가 존재하지 않습니다..", Toast.LENGTH_LONG).show();
            mResult = null;
        }

        mAdapter.notifyDataSetChanged();
    }

    private void requestWeather() {
        String url = SessionManager.getURL() + "weatherImage/select_weather.php";
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
                            Toast.makeText(getContext(), "서버 에러",
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