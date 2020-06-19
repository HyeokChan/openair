package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

public class CustomDialogMypage extends AppCompatActivity {
    SessionManager mSession = SessionManager.getInstance(this);
    RecyclerView mypage_dialog;
    RecyclerView.LayoutManager mLayoutManager;

    private Context context;

    public CustomDialogMypage(Context context) {
        this.context = context;
    }

    public class test {
        public String nic;
        public String phone;

        public test(String nic, String phone){
            this.nic = nic;
            this.phone = phone;
        }
        public String nic() {
            return  nic;
        }
        public String phone() {
            return  phone;
        }
    }
    public void callFunction(){

        final Dialog dlg_mypage = new Dialog(context);
        // 액티비티의 타이틀바를 숨긴다.
        dlg_mypage.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg_mypage.setContentView(R.layout.custom_dialog_mypage);

        // 커스텀 다이얼로그를 노출한다.

        mypage_dialog = (RecyclerView) dlg_mypage.findViewById(R.id.mypage_dialog_rey);
        mypage_dialog.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mypage_dialog.setLayoutManager(mLayoutManager);

        ArrayList<test> testList = new ArrayList<>();
        testList.clear();

        for (int i=0; i<Mypage.applicantId.size(); i++){
            testList.add(new test(Mypage.applicantId.get(i).getNick(),Mypage.applicantId.get(i).getPhone()));
        }

        MyAdapter myAdapter = new MyAdapter(testList);
        mypage_dialog.setAdapter(myAdapter);


        ViewGroup.LayoutParams params = dlg_mypage.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dlg_mypage.getWindow().setAttributes((WindowManager.LayoutParams)params);
        dlg_mypage.show();

    }

    public void callFunctionMatch(){

        final Dialog dlg_mypage = new Dialog(context);
        // 액티비티의 타이틀바를 숨긴다.
        dlg_mypage.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg_mypage.setContentView(R.layout.custom_dialog_mypage);

        // 커스텀 다이얼로그를 노출한다.

        mypage_dialog = (RecyclerView) dlg_mypage.findViewById(R.id.mypage_dialog_rey);
        mypage_dialog.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mypage_dialog.setLayoutManager(mLayoutManager);

        ArrayList<test> testListMatch = new ArrayList<>();
        testListMatch.clear();

        for (int i=0; i<Mypage.applicantIdMatch.size(); i++){
            testListMatch.add(new test(Mypage.applicantIdMatch.get(i).getNick(),Mypage.applicantIdMatch.get(i).getPhone()));
        }

        MyAdapter myAdapter = new MyAdapter(testListMatch);
        mypage_dialog.setAdapter(myAdapter);


        ViewGroup.LayoutParams params = dlg_mypage.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dlg_mypage.getWindow().setAttributes((WindowManager.LayoutParams)params);
        dlg_mypage.show();

    }



}
class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dialogNick;
        TextView dialogPhone;

        MyViewHolder(View view){
            super(view);
            dialogNick = view.findViewById(R.id.dialog_nick);
            dialogPhone = view.findViewById(R.id.dialog_phone);
        }
    }

    private ArrayList<CustomDialogMypage.test> applicantId;
    MyAdapter(ArrayList<CustomDialogMypage.test> applicantId){
        this.applicantId = applicantId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_row_mypage_dialog, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.dialogNick.setText(applicantId.get(position).nic());
        myViewHolder.dialogPhone.setText(applicantId.get(position).phone());
    }

    @Override
    public int getItemCount() {
        return applicantId.size();
    }
}