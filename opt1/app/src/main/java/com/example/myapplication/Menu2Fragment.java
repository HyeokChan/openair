package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

public class Menu2Fragment extends Fragment {

    View v;
    Button ActivityBtCategoty;
    static String ActivityStCategory="";
    static boolean ActivitySelectBool = false;

    private RequestQueue mRequestQueue = null;
    private ImageLoader mImageLoader = null;

    RecyclerView mRecyclerViewActivity;
    RecyclerView.LayoutManager mLayoutManagerActivity;

    static String c_name;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_menu2, container, false);

        final String [] items = {"축구", "풋살", "농구", "등산", "낚시"};

        ActivityBtCategoty = (Button) v.findViewById(R.id.ActivityCategoryButton);
        ActivityBtCategoty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                //Toast.makeText(v.getContext(), "ddd", Toast.LENGTH_SHORT).show();
                // TODO Auto-generated method stub
                AlertDialog.Builder dl = new AlertDialog.Builder(v.getContext());
                dl.setTitle("카테고리 선택");
                dl.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(v.getContext(), items[which], Toast.LENGTH_SHORT).show();
                        ActivityStCategory = items[which];
                    }
                });
                dl.setPositiveButton("선택완료",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // 프로그램을 종료한다
                                ActivitySelectBool = true;
                                ActivityBtCategoty.setText(ActivityStCategory);
                                dialog.dismiss(); // 누르면 바로 닫히는 형태
                            }
                        });

                dl.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // 프로그램을 종료한다
                                dialog.dismiss(); // 누르면 바로 닫히는 형태
                            }
                        });


                dl.show();
            }

        });

        mRecyclerViewActivity = (RecyclerView)v.findViewById(R.id.ActivityRecycler);
        mRecyclerViewActivity.setHasFixedSize(true);
        mLayoutManagerActivity = new LinearLayoutManager(this.getContext());
        mRecyclerViewActivity.setLayoutManager(mLayoutManagerActivity);
        //mRecyclerViewActivity.setNestedScrollingEnabled(false);

        mRequestQueue = Volley.newRequestQueue(this.getContext());
        mImageLoader = new ImageLoader(mRequestQueue,
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

        ArrayList<ActivityInfo> activityInfoArrayList = new ArrayList<ActivityInfo>();
        activityInfoArrayList.add(new ActivityInfo("ball2.png", "풋살", "풋살장a", "넓고 쾌적한 풋살장", "4.5", "30,000원"));
        activityInfoArrayList.add(new ActivityInfo("suf.png", "서핑", "서핑a", "대천 해수욕장", "5.0", "20,000원"));
        activityInfoArrayList.add(new ActivityInfo("ball1.png", "풋살", "풋살장b", "넓고 쾌적한 풋살장", "4.3", "10,000원"));
        activityInfoArrayList.add(new ActivityInfo("ball3.png", "풋살", "풋살장c", "넓고 쾌적한 풋살장", "4.1", "20,000원"));
        MyAdapterActivity myAdapterAc = new MyAdapterActivity(activityInfoArrayList);
        mRecyclerViewActivity.setAdapter(myAdapterAc);

        return v;
    }

    public class ActivityInfo {
        public String activityImage;
        public String activityCategory;
        public String activityName;
        public String activityText;
        public String activityStar;
        public String activityPrice;

        public ActivityInfo(String activityImage, String activityCategory, String activityName, String activityText, String activityStar, String activityPrice){
            this.activityImage = activityImage;
            this.activityCategory = activityCategory;
            this.activityName = activityName;
            this.activityText = activityText;
            this.activityStar = activityStar;
            this.activityPrice = activityPrice;
        }
    }

    // MyAdapterActivity
    public class MyAdapterActivity extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

        //static int selectImage;
        //static String selectCategory="";
        //static String selectName="";
        //static String selectText="";
        //static String selectStar="";
        //static String selectPrice="";

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            NetworkImageView ivImage;
            TextView tvCategoty;
            TextView tvName;
            TextView tvText;
            TextView tvStar;
            TextView tvPrice;

            MyViewHolder(View view){
                super(view);
                view.setOnClickListener(this);
                ivImage = (NetworkImageView)view.findViewById(R.id.AcitvityImage);
                tvCategoty = view.findViewById(R.id.ActivityCategory);
                tvName = view.findViewById(R.id.ActivityName);
                tvText = view.findViewById(R.id.ActivityText);
                tvStar = view.findViewById(R.id.ActivityStar);
                tvPrice = view.findViewById(R.id.ActivityPrice);
            }

            @Override
            public void onClick(View v) {
                c_name = tvName.getText().toString();
                //Toast.makeText(v.getContext(), "ok", Toast.LENGTH_SHORT).show();
                Intent ReserveIntent = new Intent(getActivity(),Reserve.class);
                getActivity().startActivity(ReserveIntent);
            }

        }

        private ArrayList<ActivityInfo> activityInfoArrayList;

        MyAdapterActivity(ArrayList<ActivityInfo> activityInfoArrayList){
            this.activityInfoArrayList = activityInfoArrayList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_activity, parent, false);
            return new MyAdapterActivity.MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            MyAdapterActivity.MyViewHolder myViewHolder = (MyAdapterActivity.MyViewHolder) holder;

            myViewHolder.ivImage.setImageUrl("http://rnjsgur12.cafe24.com/image/" + activityInfoArrayList.get(position).activityImage, mImageLoader);
            myViewHolder.tvCategoty.setText(activityInfoArrayList.get(position).activityCategory);
            myViewHolder.tvName.setText(activityInfoArrayList.get(position).activityName);
            myViewHolder.tvText.setText(activityInfoArrayList.get(position).activityText);
            myViewHolder.tvStar.setText(activityInfoArrayList.get(position).activityStar);
            myViewHolder.tvPrice.setText(activityInfoArrayList.get(position).activityPrice);
        }

        @Override
        public int getItemCount() {
            return activityInfoArrayList.size();
        }
    }
}