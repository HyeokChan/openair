package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import java.security.PrivateKey;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

class Nick_Phone{
    private String Nick;
    private String Phone;
    public Nick_Phone(){}
    public Nick_Phone(String Nick, String Phone)
    {
        this.Nick = Nick;
        this.Phone = Phone;
    }
    public String getNick(){
        return Nick;
    }
    public String getPhone(){
        return Phone;
    }
}

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

    ArrayList<Nick_Phone> applicantId = new ArrayList<Nick_Phone>();
    TextView[] textViews;

    //-----------------------------------------
    protected RequestQueue mQueue3 = null;
    JSONObject mResult3 = null;
    ArrayList<my_write_recruitInfo> MyWriteMatInfoArrayList = new ArrayList<my_write_recruitInfo>();
    protected my_write_matAdapter mAdapter3 = new my_write_matAdapter(MyWriteMatInfoArrayList);
    //-----------------------------------------
    ArrayList<Integer> communityNo = new ArrayList<>();
    protected RequestQueue mQueue4 = null;
    JSONObject mResult4 = null;
    ArrayList<my_write_recruitInfo> MyJoinedRecInfoArrayList = new ArrayList<my_write_recruitInfo>();
    protected my_joined_recAdapter mAdapter4 = new my_joined_recAdapter(MyJoinedRecInfoArrayList);

    //----------------------------현재 시간
    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
    String currentDate;

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
        //-------------------------------------
        RecyclerView mRecyclerView4 = (RecyclerView)findViewById(R.id.joined_rec_recy); /////
        mRecyclerView4.setAdapter(mAdapter4);
        mRecyclerView4.setLayoutManager(new LinearLayoutManager(Mypage.this));
        mRecyclerView4.setHasFixedSize(true);
        mQueue4 = mSession.getQueue();
        requestCheckRecruit();
        //requestJoinedRecruit();

        //------------------현재 날짜
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        currentDate = mFormat.format(mDate);
        Log.i("datetest",currentDate);
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

                    String compareDateReserve = date.replace("-","");
                    if (Integer.parseInt(currentDate)<= Integer.parseInt(compareDateReserve))
                    {
                        MyReserveInfoArrayList.add(new my_reserveInfo(place_no,date ,time));
                    }
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
        public int recno;
        public String category;
        public String place;
        public String date;
        public String time;
        public String totalnum;
        public String recruitnum;

        public my_write_recruitInfo(int recno, String category, String place,String date, String time, String totalnum, String recruitnum) {
            this.recno = recno;
            this.category = category;
            this.place = place;
            this.date = date;
            this.time = time;
            this.totalnum = totalnum;
            this.recruitnum = recruitnum;

        }

        public int getRecno() {
            return recno;
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
            TextView recno;
            TextView category;
            TextView place;
            TextView date;
            TextView time;
            TextView totalnum;
            TextView recruitnum;


            public ViewHolder(View view){
                super(view);
                view.setOnClickListener(this);
                recno = view.findViewById(R.id.rec_no);
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
                requestCheckRecruit2(recno.getText().toString());

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
            holder.recno.setText(MyWriteRecInfoArrayList.get(position).recno+"");
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
                    int recno = info.getInt("rec_no");
                    String category = info.getString("category");
                    String place = info.getString("place");
                    String date = info.getString("date");
                    String time = info.getString("time");
                    String totalnum = info.getInt("total_num")+"";
                    String recruitnum = info.getInt("recruit_num")+"";

                    String compareDateRecruit = date.replace("-","");
                    if (Integer.parseInt(currentDate) <= Integer.parseInt(compareDateRecruit))
                    {
                        MyWriteRecInfoArrayList.add(new my_write_recruitInfo(recno,category,place ,date,time,totalnum,recruitnum));
                    }
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
            TextView matno;
            TextView category;
            TextView place;
            TextView date;
            TextView time;
            TextView teamname;
            TextView recruitnum;


            public ViewHolder(View view){
                super(view);
                view.setOnClickListener(this);
                matno = view.findViewById(R.id.rec_no);
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
            holder.matno.setText(MyWriteMatInfoArrayList.get(position).recno+"");
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
                    int matno = info.getInt("mat_no");
                    String category = info.getString("category");
                    String place = info.getString("place");
                    String date = info.getString("date");
                    String time = info.getString("time");
                    String teamname = info.getString("team_name");
                    String recruitnum = info.getInt("recruit_num")+"";

                    String compareDateMatch = date.replace("-","");
                    if (Integer.parseInt(currentDate) <= Integer.parseInt(compareDateMatch))
                    {
                        MyWriteMatInfoArrayList.add(new my_write_recruitInfo(matno ,category,place ,date,time,teamname,recruitnum));
                    }
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
    //---------------------------------------------------- 등록된 recruit 체크

    public void check_drawList() {
        try {

            JSONArray items = mResult.getJSONArray("list");
            communityNo.clear();
            for (int i = 0; i < items.length(); i++) {
                JSONObject info = items.getJSONObject(i);
                int community_no = info.getInt("community_no");
                int applicant_id = info.getInt("applicant_id");
                if(Integer.parseInt(mSession.getID()) == applicant_id)
                {
                    communityNo.add(community_no);
                    //Log.i("확인체크숫자",community_no+"");
                }

            }


            requestJoinedRecruit();

        } catch (JSONException | NullPointerException e) {
            mResult = null;
        }
    }


    private void requestCheckRecruit() {

        String url = SessionManager.getURL() + "recruit/select_applicant.php";
        Log.i("ㅅㅅㅅ","확인체크3");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mResult = response;
                        check_drawList();
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
                            //Toast.makeText(getContext(), error.getMessage(),
                            //      Toast.LENGTH_LONG).show();
                        }
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }


    //---------------------------------------------------- 내가 등록된 recruit
    public class my_joined_recruitInfo {
        public int joino;
        public String category;
        public String place;
        public String date;
        public String time;
        public String totalnum;
        public String recruitnum;

        public my_joined_recruitInfo(int joino, String category, String place,String date, String time, String totalnum, String recruitnum) {
            this.joino = joino;
            this.category = category;
            this.place = place;
            this.date = date;
            this.time = time;
            this.totalnum = totalnum;
            this.recruitnum = recruitnum;

        }

        public int getJoino()
        {
            return joino;
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

    public class my_joined_recAdapter extends RecyclerView.Adapter<my_joined_recAdapter.ViewHolder>  {

        ArrayList<my_write_recruitInfo> MyJoinedRecInfoArrayList = null;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView joino;
            TextView category;
            TextView place;
            TextView date;
            TextView time;
            TextView totalnum;
            TextView recruitnum;


            public ViewHolder(View view){
                super(view);
                view.setOnClickListener(this);
                joino = view.findViewById(R.id.rec_no);
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

        public my_joined_recAdapter(ArrayList<my_write_recruitInfo> MyJoinedRecInfoArrayList){
            this.MyJoinedRecInfoArrayList = MyJoinedRecInfoArrayList;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_write_recruit, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.joino.setText(MyJoinedRecInfoArrayList.get(position).recno+"");
            holder.category.setText(MyJoinedRecInfoArrayList.get(position).category);
            holder.place.setText(MyJoinedRecInfoArrayList.get(position).place);
            holder.date.setText(MyJoinedRecInfoArrayList.get(position).date);
            holder.time.setText(MyJoinedRecInfoArrayList.get(position).time);
            holder.totalnum.setText(MyJoinedRecInfoArrayList.get(position).totalnum);
            holder.recruitnum.setText(MyJoinedRecInfoArrayList.get(position).recruitnum);
        }

        @Override
        public int getItemCount() {
            return MyJoinedRecInfoArrayList.size();
        }
    }

    public void drawList4() {
        MyJoinedRecInfoArrayList.clear();
        try {
            JSONArray items = mResult4.getJSONArray("list");

            for (int i = 0; i < items.length(); i++) {
                JSONObject info = items.getJSONObject(i);
                int joino = info.getInt("rec_no");

                String userid = info.getString("userid");
                String category = info.getString("category");
                String place = info.getString("place");
                String date = info.getString("date");
                String time = info.getString("time");
                String totalnum = info.getInt("total_num")+"";
                String recruitnum = info.getInt("recruit_num")+"";

                String compareDateRecruit = date.replace("-","");

                for (int j=0; j<communityNo.size(); j++)
                {
                    if (Integer.parseInt(currentDate) <= Integer.parseInt(compareDateRecruit) && communityNo.get(j)==joino)
                    {
                        MyJoinedRecInfoArrayList.add(new my_write_recruitInfo(joino ,category,place ,date,time,totalnum,recruitnum));
                    }
                }
            }

        } catch (JSONException | NullPointerException e) {
            mResult4 = null;
        }
        mAdapter4.notifyDataSetChanged();
    }
    private void requestJoinedRecruit() {
        String url = SessionManager.getURL() + "recruit/select_recruit.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mResult4 = response;
                        drawList4();
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
        mQueue4.add(request);
    }

    //-----------------------------------------------------------누가 내글에 참여했는지 recruit
    public void check_drawList2() {
        try {
            JSONArray items = mResult.getJSONArray("list");
            applicantId.clear();
            for (int i = 0; i < items.length(); i++) {
                JSONObject info = items.getJSONObject(i);
                String nick = info.getString("nick");
                String phone = info.getString("phone");
                Log.i("dbdb123",nick+":"+phone);

                applicantId.add(new Nick_Phone(nick,phone));


            }

            if (applicantId.size()>=1)
            {
                textViews = new TextView[applicantId.size()];


                AlertDialog.Builder joinedDialog = new AlertDialog.Builder(Mypage.this);
                joinedDialog.setTitle("등록된 유저의 프라이머리 키");
                LinearLayout DialogLayout = new LinearLayout(Mypage.this);
                DialogLayout.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout NickLayout = new LinearLayout(Mypage.this);
                NickLayout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout PhoneLayout = new LinearLayout(Mypage.this);
                PhoneLayout.setOrientation(LinearLayout.VERTICAL);


                DialogLayout.addView(NickLayout);
                DialogLayout.addView(PhoneLayout);

                for (int i=0; i<applicantId.size();i++)
                {
                    textViews[i] = new TextView(Mypage.this);
                    textViews[i].setText(applicantId.get(i).getNick());
                    NickLayout.addView(textViews[i]);
                }
                for (int i=0; i<applicantId.size();i++)
                {
                    textViews[i] = new TextView(Mypage.this);
                    textViews[i].setText(applicantId.get(i).getPhone());
                    PhoneLayout.addView(textViews[i]);
                }
                joinedDialog.setView(DialogLayout);
                joinedDialog.show();
                applicantId.clear();

            }

        } catch (JSONException | NullPointerException e) {
            mResult = null;
        }
    }

    private void requestCheckRecruit2(final String recno) {

        String url = SessionManager.getURL() + "users/mypage_joined_recruit.php?recno=" + recno;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mResult = response;
                        check_drawList2();
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
                            //Toast.makeText(getContext(), error.getMessage(),
                            //      Toast.LENGTH_LONG).show();
                        }
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }

}

