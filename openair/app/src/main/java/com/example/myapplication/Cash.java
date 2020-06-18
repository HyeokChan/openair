package com.example.myapplication;

import android.content.Intent;
import android.service.quicksettings.TileService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Cash extends AppCompatActivity {

    public static final String LOG_TAG = "ChildFragment1(Recruit)";
    public static final String QUEUE_TAG = "VolleyRequest";
    SessionManager mSession = SessionManager.getInstance(Cash.this);
    JSONObject mResult = null;
    protected RequestQueue mQueue = null;


    static final ArrayList<String> array_time = new ArrayList<>();

    Button FinishButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash);


        for (int i = 0; i < TimeSelect.arr_time.size(); i++) {
            if (!array_time.contains(TimeSelect.arr_time.get(i))) {
                array_time.add(TimeSelect.arr_time.get(i));
            }
        }
        TimeSelect.arr_time.clear();

        FinishButton = (Button) findViewById(R.id.FinishButton);
        FinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i=0; i<array_time.size(); i++)
                {
                    insertReserve(mSession.getID(), Menu2Fragment.c_name, DateSelect.c_date,array_time.get(i));
                    Log.i("ttes",array_time.get(i));
                }
                array_time.clear();

                //insertReserve(mSession.getID(), Menu2Fragment.c_name, DateSelect.c_date,)

                Intent HomeIntent = new Intent(Cash.this, MainActivity.class);
                HomeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Cash.this.startActivity(HomeIntent);

            }
        });

        CookieHandler.setDefault(new CookieManager());

        mQueue = mSession.getQueue();
    }


    protected void insertReserve(String id, String c_name, String c_date, String c_time) {
        String url = SessionManager.getURL() + "reserve/insert_reserve.php";
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", id);
        params.put("place", c_name);
        params.put("date", c_date);
        params.put("time", c_time);
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
                                Toast.makeText(Cash.this,
                                        response.getString("error").toString(),
                                        Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //else
                        //  requestRecruit();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG, error.getMessage());
                        Toast.makeText(Cash.this, error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }

}
