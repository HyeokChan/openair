package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import java.util.HashMap;
import java.util.Map;

public class ReviewActivity extends AppCompatActivity {

    public static final String LOG_TAG = "ReviewActivity(Write)";
    public static final String QUEUE_TAG = "VolleyRequest";

    protected RequestQueue mQueue = null;
    JSONObject mResult = null;
    ArrayList<ReviewInfo> reviewInfoArrayList = new ArrayList<ReviewInfo>();
    protected MyAdapterReview mAdapter = new MyAdapterReview(reviewInfoArrayList);
    SessionManager mSession = SessionManager.getInstance(this);

    RecyclerView mRecyclerViewReview;
    RecyclerView.LayoutManager mLayoutManagerReview;
    FloatingActionButton fabReivew;

    CustomDialog customDialog;

    public TextView tvratingsum;
    protected float ratingsum;
    protected int reviewcnt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        tvratingsum = (TextView) findViewById(R.id.ratingsum);

        fabReivew = findViewById(R.id.fabReview);
        fabReivew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                customDialog = new CustomDialog(ReviewActivity.this);

                // 커스텀 다이얼로그를 호출한다.
                // 커스텀 다이얼로그의 결과를 출력할 TextView를 매개변수로 같이 넘겨준다.
                if(mSession.isLogin())
                {
                    //customDialog.callFunction();

                    final Dialog dlg = new Dialog(ReviewActivity.this);
                    // 액티비티의 타이틀바를 숨긴다.
                    dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    // 커스텀 다이얼로그의 레이아웃을 설정한다.
                    dlg.setContentView(R.layout.custom_dialog);

                    // 커스텀 다이얼로그를 노출한다.

                    TextView txt_dialog_title = (TextView) dlg.findViewById(R.id.txt_dialog_title);

                    final RatingBar rb_ratingBar = (RatingBar) dlg.findViewById(R.id.ratingBarDL);

                    final EditText txt_modify_edit = (EditText) dlg.findViewById(R.id.txt_modify_edit);

                    final Button btn_modify_done = (Button) dlg.findViewById(R.id.btn_modify_done);

                    btn_modify_done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //main_label.setText(txt_modify_edit.getText().toString());
                            String insert_c_name = Menu2Fragment.c_name;
                            String insert_user_email = mSession.getEmail();
                            String insert_content = txt_modify_edit.getText().toString();
                            String insert_rating = rb_ratingBar.getRating()+"";
                            writeNews(new ReviewInfo(insert_user_email, insert_content, insert_rating), ratingsum, reviewcnt);
                            dlg.dismiss();
                        }
                    });

                    dlg.show();

                }
                else {
                    Toast.makeText(ReviewActivity.this,"로그인하시면 쓸 수 있습니다.", Toast.LENGTH_SHORT).show();
                }

            }

        });

        mRecyclerViewReview = (RecyclerView)findViewById(R.id.ReviewRecycler);
        mRecyclerViewReview.setAdapter(mAdapter);
        mRecyclerViewReview.setHasFixedSize(true);
        mLayoutManagerReview = new LinearLayoutManager(this);
        mRecyclerViewReview.setLayoutManager(mLayoutManagerReview);



        CookieHandler.setDefault(new CookieManager());

        mQueue = mSession.getQueue();
        requestReview();

        Log.i("ratingsum",ratingsum+"");
    }




    public class MyAdapterReview extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView tvNic;
            RatingBar rbRating;
            TextView tvSen;

            MyViewHolder(View view){
                super(view);
                view.setOnClickListener(this);
                tvNic = view.findViewById(R.id.ReviewNic);
                rbRating = view.findViewById(R.id.ReviewRatingBar);
                tvSen = view.findViewById(R.id.ReviewSen);
            }

            @Override
            public void onClick(View v) {

            }
        }

        private ArrayList<ReviewInfo> reviewInfoArrayList;

        MyAdapterReview(ArrayList<ReviewInfo> reviewInfoArrayList){
            this.reviewInfoArrayList = reviewInfoArrayList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_review, parent, false);
            return new MyAdapterReview.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            MyAdapterReview.MyViewHolder myViewHolder = (MyAdapterReview.MyViewHolder) holder;
            myViewHolder.tvNic.setText(reviewInfoArrayList.get(position).nicname);
            myViewHolder.tvSen.setText(reviewInfoArrayList.get(position).sentence);
            myViewHolder.rbRating.setRating(reviewInfoArrayList.get(position).ratingFloat);

        }

        @Override
        public int getItemCount() {
            return reviewInfoArrayList.size();
        }
    }

    public class ReviewInfo {
        public String nicname;
        public String rating;
        public String sentence;

        public Float ratingFloat = 0f;

        public ReviewInfo(String nicname, String sentence, String rating){
            this.nicname = nicname;
            this.sentence = sentence;
            this.rating = rating;
            this.ratingFloat = Float.parseFloat(this.rating);
        }
        public String getNicname() {
            return nicname;
        }

        public String getSentence() {
            return sentence;
        }

        public String getRating() {
            return rating;
        }
        public  float getRatingFloat(){
            return ratingFloat;
        }

    }

    public void drawList() {
        reviewInfoArrayList.clear();
        try {
            JSONArray items = mResult.getJSONArray("list");

            reviewcnt = 0;
            ratingsum = 0f;
            for (int i = 0; i < items.length(); i++) {
                JSONObject info = items.getJSONObject(i);
                String c_name = info.getString("c_name");
                if (Menu2Fragment.c_name.equals(c_name)) {
                    reviewcnt++;
                    String user_email = info.getString("user_email");
                    String content = info.getString("content");
                    String rating = info.getString("rating");

                    ratingsum = ratingsum + Float.parseFloat(rating);
                    reviewInfoArrayList.add(new ReviewInfo(user_email, content, rating));
                }

            }
            String stratingnum = String.format("%.2f",ratingsum/reviewcnt);
            tvratingsum.setText("평점 " + stratingnum + "/5");


        } catch (JSONException | NullPointerException e) {
            Toast.makeText(ReviewActivity.this,
                    "모집 글이 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            mResult = null;
        }

        mAdapter.notifyDataSetChanged();

    }

    private void requestReview() {

        String url = SessionManager.getURL() + "review/select_review.php";
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
                            Toast.makeText(ReviewActivity.this, "서버 에러",
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

    protected void writeNews(ReviewInfo insert_data, float starsum, int cnt) {
        String url = SessionManager.getURL() + "review/insert_review.php";

        Map<String, String> params = new HashMap<String, String>();
        params.put("c_name", Menu2Fragment.c_name);
        params.put("user_email", insert_data.nicname);
        params.put("content", insert_data.sentence);
        params.put("rating", insert_data.rating);
        params.put("star", String.format("%.2f", ((starsum + Float.parseFloat(insert_data.rating)) / (cnt + 1))));
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
                                Toast.makeText(ReviewActivity.this,
                                        response.getString("error").toString(),
                                        Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            requestReview();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, error.getMessage());
                        Toast.makeText(ReviewActivity.this, error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }

}