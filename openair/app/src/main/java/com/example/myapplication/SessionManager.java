package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class SessionManager {
    private static SessionManager sInstance;

    // TODO: 서버 주소 변경할 것
    public static final String SERVER_ADDR = "http://rnjsgur12.cafe24.com/OPENAIR/";
    public static String getURL() { return SERVER_ADDR; }

    public static final String PREF_NAME = "SessionManagerPref";
    public static final String QUEUE_TAG = "VolleyRequest";
    public static final String LOG_TAG = "LOGSessionManager";
    public static final String KEY_ID = "id";
    public static final String KEY_EMAIL = "email";

    static String f2_stCategory="카테고리 선택";
    static String f3_stCategory="전체";
    static Context mContext = null;

    protected RequestQueue mQueue = null;
    public RequestQueue getQueue() { return mQueue; }

    protected boolean mIsLogin = false;
    public boolean isLogin() {
        return mIsLogin;
    }

    protected String mID = null;
    public String getID() {
        return mID;
    }

    protected String mEmail = null;
    public String getEmail() {
        return mEmail;
    }


    protected String mEmailToLogin = null;



    protected Activity mCurrentActivity = null;

    protected ImageLoader mImageLoader = null;
    public ImageLoader getImageLoader() { return mImageLoader; }



    //--------------------------------------------------------------
    public static SessionManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SessionManager(context);
        }
        return sInstance;
    }

    //--------------------------------------------------------------
    public SessionManager(Context context){
        SessionManager.mContext = context;
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        mID = pref.getString(KEY_ID, "");
        mEmail = pref.getString(KEY_EMAIL, "");

        if (mID.length() > 0 && mEmail.length() > 0) {
            mIsLogin = true;
        }

        CookieHandler.setDefault(new CookieManager());

        mQueue = Volley.newRequestQueue(context);

        //mImageLoader = new ImageLoader(mQueue,
        //      new LruBitmapCache(LruBitmapCache.getCacheSize(mContext)));
    }


    protected void sessionLogin() {
        // mID는 parseNetworkResponse()에서 세팅
        mEmail = mEmailToLogin;
        Log.i(LOG_TAG, "mEmail = mEmailToLogin;");
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_EMAIL, mEmailToLogin);
        editor.commit();
        mEmailToLogin = "";

        mIsLogin = true;
        Log.i(LOG_TAG, "mIsLogin = true;");

        ((MainActivity) mContext).setNavEmail(mEmail);
    }

    protected void sessionLogout() {
        mID = "";
        mEmail = "";
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
        mIsLogin = false;
        Log.i(LOG_TAG, "mIsLogin = false;");
        ((MainActivity) mContext).setNavEmail(mEmail);
    }

    //---------------------------------------------------------------------------
    public void Login(String email, String pass, Activity activity)  {
        String url = SERVER_ADDR + "users/login.php";

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("pass", pass);
        params.put("token", FirebaseInstanceId.getInstance().getToken());
        JSONObject jsonObj = new JSONObject(params);

        mEmailToLogin = email;

        mCurrentActivity = activity;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        View focusView = null;
                        Log.i(LOG_TAG,"Response: " + response.toString());
                        try {
                            if (response.has("status")) {
                                if (response.getString("status").equals("Success")) {
                                    sessionLogin();
                                    mCurrentActivity.setResult(RESULT_OK);
                                    mCurrentActivity.finish();
                                    mCurrentActivity = null;
                                }
                            }
                            else {
                                if (response.getString("error").
                                        equals("Not exist e-mail")) {
                                    AutoCompleteTextView tv = (AutoCompleteTextView) mCurrentActivity.findViewById(R.id.email);
                                    tv.setError("Email does not exist.");
                                    focusView = tv;
                                    focusView.requestFocus();
                                }
                                else if (response.getString("error").
                                        equals("Invalid password")) {
                                    EditText tv = (EditText) mCurrentActivity.findViewById(R.id.password);
                                    tv.setError("Invalid password.");
                                    focusView = tv;
                                    focusView.requestFocus();
                                }
                                else {
                                    Log.i(LOG_TAG, "Error: " +
                                            response.getString("error"));
                                    Intent intent = new Intent();
                                    intent.putExtra("message",
                                            response.getString("error"));
                                    mCurrentActivity.setResult(RESULT_OK, intent);
                                    mCurrentActivity.finish();
                                    mCurrentActivity = null;
                                }
                            }
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG,"Error: " + error.getMessage());
                        Intent intent = new Intent();
                        intent.putExtra("message", "Error: " + error.getMessage());
                        mCurrentActivity.setResult(RESULT_OK, intent);
//                        mCurrentActivity.setResult(RESULT_OK);
                        mCurrentActivity.finish();
                        mCurrentActivity = null;
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.i(LOG_TAG,"getHeaders()");
                HashMap<String, String> headers = new HashMap<String, String>();

                if (mID.length() > 0 && mEmail.length() > 0) {
                    String cookie = String.format("id=" + mID + ";email=" + mEmail);
                    headers.put("Cookie", cookie);
                }
                return headers;
            }
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                Log.i(LOG_TAG,"parseNetworkResponse()");

                Log.i("response",response.headers.toString());
                Map<String, String> responseHeaders = response.headers;
                String cookie = responseHeaders.get("Set-Cookie");
                if (cookie != null) {
                    Log.i("Set-Cookie", cookie);
                    int p = cookie.indexOf("id=");
                    Log.i("index=", "" + p);
                    if (p >= 0) {
                        mID = cookie.substring(cookie.indexOf("id=") + 3, cookie.length() - 1);
                        int end = mID.indexOf(";");
                        if (end > 0)
                            mID = mID.substring(0, end);
                        Log.i("mID", mID);
                        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                                Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(KEY_ID, mID);
                        editor.commit();
                    }
                }
                return super.parseNetworkResponse(response);
            }
        };

        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }

    //---------------------------------------------------------------------------
    public void Signup(String email, String pass, String pass2, String nick, String phone, Activity activity)  {
        String url = SERVER_ADDR + "users/signup.php";

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("pass", pass);
        params.put("pass2", pass2);
        params.put("nick", nick);
        params.put("token", FirebaseInstanceId.getInstance().getToken());
        params.put("phone", phone);
        JSONObject jsonObj = new JSONObject(params);

        mEmailToLogin = email;
        mCurrentActivity = activity;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url, jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        View focusView = null;
                        Log.i(LOG_TAG,"Response: " + response.toString());
                        try {
                            if (response.has("status")) {
                                if (response.getString("status").equals("Success")) {
                                    sessionLogin();
                                    mCurrentActivity.setResult(RESULT_OK);
                                    mCurrentActivity.finish();
                                    mCurrentActivity = null;
                                }
                            }
                            else {
                                Log.i(LOG_TAG,"Error: " +
                                        response.getString("error"));
                                if (response.getString("error").
                                        equals("Registered e-n")) {
                                    AutoCompleteTextView tv = (AutoCompleteTextView) mCurrentActivity.findViewById(R.id.email);
                                    tv.setError("Email already registered.");
                                    EditText tv2 = (EditText) mCurrentActivity.findViewById(R.id.nickname);
                                    tv2.setError("Nickname already registered.");
                                    focusView = tv;
                                    focusView.requestFocus();
                                }
                                else if (response.getString("error").
                                        equals("Registered e-mail")) {
                                    AutoCompleteTextView tv = (AutoCompleteTextView) mCurrentActivity.findViewById(R.id.email);
                                    tv.setError("Email already registered.");
                                    focusView = tv;
                                    focusView.requestFocus();
                                }
                                else if (response.getString("error").
                                        equals("Registered nick")) {
                                    EditText tv = (EditText) mCurrentActivity.findViewById(R.id.nickname);
                                    tv.setError("Nickname already registered.");
                                    focusView = tv;
                                    focusView.requestFocus();
                                }
                                else if (response.getString("error").
                                        equals("Querying Error")) {
//                                    Toast.makeText(mContext, "서버 에러입니다.",
//                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent();
                                    intent.putExtra("message",
                                            //"서버 에러입니다.");
                                            response.getString("error"));
                                    mCurrentActivity.setResult(RESULT_OK, intent);
                                    mCurrentActivity.finish();
                                    mCurrentActivity = null;
                                }
                            }
                        } catch (JSONException e) {
                            //e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG,"Error: " + error.getMessage());

                        Intent intent = new Intent();
                        intent.putExtra("message", "Error: " + error.getMessage());
                        mCurrentActivity.setResult(RESULT_OK, intent);
                        mCurrentActivity.finish();

                        mCurrentActivity = null;
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.i(LOG_TAG,"getHeaders()");
                HashMap<String, String> headers = new HashMap<String, String>();

                if (mID.length() > 0 && mEmail.length() > 0) {
                    String cookie = String.format("id=" + mID + ";email=" + mEmail);
                    headers.put("Cookie", cookie);
                }
                return headers;
            }
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                Log.i(LOG_TAG,"parseNetworkResponse()");

                Log.i("response",response.headers.toString());
                Map<String, String> responseHeaders = response.headers;
                String cookie = responseHeaders.get("Set-Cookie");
                if (cookie != null) {
                    Log.i("Set-Cookie", cookie);
                    int p = cookie.indexOf("id=");
                    Log.i("index=", "" + p);
                    if (p >= 0) {
                        mID = cookie.substring(cookie.indexOf("id=") + 3, cookie.length() - 1);
                        int end = mID.indexOf(";");
                        if (end > 0)
                            mID = mID.substring(0, end);
                        Log.i("mID", mID);
                        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
                                Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString(KEY_ID, mID);
                        editor.commit();
                    }
                }
                return super.parseNetworkResponse(response);
            }
        };

        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }

    //---------------------------------------------------------------------------
    public void Logout() {
        String url = SERVER_ADDR + "users/logout.php";

        Map<String, String> params = new HashMap<String, String>();
        JSONObject jsonObj = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, jsonObj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(LOG_TAG,"Response: " + response.toString());
                        sessionLogout();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(LOG_TAG,"Error: " + error.getMessage());
                        sessionLogout();
//                        mID = "";
//                        mEmail = "";
//                        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME,
//                                Activity.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = pref.edit();
//                        editor.clear();
//                        editor.commit();
//                        mIsLogin = false;
                        Log.i(LOG_TAG, "mIsLogin = false;");
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.i(LOG_TAG,"getHeaders()");
                HashMap<String, String> headers = new HashMap<String, String>();

                if (mID.length() > 0 && mEmail.length() > 0) {
                    String cookie = String.format("id=" + mID + ";email=" + mEmail);
                    headers.put("Cookie", cookie);
                }
                return headers;
            }
        };

        request.setTag(QUEUE_TAG);
        mQueue.add(request);
    }

    //---------------------------------------------------------------------------
    public void cancelQueue() {
        if (mQueue != null) {
            mQueue.cancelAll(QUEUE_TAG);
        }
    }


    ////////////////////////////////////////////////////

    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAA9L5lpRA:APA91bFEzsSfc3WykbzYJXgvKtkZ75KY_B39xnwUdKefYlkgkI-JcTClhPFNLysLXpV-y_NfTXex71mQcW5dwYkTJkRfpxyLLuXX5owWMahVbAFDkyCJ5ueu90cgjmXgsbp4V9hVFhYv";
    protected void sendPostToFCM(final String title, final String body, final String Token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // FMC 메시지 생성 start
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);
                    root.put("notification", notification);
                    root.put("to", Token);
                    // FMC 메시지 생성 end
                    URL Url = new URL(FCM_MESSAGE_URL);
                    HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Content-type", "application/json");
                    OutputStream os = conn.getOutputStream();
                    os.write(root.toString().getBytes("utf-8"));
                    os.flush();
                    conn.getResponseCode();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
