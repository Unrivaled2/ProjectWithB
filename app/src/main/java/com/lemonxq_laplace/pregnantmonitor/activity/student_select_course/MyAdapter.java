package com.lemonxq_laplace.pregnantmonitor.activity.student_select_course;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lemonxq_laplace.pregnantmonitor.R;

import java.util.List;

public class MyAdapter  extends ArrayAdapter {

    private MyDatabaseHelper dbHelper;
    public Context mContext;
    private final int ImageId;
    private String radiotext;
    public MyAdapter(Context context, int headImage, List<myBean> obj){
        super(context,headImage,obj);
        ImageId = headImage;
        mContext = context;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MyAdapter.ViewHolder viewHolder;
        final myBean myBean = (myBean) getItem(position);
        final View view ;
        if (convertView == null){
            viewHolder = new MyAdapter.ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(ImageId,parent,false);
            viewHolder.fruitImage = view.findViewById(R.id.headimage);
            viewHolder.fruitName = view.findViewById(R.id.headtext);
            viewHolder.fruitLayout = view.findViewById(R.id.ll_view);
            viewHolder.query_class = view.findViewById(R.id.query_class);
            view.setTag(viewHolder);
        }else{
            view =convertView;
            viewHolder = (MyAdapter.ViewHolder)view.getTag();
        }
        viewHolder.fruitImage.setImageResource(myBean.getImageID());
        viewHolder.fruitName.setText(myBean.getText());


        viewHolder.query_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"查询课程信息界面待书写",Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.fruitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //将选择的课程名写入数据库，然后启动选择老师活动
                dbHelper = new MyDatabaseHelper(getContext(),"temp_select",null,3);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                myBean myBean1 = (myBean)getItem(position);
                values.put("class_name",myBean1.getText());
                db.insert("temp_select",null,values);
                Intent intent = new Intent(getContext(),Show_teacher.class);
                mContext.startActivity(intent);
            }
        });

        return view;
    }
    class ViewHolder{
        ImageView fruitImage;
        TextView fruitName;
        LinearLayout fruitLayout;
        Button query_class;
    }



//
//        @NonNull
//        @Override
//        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//            myBean myBean = (myBean) getItem(position);
//            View view;
//            if (convertView == null){
//                view = LayoutInflater.from(getContext()).inflate(ImageId,parent,false);//这个是实例化一个我们自己写的界面Item
//            }else{
//                view =convertView;
//            }
//            LinearLayout linearLayout = view.findViewById(R.id.ll_view);
//            ImageView headImage = view.findViewById(R.id.headimage);
//            TextView headText = view.findViewById(R.id.headtext);
//            RadioGroup radio = view.findViewById(R.id.radioBtn);
//            headImage.setImageResource(myBean.getImageID());
//            headText.setText(myBean.getText());
//            radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {//检查Radio Button那个被点击了
//                @Override
//                public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
//                    switch (i){
//                        case R.id.radio1:
//                            radiotext = "不打";
//                            break;
//                        case R.id.radio2:
//                            radiotext = "打他";
//                            break;
//                    }
//                }
//            });
//            linearLayout.setOnClickListener(new View.OnClickListener() {//检查哪一项被点击了
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(getContext(),"你点击了第"+position+"项"+"你选择"+radiotext,Toast.LENGTH_SHORT).show();
//                }
//            });
//            return view;
//        }
}









