package com.lemonxq_laplace.pregnantmonitor.activity.student_select_course;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static String CREATE_STU_INFO = "create table stu_info(" +
            "id integer primary key autoincrement," +
            "name text ,"+
            "class_name text," +
            "course_name text," +
            "teacher_name text)";

    public static String CREATE_TEMP_SELECT = "create table temp_select("+
            "class_name text,"+
            "teacher_name text)";
    private Context mContext;
    public MyDatabaseHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mContext = context;
    };
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_STU_INFO);
        db.execSQL(CREATE_TEMP_SELECT);
        Toast.makeText(mContext,"create successfully",Toast.LENGTH_LONG).show();
    };

    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("drop table if exists stu_info");
        onCreate(db);
    };
}
