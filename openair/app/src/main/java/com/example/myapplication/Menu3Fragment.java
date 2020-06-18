package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;

public class Menu3Fragment extends Fragment {

    View v;
    Button btCategory;
    private int pageNum;
    static int select_year, select_month, select_date;
    SessionManager mSession = SessionManager.getInstance(getContext());


    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_menu3, container, false);

        //처음 childfragment 지정
        pageNum = 1;
        getFragmentManager().beginTransaction().add(R.id.child_fragment, new ChildFragment1()).commit();

        //하위버튼
        final Button subButton1 = (Button) v.findViewById(R.id.recruitButton);
        final Button subButton2 = (Button) v.findViewById(R.id.matchButton);

        //클릭 이벤트
        subButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNum = 1;
                subButton1.setTextColor(Color.rgb(157, 195, 230));
                subButton2.setTextColor(Color.WHITE);
                getFragmentManager().beginTransaction().replace(R.id.child_fragment, new ChildFragment1()).commit();
            }
        });
        subButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageNum = 2;
                subButton1.setTextColor(Color.WHITE);
                subButton2.setTextColor(Color.rgb(157, 195, 230));
                getFragmentManager().beginTransaction().replace(R.id.child_fragment, new ChildFragment2()).commit();
            }
        });

        /* starts before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        /* ends after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(v, R.id.calendarView)
                .startDate(startDate.getTime())
                .endDate(endDate.getTime())
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Date date, int position) {
                select_year = date.getYear();
                select_month = date.getMonth();
                select_date = date.getDate();

                if(pageNum == 1)
                    getFragmentManager().beginTransaction().replace(R.id.child_fragment, new ChildFragment1()).commit();
                else if(pageNum == 2)
                    getFragmentManager().beginTransaction().replace(R.id.child_fragment, new ChildFragment2()).commit();
            }
        });

        final String [] items = {"전체", "축구", "풋살", "골프", "낚시"};

        btCategory = (Button) v.findViewById(R.id.ComunityCategoryButton);
        btCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                int checkedItem;
                for (checkedItem = 0; checkedItem < items.length; checkedItem++)
                {
                    if(items[checkedItem].equals(mSession.f3_stCategory))
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
                        mSession.f3_stCategory = items[which];
                    }
                });
                dl.setPositiveButton("선택완료",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id)
                            {
                                // 프로그램을 종료한다
                                if(pageNum == 1)
                                    getFragmentManager().beginTransaction().replace(R.id.child_fragment, new ChildFragment1()).commit();
                                else if(pageNum == 2)
                                    getFragmentManager().beginTransaction().replace(R.id.child_fragment, new ChildFragment2()).commit();
                                btCategory.setText(mSession.f3_stCategory);
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

        return v;
    }



}