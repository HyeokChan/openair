package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;

public class ChildFragment2 extends Fragment {

    public static final String LOG_TAG = "ChildFragment2(Match)";
    public static final String QUEUE_TAG = "VolleyRequest";

    protected RequestQueue mQueue = null;
    JSONObject mResult = null;
    ArrayList<matchInfo> matchInfoArrayList = new ArrayList<matchInfo>();
    protected MatchAdapter mAdapter = new MatchAdapter(matchInfoArrayList);
    SessionManager mSession = SessionManager.getInstance(getContext());

    CustomDialogCombo customDialogComboMatch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_child_fragment2, container, false);

        FloatingActionButton tvWrite = (FloatingActionButton) v.findViewById(R.id.fab);
        RecyclerView mRecyclerView = (RecyclerView)v.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setHasFixedSize(true);

        tvWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                customDialogComboMatch = new CustomDialogCombo(v.getContext());

                if(!mSession.f3_stCategory.equals("전체") && mSession.isLogin())
                {

                    final Dialog dlg_combo = new Dialog(v.getContext());
                    // 액티비티의 타이틀바를 숨긴다.
                    dlg_combo.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    // 커스텀 다이얼로그의 레이아웃을 설정한다.
                    dlg_combo.setContentView(R.layout.custom_dialog_match);

                    // 커스텀 다이얼로그를 노출한다.

                    TextView title_combo = (TextView) dlg_combo.findViewById(R.id.dialogTitleCombo);

                    final EditText team_name_combo = (EditText) dlg_combo.findViewById(R.id.totalCombo);

                    final EditText recruit_combo = (EditText) dlg_combo.findViewById(R.id.recruitCombo);

                    final Spinner spinner1_combo = (Spinner) dlg_combo.findViewById(R.id.spiner1Combo);

                    final Spinner spinner2_combo = (Spinner) dlg_combo.findViewById(R.id.spiner2Combo);

                    final EditText area_combo = (EditText) dlg_combo.findViewById(R.id.areaCombo);

                    final Button cancle_button_combo = (Button) dlg_combo.findViewById(R.id.cancleButtonCombo);

                    final Button ok_button_combo = (Button) dlg_combo.findViewById(R.id.okButtonCombo);

                    ViewGroup.LayoutParams params = dlg_combo.getWindow().getAttributes();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    dlg_combo.getWindow().setAttributes((WindowManager.LayoutParams)params);

                    ok_button_combo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //main_label.setText(txt_modify_edit.getText().toString());
                            String insert_activity = mSession.f3_stCategory;
                            String insert_time1 = spinner1_combo.getSelectedItem().toString();
                            String insert_time2 = spinner2_combo.getSelectedItem().toString();
                            String insert_time = insert_time1 + " ~ " + insert_time2;
                            String insert_area = area_combo.getText().toString();
                            String insert_total_num = team_name_combo.getText().toString();
                            String insert_recruit_num = recruit_combo.getText().toString();

                            writeMatch(new matchInfo("",mSession.getID(), insert_activity, insert_time, insert_area, insert_total_num, insert_recruit_num));

                            dlg_combo.dismiss(); // 누르면 바로 닫히는 형태
                        }
                    });

                    cancle_button_combo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dlg_combo.dismiss(); // 누르면 바로 닫히는 형태
                        }
                    });

                    dlg_combo.show();


                }
                else if (mSession.f3_stCategory.equals("전체") && mSession.isLogin())
                {
                    Toast.makeText(v.getContext(), "카테고리를 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getContext(),"로그인하시면 쓸 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        CookieHandler.setDefault(new CookieManager());

        mQueue = mSession.getQueue();
        requestMatch();

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_child2);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMatch();

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }


    public class matchInfo {
        public String category;
        public String time;
        public String area;
        public String team_name;
        public String recruit_num;

        public String userid;
        public String matNo;

        public matchInfo(String matNo, String userid, String category, String time, String area, String team_name, String recruit_num){
            this.category = category;
            this.time = time;
            this.area = area;
            this.team_name = team_name;
            this.recruit_num = recruit_num;

            this.userid = userid;
            this.matNo = matNo;

        }

        public String getCategory() {
            return category;
        }

        public String getTime() {
            return time;
        }

        public String getArea() {
            return area;
        }

        public String getTeam_name() {
            return team_name;
        }

        public String getRecruit_num() {
            return recruit_num;
        }
        public String getUserid()
        {
            return userid;
        }
        public String getMatNo()
        {
            return matNo;
        }
    }

    public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder>  {

        ArrayList<matchInfo> matchInfoArrayList = null;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvCategory;
            TextView tvTime;
            TextView tvArea;
            TextView tvRecruit_num;

            TextView mine;
            TextView matNo;

            public ViewHolder(View view){
                super(view);
                view.setOnClickListener(this);
                tvCategory = view.findViewById(R.id.tvcategory);
                tvTime = view.findViewById(R.id.tvtime);
                tvArea = view.findViewById(R.id.tvarea);
                tvRecruit_num = view.findViewById(R.id.tvrecruit_num);

                mine = view.findViewById(R.id.mine);
                matNo = view.findViewById(R.id.rec_no);
            }

            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder dlJoin = new android.support.v7.app.AlertDialog.Builder(v.getContext());
                dlJoin.setTitle("참가여부");

                LinearLayout tvLayout = new LinearLayout(v.getContext());
                tvLayout.setOrientation(LinearLayout.VERTICAL);
                final TextView dlCategory = new TextView(v.getContext());
                dlCategory.setText("    카테고리:"+tvCategory.getText());
                tvLayout.addView(dlCategory);
                final TextView dlTime = new TextView(v.getContext());
                dlTime.setText("    시간:"+tvTime.getText());
                tvLayout.addView(dlTime);
                final TextView dlArea = new TextView(v.getContext());
                dlArea.setText("    장소:"+tvArea.getText());
                tvLayout.addView(dlArea);
                final TextView dlRecruit_num = new TextView(v.getContext());
                dlRecruit_num.setText("    매칭인원 (팀 이름):"+tvRecruit_num.getText());
                tvLayout.addView(dlRecruit_num);
                final TextView dlMessage = new TextView(v.getContext());
                dlMessage.setText("    신청하시겠습니까?");
                tvLayout.addView(dlMessage);

                dlJoin.setView(tvLayout);

                if (mine.getText().equals("ME"))
                {
                    dlJoin.setNegativeButton("삭제하기",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    // 프로그램을 종료한다
                                    deleteMatch(matNo.getText().toString());
                                    dialog.dismiss(); // 누르면 바로 닫히는 형태
                                }
                            });
                }

                else
                {
                    dlJoin.setPositiveButton("예",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    // 프로그램을 종료한다
                                    joinMatch(matNo.getText().toString());
                                    dialog.dismiss(); // 누르면 바로 닫히는 형태
                                }
                            });

                    dlJoin.setNegativeButton("아니요",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id)
                                {
                                    // 프로그램을 종료한다
                                    dialog.dismiss(); // 누르면 바로 닫히는 형태
                                }
                            });
                }
                dlJoin.show();

            }
        }

        public MatchAdapter(ArrayList<matchInfo> matchInfoArrayList){
            this.matchInfoArrayList = matchInfoArrayList;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_recruit, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if(mSession.getID().equals(matchInfoArrayList.get(position).userid))
            {
                holder.mine.setText("ME");
            }
            else {
                holder.mine.setText("");
            }
            holder.tvCategory.setText(matchInfoArrayList.get(position).category);
            holder.tvTime.setText(matchInfoArrayList.get(position).time);
            holder.tvArea.setText(matchInfoArrayList.get(position).area);
            holder.tvRecruit_num.setText( matchInfoArrayList.get(position).recruit_num + " (" + matchInfoArrayList.get(position).team_name + ")");
            holder.matNo.setText(matchInfoArrayList.get(position).matNo);
        }

        @Override
        public int getItemCount() {
            return matchInfoArrayList.size();
        }
    }

    public void drawList() {
        matchInfoArrayList.clear();
        try {
            JSONArray items = mResult.getJSONArray("list");

            for (int i = 0; i < items.length(); i++) {
                JSONObject info = items.getJSONObject(i);
                String date = info.getString("date");
                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
                Date check_time = date_format.parse(date);
                if(info.getString("status").equals("false")
                        && Menu3Fragment.select_year == check_time.getYear()
                        && Menu3Fragment.select_month == check_time.getMonth()
                        && Menu3Fragment.select_date == check_time.getDate()
                        && (info.getString("category").equals(mSession.f3_stCategory) || mSession.f3_stCategory.equals("전체"))) {
                    String matno = info.getInt("mat_no")+"";
                    String userid = info.getString("userid");
                    String category = info.getString("category");
                    String time = info.getString("time");
                    String area = info.getString("place");
                    String team_name = info.getString("team_name");
                    String recruit_num = info.getString("recruit_num");

                    matchInfoArrayList.add(new matchInfo(matno, userid, category, time, area, team_name, recruit_num));
                }
            }

        } catch (JSONException | NullPointerException e) {
            Toast.makeText(getContext(),
                    "모집 글이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            mResult = null;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mAdapter.notifyDataSetChanged();
    }

    private void requestMatch() {
        String url = SessionManager.getURL() + "match/select_match.php";
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


    protected void writeMatch(matchInfo insert_data) {
        String url = SessionManager.getURL() + "match/insert_match.php";

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", mSession.getID());
        params.put("category", insert_data.category);
        params.put("team_name", insert_data.team_name);
        params.put("recruit_num", insert_data.recruit_num);
        params.put("time", insert_data.time);
        params.put("area", insert_data.area);
        params.put("date", (Menu3Fragment.select_year + 1900) + "-" + ((Menu3Fragment.select_month + 1) < 10 ? "0" + (Menu3Fragment.select_month + 1) : (Menu3Fragment.select_month + 1))
                + "-" + ((Menu3Fragment.select_date) < 10 ? "0" + (Menu3Fragment.select_date) : (Menu3Fragment.select_date)));
        JSONObject jsonObj = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "Response: " + response.toString());
                        mResult = response;
                        if (response.has("error")) {
                            try {
                                Toast.makeText(getContext(),
                                        response.getString("error").toString(),
                                        Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            requestMatch();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }


    protected void deleteMatch(String matno) {
        String url = SessionManager.getURL() + "match/delete_match.php";
        Map<String, String> params = new HashMap<String, String>();
        Log.i("wpwp",matno);
        params.put("mat_no", matno);

        JSONObject jsonObj = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, jsonObj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("adad","ok");
                        Log.i(LOG_TAG, "Response: " + response.toString());
                        mResult = response;
                        if (response.has("error")) {
                            try {
                                Toast.makeText(getContext(),
                                        response.getString("error").toString(),
                                        Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            requestMatch();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue.add(request);

    }


    protected void joinMatch(String matno) {
        String url = SessionManager.getURL() + "match/join_match.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("community", "match");
        params.put("mat_no", matno);
        params.put("id", mSession.getID());

        JSONObject jsonObj = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, jsonObj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG, "Response: " + response.toString());
                        mResult = response;
                        if (response.has("error")) {
                            try {
                                Toast.makeText(getContext(),
                                        response.getString("error").toString(),
                                        Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            requestMatch();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue.add(request);

        int checkedItem;
        for (checkedItem = 0; checkedItem < matchInfoArrayList.size(); checkedItem++)
        {
            if(matchInfoArrayList.get(checkedItem).getMatNo().equals(matno))
                applicant_notification(matchInfoArrayList.get(checkedItem).getUserid());
        }
    }



    private void applicant_notification(final String matno) {
        String url = SessionManager.getURL() + "match/match_token.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mResult = response;
                        try {
                            JSONArray items = mResult.getJSONArray("list");

                            String nick = "", token = "";
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject info = items.getJSONObject(i);
                                if(matno.equals(info.getInt("id") + "")) {
                                    token = info.getString("token");
                                }
                                if(mSession.getID().equals(info.getString("id"))){
                                    nick = info.getString("nick");
                                }
                            }
                            mSession.sendPostToFCM("매칭신청", nick + "님이 매칭 신청하였습니다.", token);

                        } catch (JSONException | NullPointerException e) {
                            Toast.makeText(getContext(),
                                    "아이디가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
                            mResult = null;
                        }
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

}