package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Menu2Fragment extends Fragment {

    public static final String LOG_TAG = "Menu2Fragment";
    public static final String QUEUE_TAG = "VolleyRequest";

    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;
    JSONObject mResult = null;
    ArrayList<placeInfo> placeInfoArrayList = new ArrayList<placeInfo>();
    protected PlaceAdapter mAdapter = new PlaceAdapter(placeInfoArrayList);
    SessionManager mSession = SessionManager.getInstance(getContext());

    Button btCategory;
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static String c_name;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_menu2, container, false);

        final String [] items = {"전체", "축구", "풋살", "농구", "등산", "낚시","골프"};

        btCategory = (Button) v.findViewById(R.id.ActivityCategoryButton);
        btCategory.setText(SessionManager.f2_stCategory);
        btCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                int checkedItem;
                for (checkedItem = 0; checkedItem < items.length; checkedItem++)
                {
                    if(items[checkedItem].equals(SessionManager.f2_stCategory))
                        break;
                }

                //Toast.makeText(v.getContext(), "ddd", Toast.LENGTH_SHORT).show();
                // TODO Auto-generated method stub
                AlertDialog.Builder dl = new AlertDialog.Builder(v.getContext());
                dl.setTitle("카테고리 선택");
                dl.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        //Toast.makeText(v.getContext(), items[which], Toast.LENGTH_SHORT).show();
                        SessionManager.f2_stCategory = items[which];
                    }
                });
                dl.setPositiveButton("선택완료",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // 프로그램을 종료한다

                                getFragmentManager().beginTransaction().replace(R.id.frame_layout, new Menu2Fragment()).commit();

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

        final RecyclerView mRecyclerView = (RecyclerView)v.findViewById(R.id.ActivityRecycler);
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
        requestPlace();

        //-------------------------------

        final EditText mEditText = (EditText)v.findViewById(R.id.editText);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_menu2);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestPlace();
                filter(mEditText.getText().toString());

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }


    private void filter(String text) {
        ArrayList<placeInfo> filteredList = new ArrayList<>();

        for (placeInfo item : placeInfoArrayList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        mAdapter.filterList(filteredList);
    }

    public class placeInfo {
        public String category; // 야외활동지 분류
        public String name; // 야외활동지 명
        public String image; // 야외활동지 이미지 파일 명
        public String sinfo; // 야외활동지 간략 설명
        public String linfo; // 야외활동지 긴 설명
        public String star; // 야외활동지 별점
        public String price; // 야외활동지 가격
        public String number; // 야외활동지 전화번호
        public String location; // 야외활동지 지역
        public String address; // 야외활동지 주소

        public placeInfo(String category, String name, String image, String sinfo, String linfo, String star, String price, String number, String location, String address) {
            this.category = category;
            this.name = name;
            this.image = image;
            this.sinfo = sinfo;
            this.linfo = linfo;
            this.star = star;
            this.price = price;
            this.number = number;
            this.location = location;
            this.address = address;
        }

        public String getCategory() {
            return category;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public String getSinfo() {
            return sinfo;
        }

        public String getLinfo() {
            return linfo;
        }

        public String getStar() {
            return star;
        }

        public String getPrice() {
            return price;
        }

        public String getNumber() {
            return number;
        }

        public String getLocation() {
            return location;
        }

        public String getAddress() {
            return address;
        }
    }


    public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

        ArrayList<placeInfo> placeInfoArrayList = null;

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            NetworkImageView ivImage;
            TextView tvCategoty;
            TextView tvName;
            TextView tvText;
            TextView tvStar;
            TextView tvPrice;

            public ViewHolder(View view){
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
                int index_num;
                for(index_num = 0; index_num < placeInfoArrayList.size(); index_num++)
                {
                    if(placeInfoArrayList.get(index_num).name.equals(c_name))
                        break;
                }
                //Toast.makeText(v.getContext(), "ok", Toast.LENGTH_SHORT).show();
                Intent ReserveIntent = new Intent(getActivity(),Reserve.class);
                ReserveIntent.putExtra("image", placeInfoArrayList.get(index_num).image);
                ReserveIntent.putExtra("linfo", placeInfoArrayList.get(index_num).linfo);
                ReserveIntent.putExtra("number", placeInfoArrayList.get(index_num).number);
                ReserveIntent.putExtra("location", placeInfoArrayList.get(index_num).location);
                ReserveIntent.putExtra("address", placeInfoArrayList.get(index_num).address);

                getActivity().startActivity(ReserveIntent);
            }
        }

        public PlaceAdapter(ArrayList<placeInfo> placeInfoArrayList){
            this.placeInfoArrayList = placeInfoArrayList;
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_activity, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.ivImage.setImageUrl(SessionManager.getURL() + "image/" + placeInfoArrayList.get(position).image, mImageLoader);
            holder.tvCategoty.setText(placeInfoArrayList.get(position).category);
            holder.tvName.setText(placeInfoArrayList.get(position).name);
            holder.tvText.setText(placeInfoArrayList.get(position).sinfo);
            holder.tvStar.setText(placeInfoArrayList.get(position).star);
            holder.tvPrice.setText(placeInfoArrayList.get(position).price);
        }

        @Override
        public int getItemCount() {
            return placeInfoArrayList.size();
        }

        public void filterList(ArrayList<placeInfo> filteredList) {
            placeInfoArrayList = filteredList;
            notifyDataSetChanged();
        }
    }

    public void drawList() {
        placeInfoArrayList.clear();
        try {
            JSONArray items = mResult.getJSONArray("list");

            for (int i = 0; i < items.length(); i++) {
                JSONObject info = items.getJSONObject(i);
                if(info.getString("category").equals(SessionManager.f2_stCategory) || SessionManager.f2_stCategory.equals("전체")) {
                    String category = info.getString("category"); // 야외활동지 분류
                    String name = info.getString("name"); // 야외활동지 명
                    String image = info.getString("image"); // 야외활동지 이미지 파일 명
                    String sinfo = info.getString("sinfo"); // 야외활동지 설명
                    String linfo = info.getString("linfo"); // 야외활동지 설명
                    String star = info.getString("star"); // 야외활동지 별점
                    String price = info.getString("price"); // 야외활동지 가격
                    String number = info.getString("number"); // 야외활동지 전화번호
                    String location = info.getString("location"); // 야외활동지 지역
                    String address = info.getString("address"); // 야외활동지 주소

                    placeInfoArrayList.add(new placeInfo(category, name, image, sinfo, linfo, star, price, number, location, address));
                }
            }

        } catch (JSONException | NullPointerException e) {
            Toast.makeText(getContext(),
                    "야외활동지가 존재하지 않습니다.", Toast.LENGTH_LONG).show();
            mResult = null;
        }

        mAdapter.notifyDataSetChanged();
    }

    private void requestPlace() {
        String url = SessionManager.getURL() + "place/select_place.php";
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