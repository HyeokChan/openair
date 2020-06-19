package com.example.myapplication;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;


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

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import static java.lang.StrictMath.abs;
import static java.lang.StrictMath.min;
import static android.app.Activity.RESULT_OK;


//getActivity().getApplicationContext() = 컨텍스트
public class Menu1Fragment extends Fragment {
    private LocationTracker gpslocation;
    private ApiExplorer weatherapi;



    // 미세먼지 데이터 받기 위해 선언
    public static final String LOG_TAG = "Menu1Fragment";
    public static final String QUEUE_TAG = "VolleyRequest";


    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;
    JSONObject mResult = null;
    ArrayList<finedustInfo> finedustInfoArrayList = new ArrayList<finedustInfo>();
    protected TomorrowAdapter mAdapter = new TomorrowAdapter(finedustInfoArrayList);
    SessionManager mSession = SessionManager.getInstance(getContext());

    protected RequestQueue mQueue2 = null;
    protected ImageLoader mImageLoader2 = null;
    JSONObject mResult2 = null;
    ArrayList<weatherInfo> weatherInfoArrayList = new ArrayList<weatherInfo>();
    protected WeatherAdapter mAdapter2 = new WeatherAdapter(weatherInfoArrayList);


    //fragment 에선 getView()- fragment의 root의 view를 불러오는 함수로 해결
    private TextView address;
    private Button sbtn;
    private ImageView wImage;
    private TextView wInfo1;
    private TextView wInfo2;
    private SwipeRefreshLayout swipe;


    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    ///////////////////////gps와 internet 상태 확인을 위한 변수 //////////////////////////

    public static final int GPS_DIALOG = 0;
    public static final int INTERNET_DIALOG = 1;
    public static final int REQUEST_CODE_MENU = 001;

    /////////////위경도 //////////////////
    double latitude;
    double longitude;

    ///////////날씨 변수 /////////////////
    rtweatherInfo w_Info;
    String leftstat;
    String rightstat;

    /////////////현재 내 위치 //////////
    String curMyAddress;   //현재 주소
    Location curMyLatLng;  //현재 위경도


    ////////////비교할 주변 측정소 위치 및 거리 ///////////
    Location nearList; //주변 측정소 리스트
    realtimeInfo destination;      //최종 측정소
    double distance;

    ///////////미세먼지 디비에서 가져올 데이터를 위한 변수 ///////////
    RequestQueue queue;
    JSONObject dustmResult = null;
    ArrayList<realtimeInfo> curInfoList = new ArrayList<>();  //관측소 정보 리스트

    class rtweatherInfo{
        String rain; // 강수상태
        String sky; //하늘상태
        String tem; //온도
        String hum; //습도
    }

    class realtimeInfo{ //관측소 정보
        String address;
        String pm10level;
        String pm25level;
        public realtimeInfo(String addr, String pm10, String pm25){
            this.address = addr;
            this.pm10level = pm10;
            this.pm25level = pm25;
        }
    }

    /////////////꾸미기용////////
    Animation appear;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_menu1, container, false);

        /**gps와 네트워크 연결 상태 확인 **/
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting(GPS_DIALOG);
        } else {
            checkRunTimePermission();
        }
        Log.i("GPS 서비스 확인", "OK");
        if(!checkInternetServicesStatus()){
            showDialogForLocationServiceSetting(INTERNET_DIALOG);
        }
        Log.i("INTERNET 서비스 확인", "OK");

        //꾸미기
        appear = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        //각 영역 뷰 가져옴
        swipe = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);
        address = (TextView) rootView.findViewById(R.id.location);
        sbtn = (Button) rootView.findViewById(R.id.sbtn);
        wImage = (ImageView) rootView.findViewById(R.id.w_icon);
        wInfo1 = (TextView) rootView.findViewById(R.id.weather1);
        wInfo2 = (TextView) rootView.findViewById(R.id.weather2);

        /*locationManager를 이용한 현재 위경도 받아오기*/
        gpslocation = new LocationTracker(getActivity());
        latitude = gpslocation.getLatitude();
        longitude = gpslocation.getLongitude();
        Log.i("확인", "gpstracker 확인");;

        /*지오코더를 이용하여 위경도 -> 주소 변환 */
        String addr = getAddress(getContext(), latitude, longitude);
        Log.i("ghkrdlsgkrl",addr);
        address.setText(addr);
        curMyAddress = addr;
        Log.i("확인", "지오코더 확인");


        // 받아온 주소를 공백단위로 자른다.

        String[] addr_token = addr.split(" ");
        String city;
        city = addr_token[0];
        if(city.length() == 5) {
            // 광역시일 경우 광역시를 제거한다.
            city = addr_token[0].substring(0, addr_token[0].length()-3);
        }
        Log.i("nowcity", city);


        if(addr.equals("위치 서비스 OFF")){ //gps off
            wInfo1.setText("현재 날씨를 확인할 수 없습니다.");
        }
        else if(addr.equals("인터넷 상태 OFF")){ //access fail
            wInfo1.setText("인터넷 상태를 확인해주세요.3");
        }
        /**위경도를 받아오면 현재 날씨 받아옴**/
        else{
            weatherapi = new ApiExplorer(latitude, longitude);
            ArrayList<String> list =  weatherapi.getWeather();
            Log.i("ApiExplorer(날씨 받아오기) 확인","OK");
            Log.i("날씨 데이터 개수 확인",Integer.toString(list.size()));
            Log.i("리스트개수",list.size()+"");
            if(list.size() == 0 ){  //인터넷 연결이 안되어있으면 null 값이 return 됨
                wInfo1.setText("인터넷 상태를 확인해주세요.");
                Log.i("인터넷 상태 확인","FAIL");
            }
            else{
                setWeather(list);
                Log.i("setWeather(날씨 설정) 확인","OK");

                /*미세먼지 데이터를 가져옴*/
                requestPlace();
                Log.i("requestPlace(미세먼지)확인","OK");
            }
        }

        /*지도에서 위치를 설정할 수 있도록 함*/
        sbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkInternetServicesStatus()){
                    showDialogForLocationServiceSetting(INTERNET_DIALOG);
                }
                else{
                    //현재 내 위치값을 전송
                    Intent intent = new Intent(getContext(), Map.class);
                    Log.i("위경도 확인", Double.toString(latitude) );
                    Log.i("위경도 확인", Double.toString(longitude) );
                    intent.putExtra("lat",latitude); /*송신*/
                    intent.putExtra("lng",longitude);
                    startActivityForResult(intent, REQUEST_CODE_MENU);
                }
            }
        });

        swipe.setColorSchemeResources(
                android.R.color.holo_orange_light,
                android.R.color.holo_blue_light
        );
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                swipe.setRefreshing(false);
            }
        });



        //---------여기서부터---------
        final RecyclerView mRecyclerView = (RecyclerView)rootView.findViewById(R.id.tRecycler);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        final RecyclerView mRecyclerView2 = (RecyclerView)rootView.findViewById(R.id.WeatherRecycler);
        mRecyclerView2.setAdapter(mAdapter2);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this.getContext());
        layoutManager2.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView2.setLayoutManager(layoutManager2);
        mRecyclerView2.setHasFixedSize(true);

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

        mQueue2 = Volley.newRequestQueue(this.getContext());
        mImageLoader2 = new ImageLoader(mQueue2,
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
        mQueue2 = mSession.getQueue();
        Log.i("이이",city);
        requestTomorrowfd(city);
        requestWeather(city);

        return rootView;
    }


    ///////////
    public class TomorrowAdapter extends RecyclerView.Adapter<TomorrowAdapter.ViewHolder> {

        ArrayList<finedustInfo> finedustInfoArrayList = null;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvTime;
            NetworkImageView ivImage1;
            TextView tvStatus1;
            NetworkImageView ivImage2;
            TextView tvStatus2;

            public ViewHolder(View view){
                super(view);
                view.setOnClickListener(this);
                tvTime = (TextView) view.findViewById(R.id.ttime);
                ivImage1 = (NetworkImageView)view.findViewById(R.id.l10_timage);
                tvStatus1 = (TextView) view.findViewById(R.id.l10_status);
                ivImage2 = (NetworkImageView)view.findViewById(R.id.l25_timage);
                tvStatus2 = (TextView) view.findViewById(R.id.l25_status);
            }

            @Override
            public void onClick(View v) {
                //
            }
        }

        public TomorrowAdapter(ArrayList<finedustInfo> finedustInfoArrayList){
            this.finedustInfoArrayList = finedustInfoArrayList;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_col_tomorrow, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.tvTime.setText(finedustInfoArrayList.get(position).time);
            holder.ivImage1.setImageUrl(SessionManager.getURL() + "weatherImage/" + finedustInfoArrayList.get(position).pm10_image, mImageLoader);
            holder.tvStatus1.setText(finedustInfoArrayList.get(position).pm10);
            holder.ivImage2.setImageUrl(SessionManager.getURL() + "weatherImage/" + finedustInfoArrayList.get(position).pm25_image, mImageLoader);
            holder.tvStatus2.setText(finedustInfoArrayList.get(position).pm25);
        }

        @Override
        public int getItemCount() {
            return finedustInfoArrayList.size();
        }
    }

    // city 경상북도
    private void requestTomorrowfd(final String city) {
        String url = SessionManager.getURL() + "finedust/select_finedust.php?city=" + city;
        Log.i("urltest", url + "     " + city);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mResult = response;
                        finedustInfoArrayList.clear();
                        try {
                            JSONArray items = mResult.getJSONArray("list");
                            Log.i("jebal", items.toString());
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject info = items.getJSONObject(i);
                                // 초기화
                                String pm10_image = "verybad2.png";
                                String pm25_image = "verybad2.png";
                                //
                                String time = info.getString("TIME");

                                String pm10 = info.getString("PM10level");

                                if(pm10.equals("좋음")) pm10_image = "good2.png";
                                else if(pm10.equals("보통")) pm10_image = "normal2.png";
                                else if(pm10.equals("한때 나쁨")) pm10_image = "somebad2.png";
                                else if(pm10.equals("나쁨")) pm10_image = "bad2.png";
                                else pm10 = "매우 나쁨";

                                String pm25 = info.getString("PM25level");
                                if(pm25.equals("좋음")) pm25_image = "good2.png";
                                else if(pm25.equals("보통")) pm25_image = "normal2.png";
                                else if(pm25.equals("한때 나쁨")) pm25_image = "somebad2.png";
                                else if(pm25.equals("나쁨")) pm25_image = "bad2.png";
                                else pm25 = "매우 나쁨";
                                Log.i("jebal", time + "   " + pm10 + "    " + pm25 + "    " + pm10_image + pm25_image);

                                finedustInfoArrayList.add(new finedustInfo(time, pm10_image, pm10, pm25_image, pm25));
                            }

                        } catch (JSONException | NullPointerException e) {
                            Toast.makeText(getContext(),
                                    "존재하지 않습니다.", Toast.LENGTH_LONG).show();
                            mResult = null;
                        }
                        mAdapter.notifyDataSetChanged();
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
                        }
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }

    public class finedustInfo {
        public String time;
        public String pm10_image;
        public String pm10;
        public String pm25_image;
        public String pm25;

        public finedustInfo(String time, String pm10_image, String pm10, String pm25_image, String pm25) {
            this.time = time;
            this.pm10_image = pm10_image;
            this.pm10 = pm10;
            this.pm25_image = pm25_image;
            this.pm25 = pm25;
        }

        public String getTime() {
            return time;
        }

        public String getPm10_image() {
            return pm10_image;
        }

        public String getPm10() {
            return pm10;
        }

        public String getPm25_image() {
            return pm25_image;
        }

        public String getPm25() {
            return pm25;
        }
    }

///////////////////////////////////////////

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
                date = (TextView) view.findViewById(R.id.date);
                time = (TextView) view.findViewById(R.id.time);
                PM10dust = (TextView) view.findViewById(R.id.PM10dust);
                ivImage = (NetworkImageView)view.findViewById(R.id.WeatherImage);
                PM25dust = (TextView) view.findViewById(R.id.PM25dust);
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
                holder.ivImage.setImageUrl(SessionManager.getURL() + "weatherImage/good2.png", mImageLoader2);
            }
            else if(holder.PM10dust.getText().toString().equals("보통"))
            {
                holder.ivImage.setImageUrl(SessionManager.getURL() + "weatherImage/normal2.png", mImageLoader2);
            }
            else if(holder.PM10dust.getText().toString().equals("한때 나쁨"))
            {
                holder.ivImage.setImageUrl(SessionManager.getURL() + "weatherImage/somebad2.png", mImageLoader2);
            }
            else if(holder.PM10dust.getText().toString().equals("나쁨"))
            {
                holder.ivImage.setImageUrl(SessionManager.getURL() + "weatherImage/bad2.png", mImageLoader2);
            }
            else if(holder.PM10dust.getText().toString().equals("매우 나쁨"))
            {
                holder.ivImage.setImageUrl(SessionManager.getURL() + "weatherImage/verybad2.png", mImageLoader2);
            }

            holder.PM25dust.setText(weatherInfoArrayList.get(position).PM25level);
            if(holder.PM25dust.getText().toString().equals("좋음"))
            {
                holder.ivImage2.setImageUrl(SessionManager.getURL() + "weatherImage/good2.png", mImageLoader2);
            }
            else if(holder.PM25dust.getText().toString().equals("보통"))
            {
                holder.ivImage2.setImageUrl(SessionManager.getURL() + "weatherImage/normal2.png", mImageLoader2);
            }
            else if(holder.PM25dust.getText().toString().equals("한때 나쁨"))
            {
                holder.ivImage2.setImageUrl(SessionManager.getURL() + "weatherImage/somebad2.png", mImageLoader2);
            }
            else if(holder.PM25dust.getText().toString().equals("나쁨"))
            {
                holder.ivImage2.setImageUrl(SessionManager.getURL() + "weatherImage/bad2.png", mImageLoader2);
            }
            else if(holder.PM25dust.getText().toString().equals("매우 나쁨"))
            {
                holder.ivImage2.setImageUrl(SessionManager.getURL() + "weatherImage/verybad2.png", mImageLoader2);
            }
        }

        @Override
        public int getItemCount() {
            return weatherInfoArrayList.size();
        }
    }

    private void requestWeather(final String city) {
        String url = SessionManager.getURL() + "finedust/select_weather.php?city=" + city;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mResult2 = response;
                        weatherInfoArrayList.clear();
                        try {
                            JSONArray items = mResult2.getJSONArray("list");

                            for (int i = 0; i < items.length(); i++) {
                                JSONObject info = items.getJSONObject(i);
                                String date = info.getString("DATE");
                                String time = info.getString("TIME");
                                String PM10level = info.getString("PM10level");
                                String PM25level = info.getString("PM25level");

                                weatherInfoArrayList.add(new weatherInfo(date, time, PM10level, PM25level));
                            }

                        } catch (JSONException | NullPointerException e) {
                            Toast.makeText(getContext(),
                                    "일별 예보가 존재하지 않습니다..", Toast.LENGTH_LONG).show();
                            mResult2 = null;
                        }

                        mAdapter2.notifyDataSetChanged();
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
        mQueue2.add(request);
    }


    /**지도에서 위치를 설정하게 되면 현재 위경도와 날씨 갱신**/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_MENU){
            if(resultCode == RESULT_OK){
                Log.i("Intent Google map 변경 확인","OK");
                Toast.makeText(getContext(), "변경되었습니다.", Toast.LENGTH_SHORT).show();
                String changeaddr = data.getStringExtra("address");

                address.setText(changeaddr);
                curMyAddress = changeaddr;



                Location loc = getPoint(getContext(), changeaddr); //위경도를 다시 주소로 변환하는 함수
                Log.i("지오코더(주소->위경도) 확인","OK");

                latitude = loc.getLatitude();
                longitude = loc.getLongitude();
                Log.i("위경도 확인", Double.toString(latitude) );
                Log.i("위경도 확인", Double.toString(longitude) );

                weatherapi = new ApiExplorer(latitude, longitude);
                ArrayList<String> list = weatherapi.getWeather();
                if(list.size() == 0 ){  //인터넷 연결이 안되어있으면 null 값이 return 됨
                    wInfo1.setText("인터넷 상태를 확인해주세요.1");
                    wInfo2.setText("");
                    wImage.setImageResource(0);
                    Log.i("인터넷 상태 확인","FAIL");
                }
                else{
                    setWeather(list);
                    requestPlace();

                    Log.i("Intent setWeather 확인","OK");

                }

            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(QUEUE_TAG);
        }
    }

    //위경도 -> 주소
    public String getAddress(Context mContext, double lat, double lng) {
        String nowAddress = "위치 서비스 OFF";
        Geocoder geocoder = new Geocoder(mContext, Locale.KOREA);
        List<Address> address;
        try {
            if (geocoder != null) {
                //세번째 파라미터는 좌표에 대해 주소를 리턴 받는 갯수로
                //한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 최대갯수 설정
                address = geocoder.getFromLocation(lat, lng, 1);

                if (address != null && address.size() > 0) { //이 조건문 아니면 에러남
                    // 주소 받아오기
                    String currentLocationAddress = address.get(0).getAddressLine(0).toString();
                    currentLocationAddress = currentLocationAddress.replaceFirst("대한민국 ", "");
                    nowAddress = currentLocationAddress;
                    Log.i("mpowew",nowAddress);
                }
            }
        } catch (IOException e) {
            nowAddress = "인터넷 상태 OFF";
            e.printStackTrace();
        }
        return nowAddress;
    }

    //주소 -> 위경도
    public static Location getPoint(Context mContext, String address){
        Location loc = new Location("");
        Geocoder coder = new Geocoder(mContext);
        List<Address> addr = null;// 한좌표에 대해 두개이상의 이름이 존재할수있기에 주소배열을 리턴받기 위해 설정

        try {
            addr = coder.getFromLocationName(address, 1);
        } catch (IOException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }// 몇개 까지의 주소를 원하는지 지정 1~5개 정도가 적당
        if (addr != null && addr.size() > 0) {
            Address lating = addr.get(0);
            double lat = lating.getLatitude(); // 위도가져오기
            double lon = lating.getLongitude(); // 경도가져오기
            loc.setLatitude(lat);
            loc.setLongitude(lon);
        }
        return loc;
    }

    //현재 위치서비스 상태 확인
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    //현재 인터넷서비스 상태 확인
    /*public boolean checkInternetServicesStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo state = connectivityManager.getActiveNetworkInfo();

        if(state != null && state.isConnected() && state.getType() == ConnectivityManager.TYPE_WIFI)
            return true;
        else return false;
    }*/
    public boolean checkInternetServicesStatus() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo state = connectivityManager.getActiveNetworkInfo();

        if(state != null && state.isConnected())
            return true;
        else return false;
    }

    //gps, INTERNET 활성화 다이얼로그 생성
    private void showDialogForLocationServiceSetting(int mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if(mode == GPS_DIALOG){
            builder.setTitle("위치 서비스 비활성화");
            builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.");
            builder.setCancelable(false);
            builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

        }
        else if (mode == INTERNET_DIALOG){
            builder.setTitle("인터넷 서비스 비활성화");
            builder.setMessage("앱을 사용하기 위해서는 무선네트워크 연결이 필요합니다.");
            builder.setCancelable(false);
            builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    Intent callGPSSettingIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(callGPSSettingIntent);
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        }
        builder.create().show();
    }

    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)

            // 3.  위치 값을 가져올 수 있음

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(getActivity(), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
            } else {
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }


    public void setWeather(ArrayList<String> list){
        w_Info = new rtweatherInfo();

        String rainstat = list.get(0); // 강수상태
        String skystat = list.get(1); //하늘상태
        w_Info.tem = list.get(2); //온도
        w_Info.hum = list.get(3); //습도

        w_Info.sky = null;
        w_Info.rain = null;

        switch(rainstat){
            case "0" :
                w_Info.rain = "강수 : 없음";
                break;
            case "1" :
                w_Info.rain = "강수 : 비";
                break;
            case "2" :
                w_Info.rain = "강수 : 비/눈";
                break;
            case "3" :
                w_Info.rain = "강수 : 눈";
                break;
            case "4":
                w_Info.rain = "강수 : 소나기";
                break;
        }
        switch(skystat) {
            case "1":
                w_Info.sky = "하늘 : 맑음";
                break;

            case "3":
                w_Info.sky = "하늘 : 구름많음";
                break;

            case "4":
                w_Info.sky = "하늘 : 흐림";
                break;
        }

        leftstat = null;
        rightstat = null;
        Log.i("확인", w_Info.tem + w_Info.hum + w_Info.sky + w_Info.rain);

        if(rainstat.equals("0")){
            switch(skystat){
                case "1":
                    leftstat = w_Info.sky + "\n" + w_Info.tem;
                    rightstat = w_Info.rain + "\n" + w_Info.hum;
                    wImage.setImageResource(R.drawable.sunny);
                    wImage.setAnimation(appear);
                    break;

                case "3":
                    leftstat = w_Info.sky + "\n" + w_Info.tem;
                    rightstat = w_Info.rain + "\n" + w_Info.hum;
                    wImage.setImageResource(R.drawable.cloud);
                    wImage.setAnimation(appear);
                    break;

                case "4":
                    leftstat = w_Info.sky + "\n" + w_Info.tem;
                    rightstat = w_Info.rain + "\n" + w_Info.hum;
                    wImage.setImageResource(R.drawable.badcloud);
                    wImage.setAnimation(appear);
                    break;
            }
        }
        else{
            switch (rainstat){
                case "1":

                    leftstat = w_Info.sky + "\n" + w_Info.tem;
                    rightstat = w_Info.rain + "\n" + w_Info.hum;
                    wImage.setImageResource(R.drawable.rainny);
                    wImage.setAnimation(appear);
                    break;
                case "2":
                    leftstat = w_Info.sky + "\n" + w_Info.tem;
                    rightstat = w_Info.rain + "\n" + w_Info.hum;
                    wImage.setImageResource(R.drawable.snowandrain);
                    wImage.setAnimation(appear);
                    break;
                case "3":
                    leftstat = w_Info.sky + "\n" + w_Info.tem;
                    rightstat = w_Info.rain + "\n" + w_Info.hum;
                    wImage.setImageResource(R.drawable.snow);
                    wImage.setAnimation(appear);
                    break;
                case "4":
                    leftstat = w_Info.sky + "\n" + w_Info.tem;
                    rightstat = w_Info.rain + "\n" + w_Info.hum;
                    wImage.setImageResource(R.drawable.shower);
                    wImage.setAnimation(appear);
                    break;
            }
        }
        Log.i("확인", "setWeather");
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return (dist);
    }
    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    void refresh(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.detach(this).attach(this).commit();
    }

    public void drawList() {
        double min_dis;
        curInfoList.clear();
        try {
            JSONArray items = dustmResult.getJSONArray("list");

            Log.i("servertest2" , "수신완료");
            for (int i = 0; i < items.length(); i++) {
                JSONObject info = items.getJSONObject(i);

                String address = info.getString("address");
                String pm10level = info.getString("PM10level");

                Log.i("servertest3", address + "    " + pm10level);
                switch(pm10level){
                    case "1": pm10level = "좋음"; break;
                    case "2": pm10level = "보통"; break;
                    case "3": pm10level = "나쁨"; break;
                    case "4": pm10level = "매우나쁨"; break;
                }
                String pm25level = info.getString("PM25level");
                switch(pm25level){
                    case "1": pm25level = "좋음"; break;
                    case "2": pm25level = "보통"; break;
                    case "3": pm25level = "나쁨"; break;
                    case "4": pm25level = "매우나쁨"; break;
                }
                curInfoList.add(new realtimeInfo(address, pm10level, pm25level));  //이 클래스에 장소, 미세먼지, 초미세먼지 받아옴
            }

        } catch (JSONException | NullPointerException e) {
            Toast.makeText(getContext(), "실패", Toast.LENGTH_LONG).show();
            dustmResult = null;
        }

        min_dis = 1000000000;
        curMyLatLng = getPoint(getContext(), curMyAddress);

        for(int i = 0; i<curInfoList.size(); i++){ //가장 가까운 관측소
            nearList = getPoint(getContext(), curInfoList.get(i).address);
            Log.i("위경도", Double.toString(nearList.getLatitude()) + "     "  + Double.toString(nearList.getLongitude()));
            distance = distance(curMyLatLng.getLatitude(), curMyLatLng.getLongitude(), nearList.getLatitude(), nearList.getLongitude());

            Log.i("확인", "속도체크");
            if(min_dis>abs(distance))
            {
                min_dis = distance;
                destination = curInfoList.get(i); //최종 가장 가까운 관측소의 장소, 미세먼지, 초미세먼지 변수
            }
        }

        appear = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        wInfo1.setAnimation(appear);
        wInfo2.setAnimation(appear);
        //미세먼지
        wInfo1.setText(leftstat+"\n"+ "미세먼지 : " + destination.pm10level);
        //초미세먼지
        wInfo2.setText(rightstat+"\n"+ "초미세먼지 : "+ destination.pm25level);
        Log.i("확인", leftstat.toString());
    }

    public void requestPlace(){
        Log.i("servertest0" , "시작");
        queue = Volley.newRequestQueue(getContext());
        //String url = getIP(); 나중에 만들어쓰기   + token[1] + "%";
        String url = "http://rnjsgur12.cafe24.com/select_realtime.php?addr=";
        //String url = "http://rnjsgur12.cafe24.com/select_realtime.php?ADDR=%경북 구미시%";
        Log.i("servertest0" , "test0");

        Log.i("servertest0" , curMyAddress);

        Log.i("servertest0" , "test3");
        String[] token = curMyAddress.split(" ");
        switch (token[0]) {
            case "서울특별시":
                token[0] = "서울";
                url = url + token[0] + " " + token[1] + "%";
                break;
            case "인천광역시":
                token[0] = "인천";
                if (token[1].equals("옹진군")) token[1] = "중구";
                url = url + token[0] + " " + token[1] + "%";
                break;
            case "경기도":
                token[0] = "경기";
                url = url + token[0] + " " + token[1] + "%";
                break;
            case "대구광역시":
                token[0] = "대구";
                url = url + token[0] + " " + token[1] + "%";
                break;
            case "제주특별자치도":
                token[0] = "제주";
                url = url + token[0] + " " + token[1] + "%";
                break;
            case "부산광역시":
                token[0] = "부산";
                url = url + token[0] + " " + token[1] + "%";
                ;
                break;
            case "대전광역시":
                token[0] = "대전";
                url = url + token[0] + " " + token[1] + "%";
                break;
            case "광주광역시":
                token[0] = "광주";
                url = url + token[0] + " " + token[1] + "%";
                break;
            case "세종특별자치시":
                token[0] = "세종";
                url = url + token[0] + "%";
                break;
            case "울산광역시":
                token[0] = "울산";
                url = url + token[0] + " " + token[1] + "%";
                ;
                break;
            case "경상남도":
                token[0] = "경남";
                if (token[1].equals("창녕군") || token[1].equals("의령군")) token[1] = "함안군";
                else if (token[1].equals("합천군")) token[1] = "거창군";
                else if (token[1].equals("산청군")) token[1] = "함양군";
                url = url + token[0] + " " + token[1] + "%";
                break;
            case "경상북도":
                token[0] = "경북";
                ;
                if (token[1].equals("영덕군") || token[1].equals("영양군") || token[1].equals("청송군"))
                    token[1] = "안동시";
                else if (token[1].equals("울릉군")) token[1] = "울진군";
                else if (token[1].equals("성주군")) token[1] = "칠곡군";
                else if (token[1].equals("고령군")) token[1] = "달성군";
                else if (token[1].equals("청도군")) token[1] = "경산군";
                else if (token[1].equals("군위군")) token[1] = "구미시";
                else if (token[1].equals("의성군")) token[1] = "상주시";
                else if (token[1].equals("예천군") || token[1].equals("문경시")) token[1] = "영주시";
                url = url + token[0] + " " + token[1] + "%";
                break; //예천, 문경시
            case "강원도":
                token[0] = "강원";
                url = url + token[0] + " " + token[1] + "%";
                break;
            case "충청북도":
                token[0] = "충북";
                url = url + token[0] + " " + token[1] + "%";
                break;
            case "충청남도":
                token[0] = "충남";
                url = url + token[0] + " " + token[1] + "%";
                break;
            case "전라북도":
                token[0] = "전북";
                url = url + token[0] + " " + token[1] + "%";
                break;
            case "전라남도":
                token[0] = "전남";
                if (token[1].equals("곡성군") || token[1].equals("구례군")) token[1] = "담양군";
                url = url + token[0] + " " + token[1] + "%";
                break;
        }

        Log.i("servertest0" , "test4");
        Log.i("url확인", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dustmResult = response;

                        drawList();
                        Log.i("확인", "drawList");
                        Log.i("servertest1" , "전송완료");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.getMessage() == null) {
                            Log.i(LOG_TAG, "서버 에러");
                            Toast.makeText(getContext(), "서버 에러", Toast.LENGTH_LONG).show();
                            Log.i("servertest1" , "서버 에러");
                        }
                        else {
                            Log.i(LOG_TAG, error.getMessage());
                            Log.i("servertest1" , error.getMessage());
                        }
                    }
                });
        Log.i("servertestre", request.toString());
        request.setTag(QUEUE_TAG);
        queue.add(request);

    }

}