package com.lemonxq_laplace.pregnantmonitor.activity.student_select_course;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.lemonxq_laplace.pregnantmonitor.R;

import java.util.ArrayList;
import java.util.List;

public class Show_class  extends AppCompatActivity {

    private List<myBean> myBeanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_test);
        ListView listView = (ListView) findViewById(R.id.listview);
        init();
        MyAdapter adapter = new MyAdapter(Show_class.this, R.layout.my_item, myBeanList);
        listView.setAdapter(adapter);


    }

    private void init() {
        myBean bean1 = new myBean("地理信息系统导论", R.mipmap.ic_launcher_round);
        myBeanList.add(bean1);

        myBean bean2 = new myBean("数据结构", R.mipmap.ic_launcher_round);
        myBeanList.add(bean2);

        myBean bean3 = new myBean("算法导论", R.mipmap.ic_launcher_round);
        myBeanList.add(bean3);

        myBean bean4 = new myBean("高数A", R.mipmap.ic_launcher_round);
        myBeanList.add(bean4);

        myBean bean5 = new myBean("物理A", R.mipmap.ic_launcher_round);
        myBeanList.add(bean5);

        myBean bean6 = new myBean("线性代数A", R.mipmap.ic_launcher_round);
        myBeanList.add(bean6);


    }
}

