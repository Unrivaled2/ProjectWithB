package com.lemonxq_laplace.pregnantmonitor.activity.student_select_course;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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

public class MyAdapter_teacher  extends ArrayAdapter {

    private MyDatabaseHelper dbHelper;
    private final int ImageId;
    private String radiotext;
    public MyAdapter_teacher(Context context, int headImage, List<myBean> obj){
        super(context,headImage,obj);
        ImageId = headImage;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MyAdapter_teacher.ViewHolder viewHolder;
        final myBean myBean = (myBean) getItem(position);
        View view ;
        if (convertView == null){
            viewHolder = new MyAdapter_teacher.ViewHolder();
            view = LayoutInflater.from(getContext()).inflate(ImageId,parent,false);
            viewHolder.fruitImage = view.findViewById(R.id.teacher_image);
            viewHolder.fruitName = view.findViewById(R.id.head_text);
            viewHolder.fruitLayout = view.findViewById(R.id.teacher_view);
            viewHolder.query_class = view.findViewById(R.id.query_teacher);
            viewHolder.select = view.findViewById(R.id.select);
            view.setTag(viewHolder);
        }else{
            view =convertView;
            viewHolder = (MyAdapter_teacher.ViewHolder)view.getTag();
        }
        viewHolder.fruitImage.setImageResource(myBean.getImageID());
        viewHolder.fruitName.setText(myBean.getText());

        viewHolder.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取刚才点击的课程名
                dbHelper = new MyDatabaseHelper(getContext(),"temp_select",null,3);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                Cursor cursor = db.query("temp_select",null,null,null,
                        null,null,null);
                String class_Name="";
                if(cursor.moveToFirst())
                {
                    class_Name = cursor.getString(cursor.getColumnIndex("class_name"));
                    cursor.close();
                }
                //弹出对话框对用户进行选择确定还是取消
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        //写入数据库进行存储
                        Toast.makeText(getContext(),"选课成功",Toast.LENGTH_SHORT).show();
                        dia.dismiss();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        dia.dismiss();
                        //进行数据删除
                    }
                });
                myBean myBean2 = (myBean)getItem(position);
                String teacher_name = myBean2.getText();
                dialog.setMessage("class_name: "+class_Name+"\n"+"teacher_name: "
                        +teacher_name+" 确定么?");
                dialog.setTitle("提示框");
                dialog.show();
            }
        });
        viewHolder.query_class.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"查询老师信息界面待书写",Toast.LENGTH_SHORT).show();
            }
        });

        viewHolder.fruitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myBean myBean1 = (myBean)getItem(position);
                Toast.makeText(getContext(),myBean1.text,Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }
    class ViewHolder{
        ImageView fruitImage;
        TextView fruitName;
        LinearLayout fruitLayout;
        Button query_class;
        Button select;
    }
}








