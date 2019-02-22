package com.lemonxq_laplace.pregnantmonitor.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lemonxq_laplace.pregnantmonitor.R;
import com.lemonxq_laplace.pregnantmonitor.Util.ActivityController;
import com.lemonxq_laplace.pregnantmonitor.Util.UserManager;
import com.lemonxq_laplace.pregnantmonitor.Util.Util;
import com.lemonxq_laplace.pregnantmonitor.activity.MainActivity;
import com.lemonxq_laplace.pregnantmonitor.activity.MonitorHistoryActivity;
import com.lemonxq_laplace.pregnantmonitor.activity.Show_location;
import com.lemonxq_laplace.pregnantmonitor.activity.Show_speech;

import java.util.Date;
import java.util.List;

import am.widget.circleprogressbar.CircleProgressBar;

/**
 * @author: Lemon-XQ
 * @date: 2018/1/24
 * @description: 实时监测界面
 */
public class MonitorFragment extends Fragment {

    private CircleProgressBar dayProgressBar;// 环形进度条，显示经纬度
    public TextView longtitude;
    public TextView altitude;
    private View mView;
    private TextView input;
    private EditText start_edit;
    private LocationManager locationManager;
    private String locationProvider;
    private Button monitor_button;
    private RadioGroup radioGroup;
    public EditText des;
    private Handler handler = new Handler();
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_monitor, null);
       // requestPermission();
        initView();
        getLocation(this.getActivity());
        return mView;
    }

    void initView() {
        dayProgressBar = mView.findViewById(R.id.circleProgressBar);
        longtitude = mView.findViewById(R.id.longtitude);
        altitude = mView.findViewById(R.id.latitude);
        input = mView.findViewById(R.id.input);
        start_edit = mView.findViewById(R.id.start_place);
        monitor_button = mView.findViewById(R.id.start_monitor);
        radioGroup = mView.findViewById(R.id.radioBtn);
        des = mView.findViewById(R.id.end_place);
        monitor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i)
                {
                    case R.id.radio1:
                        Intent intent = new Intent(getActivity(),Show_speech.class);
                        startActivity(intent);
                    case R.id.radio2:
                        Intent intent1 = new Intent(getActivity(),Show_location.class);
                        startActivity(intent1);
                }
            }
        });
    }

    public void setlontitude(String lontitude) {
        longtitude.setText(lontitude);
    }

    public void setAltitude(String alititude) {
        altitude.setText(alititude);
    }

    public void getLocation(Context context) {
        //1.获取位置管理器
        /*locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //2.获取位置提供器，GPS或是NetWork
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.NETWORK_PROVIDER)){
            //如果是网络定位
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }else if (providers.contains(LocationManager.GPS_PROVIDER)){
            //如果是GPS定位
            locationProvider = LocationManager.GPS_PROVIDER;
        }else {

            return;
        }*/

        //3.获取上次的位置，一般第一次运行，此值为null

        if (context.checkCallingPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, 31);

        } else {
            Location location = locationManager.getLastKnownLocation(locationProvider);
            double lonti = location.getLongitude();
            String strlonti = Double.toString(lonti);
            longtitude.setText(strlonti);
            String strlati = Double.toString(location.getLatitude());

            altitude.setText(strlati);
            LocationListener mListener = new LocationListener() {
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }

                // 如果位置发生变化，重新显示
                @Override
                public void onLocationChanged(Location location) {
                    double lonti = location.getLongitude();
                    String strlonti = Double.toString(lonti);
                    longtitude.setText(strlonti);
                    String strlati = Double.toString(location.getLatitude());
                    altitude.setText(strlati);

                }
            };
            locationManager.requestLocationUpdates(locationProvider, 2, 0, mListener);
        }
    }



/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode == 31){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getContext(),"权限获取成功",Toast.LENGTH_SHORT).show();
                locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                //2.获取位置提供器，GPS或是NetWork
                List<String> providers = locationManager.getProviders(true);
                Toast.makeText(getContext(),"1111",Toast.LENGTH_SHORT).show();
                if ( providers.contains(LocationManager.GPS_PROVIDER)){
                    //如果是GPS
                    locationProvider = LocationManager.GPS_PROVIDER;
                }else if (providers.contains(LocationManager.NETWORK_PROVIDER)){
                    //如果是network
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                }else {

                    return;
                }
                Toast.makeText(getContext(),"888"+ Integer.toString(1),Toast.LENGTH_SHORT).show();
                try{Location location = locationManager.getLastKnownLocation(locationProvider);
                    double lonti = location.getLongitude();
                    String strlonti = Double.toString(lonti);
                    Toast.makeText(getContext(),"333",Toast.LENGTH_SHORT).show();
                    //monitorFragment.setlontitude(strlonti);
                    final String strlati =Double.toString(location.getLatitude());
                    Toast.makeText(getContext(),"6666"+ strlati,Toast.LENGTH_SHORT).show();
                    //monitorFragment.setAltitude(strlati);
                    LocationListener mListener = new LocationListener() {
                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }
                        @Override
                        public void onProviderEnabled(String provider) {
                        }
                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                        // 如果位置发生变化，重新显示
                        @Override
                        public void onLocationChanged(Location location) {
                            double lonti = location.getLongitude();
                            String strlonti = Double.toString(lonti);
                           // monitorFragment.setlontitude(strlonti);
                            String strlati =Double.toString(location.getLatitude());
                            //monitorFragment.setAltitude(strlati);

                        }
                    };
                    locationManager.requestLocationUpdates(locationProvider, 2, 0,mListener);
                }catch (SecurityException e){
                    e.printStackTrace();
                }


            }else {
                Toast.makeText(getContext(),"手动开启",Toast.LENGTH_SHORT).show();
            }
        }
    }
    */




}


