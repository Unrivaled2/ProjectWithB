package com.lemonxq_laplace.pregnantmonitor.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.lemonxq_laplace.pregnantmonitor.Data.User;
import com.lemonxq_laplace.pregnantmonitor.R;
import com.lemonxq_laplace.pregnantmonitor.Util.CommonRequest;
import com.lemonxq_laplace.pregnantmonitor.Util.Consts;
import com.lemonxq_laplace.pregnantmonitor.Util.HttpUtil;
import com.lemonxq_laplace.pregnantmonitor.Util.UserManager;
import com.lemonxq_laplace.pregnantmonitor.Util.Util;
import com.lemonxq_laplace.pregnantmonitor.activity.AnalyzeActivity;
import com.lemonxq_laplace.pregnantmonitor.activity.CenterActivity;
import com.lemonxq_laplace.pregnantmonitor.activity.FStepActivity;
import com.lemonxq_laplace.pregnantmonitor.activity.getFrame.GetFrame;
import com.lemonxq_laplace.pregnantmonitor.view.WheelView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author: Lemon-XQ
 * @date: 2018/1/24
 * @description: 工具箱界面
 */
public class ToolFragment extends Fragment {

    View view;
    private TextView username;
    private TextView Place;
    private  TextView Classroom;
    private Button place;
    private Button class_room;
    private Button begin_signup;
    private Button sign_up;
    private ArrayList<String> class_room_list= new ArrayList<String>();
    private ArrayList<String> place_list = new ArrayList<String>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tool, container, false);
        InitComponent();
        initData();
        setListeners();

        return view;

    }

    private  void initData()
    {
        class_room_list.add("100");
        class_room_list.add("101");
        class_room_list.add("102");
        class_room_list.add("103");
        class_room_list.add("104");
        class_room_list.add("106");
        class_room_list.add("107");
        class_room_list.add("108");
        place_list.add("北综");
        place_list.add("教三");
        place_list.add("东教A");
        place_list.add("东教B");
        place_list.add("教二");
    }

    private void setListeners()
    {
        place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View heightView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_wheelview, null);
                final WheelView wv = heightView.findViewById(R.id.wheel_view);
                wv.setItems(place_list);
                wv.setSeletion(3);

                // 弹身高修改框
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(heightView);
                builder.setTitle("上课地点");
                builder.setNegativeButton(getResources().getString(R.string.cancel),null);
                builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 保存身高
                        String heightStr = wv.getSeletedItem();
                        float height = Float.parseFloat(heightStr)/100.0f;
                        User user = UserManager.getCurrentUser();
                        user.setHeight(height);
                        user.save();
                        // 更改显示
                        Place.setText(heightStr);
                    }
                });
                builder.show();
            }
        });

        class_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View heightView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_wheelview, null);
                final WheelView wv = heightView.findViewById(R.id.wheel_view);
                wv.setItems(class_room_list);
                wv.setSeletion(3);
                // 弹身高修改框
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setView(heightView);
                builder.setTitle("教室选择");
                builder.setNegativeButton(getResources().getString(R.string.cancel),null);
                builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // 保存身高
                        String heightStr = wv.getSeletedItem();
                        float height = Float.parseFloat(heightStr)/100.0f;
                        User user = UserManager.getCurrentUser();
                        user.setHeight(height);
                        user.save();
                        // 更改显示
                        Classroom.setText(heightStr);
                    }
                });
                builder.show();
            }
        });
        begin_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),GetFrame.class);
                startActivity(intent);
            }
        });

    }

    private void InitComponent() {
        username = view.findViewById(R.id.center_account_t);
        class_room = view.findViewById(R.id.center_height_btn_t);
        place = view.findViewById(R.id.place_choose);
        begin_signup = view.findViewById(R.id.center_exit_btn_t);
        sign_up = view.findViewById(R.id.center_exit_btn_s);
        Place = view.findViewById(R.id.Place);
        Classroom = view.findViewById(R.id.classroom);
    }
}