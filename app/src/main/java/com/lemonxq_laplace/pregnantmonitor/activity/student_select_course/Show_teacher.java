package com.lemonxq_laplace.pregnantmonitor.activity.student_select_course;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.lemonxq_laplace.pregnantmonitor.R;

import java.util.ArrayList;
import java.util.List;

public class Show_teacher extends AppCompatActivity {

    private List<myBean> myBeanList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_layout);
        ListView listView = (ListView) findViewById(R.id.listview_teacher);
        init();
        MyAdapter_teacher adapter = new MyAdapter_teacher(Show_teacher.this,R.layout.my_teacher,myBeanList);
        listView.setAdapter(adapter);

    }
    private void init(){
        myBean bean1 = new myBean("老师A",R.mipmap.ic_launcher_round);
        myBeanList.add(bean1);

        myBean bean2 = new myBean("老师B",R.mipmap.ic_launcher_round);
        myBeanList.add(bean2);

        myBean bean3 = new myBean("老师C",R.mipmap.ic_launcher_round);
        myBeanList.add(bean3);


    }
}
