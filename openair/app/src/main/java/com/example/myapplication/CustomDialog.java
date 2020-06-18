package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//import butterknife.Bind;

public class CustomDialog extends AppCompatActivity {

    public static final String LOG_TAG = "CustomDialog(insert)";
    public static final String QUEUE_TAG = "VolleyRequest";
    SessionManager mSession = SessionManager.getInstance(this);
    JSONObject mResult = null;
    protected RequestQueue mQueue = null;

    RatingBar rb_ratingBar;
    EditText txt_modify_edit;
    Button btn_modify_done;

    //public static boolean finishBool = false;
    //public static String insert_email;
    //public static String insert_rating;
    //public static String insert_content;

    private Context context;

    public CustomDialog(Context context) {
        this.context = context;
    }

    public void callFunction(){
        final Dialog dlg = new Dialog(context);
        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.custom_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();
        TextView txt_dialog_title = (TextView) dlg.findViewById(R.id.txt_dialog_title);

        rb_ratingBar = (RatingBar) dlg.findViewById(R.id.ratingBarDL);

        txt_modify_edit = (EditText) dlg.findViewById(R.id.txt_modify_edit);

        btn_modify_done = (Button) dlg.findViewById(R.id.btn_modify_done);

        btn_modify_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //main_label.setText(txt_modify_edit.getText().toString());


                dlg.dismiss();
            }
        });
    }


}
