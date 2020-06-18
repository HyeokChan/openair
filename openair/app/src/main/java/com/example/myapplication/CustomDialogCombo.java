package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
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

public class CustomDialogCombo extends AppCompatActivity {

    public static final String LOG_TAG = "CustomDialogCombo(insert)";
    public static final String QUEUE_TAG = "VolleyRequest";
    SessionManager mSession = SessionManager.getInstance(this);
    JSONObject mResult = null;
    protected RequestQueue mQueue = null;

    TextView title_combo;
    EditText total_combo;
    EditText recruit_combo;
    Spinner spinner1_combo;
    Spinner spinner2_combo;
    EditText area_combo;
    Button cancle_button_combo;
    Button ok_button_combo;


    //public static boolean finishBool = false;
    //public static String insert_email;
    //public static String insert_rating;
    //public static String insert_content;

    private Context context;

    public CustomDialogCombo(Context context) {
        this.context = context;
    }

    public void callFunction(){
        final Dialog dlg_combo = new Dialog(context);
        // 액티비티의 타이틀바를 숨긴다.
        dlg_combo.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg_combo.setContentView(R.layout.custom_dialog_combo);

        WindowManager.LayoutParams wm = new WindowManager.LayoutParams();
        wm.copyFrom(dlg_combo.getWindow().getAttributes());
        wm.width=400;


        // 커스텀 다이얼로그를 노출한다.
        dlg_combo.show();
        title_combo = (TextView) dlg_combo.findViewById(R.id.dialogTitleCombo);

        total_combo = (EditText) dlg_combo.findViewById(R.id.totalCombo);

        recruit_combo = (EditText) dlg_combo.findViewById(R.id.recruitCombo);

        spinner1_combo = (Spinner) dlg_combo.findViewById(R.id.spiner1Combo);

        spinner2_combo = (Spinner) dlg_combo.findViewById(R.id.spiner2Combo);

        area_combo = (EditText) dlg_combo.findViewById(R.id.areaCombo);

        cancle_button_combo = (Button) dlg_combo.findViewById(R.id.cancleButtonCombo);

        ok_button_combo = (Button) dlg_combo.findViewById(R.id.okButtonCombo);

        ok_button_combo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //main_label.setText(txt_modify_edit.getText().toString());

                dlg_combo.dismiss();
            }
        });

        cancle_button_combo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //main_label.setText(txt_modify_edit.getText().toString());

                dlg_combo.dismiss();
            }
        });
    }


}
