package com.lemonxq_laplace.pregnantmonitor.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lemonxq_laplace.pregnantmonitor.Data.Record;
import com.lemonxq_laplace.pregnantmonitor.Data.User;
import com.lemonxq_laplace.pregnantmonitor.R;
import com.lemonxq_laplace.pregnantmonitor.ToolBar;
import com.lemonxq_laplace.pregnantmonitor.Util.ACache;
import com.lemonxq_laplace.pregnantmonitor.Util.ActivityController;
import com.lemonxq_laplace.pregnantmonitor.Util.CommonRequest;
import com.lemonxq_laplace.pregnantmonitor.Util.CommonResponse;
import com.lemonxq_laplace.pregnantmonitor.Util.Consts;
import com.lemonxq_laplace.pregnantmonitor.Util.Database;
import com.lemonxq_laplace.pregnantmonitor.Util.HttpUtil;
import com.lemonxq_laplace.pregnantmonitor.Util.PhotoUtil;
import com.baidu.location.BDLocationListener;
import com.lemonxq_laplace.pregnantmonitor.Util.Server;
import com.lemonxq_laplace.pregnantmonitor.Util.SharedPreferencesUtil;
import com.lemonxq_laplace.pregnantmonitor.Util.UserManager;
import com.lemonxq_laplace.pregnantmonitor.Util.Util;
import com.lemonxq_laplace.pregnantmonitor.fragment.DateDialogFragment;
import com.lemonxq_laplace.pregnantmonitor.fragment.MonitorFragment;
import com.lemonxq_laplace.pregnantmonitor.fragment.ToolFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @author LemonXQ
 * @version 1.0
 * @time 2017/11/5
 * @updateAuthor
 */
public class MainActivity extends BaseActivity implements DialogInterface.OnDismissListener {

    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private ViewPager mMViewPager;
    private LinearLayout mMLinearLayout;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;
    private CircleImageView avatarImage;
    private TextView nickNameText;
    private ToolBar mMToolBar;
    private Toolbar mTitleBar;
    private TextView longtitude;
    private TextView altitude;
    private MonitorFragment monitorFragment = new MonitorFragment();
    final DateDialogFragment dateDialog = new DateDialogFragment();
    private PhotoUtil photoUtil = new PhotoUtil();
    private Server server = new Server(this);
    private String[] bottomTitleArr = new String[2];
    private int[] bottomPic = {R.drawable.icon_message, R.drawable.icon_selfinfo};
    private ACache aCache;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String locationProvider;
    private String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
    public LocationClient mLocationClient = null;

    private BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                String strlati = Double.toString(bdLocation.getLatitude());
                String strlonti = Double.toString(bdLocation.getLongitude());
                monitorFragment.longtitude.setText(strlonti);
                monitorFragment.altitude.setText(strlati);
                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation)
                    Toast.makeText(getApplicationContext(), "GPS精确定位", Toast.LENGTH_SHORT).show();
                if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation)
                    Toast.makeText(getApplicationContext(), "网络定位结果", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }

        ;
    };
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    avatarImage.setImageBitmap((Bitmap)msg.obj);
                    break;
                case 2:
                    nickNameText.setText((String)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListeners();
        initData();
        mLocationClient = new LocationClient(getApplicationContext());//声明LocationClient类
        mLocationClient.registerLocationListener(myListener); //注册监听函数
        initLocation();
        mLocationClient.start();//调用LocationClient的start()方法，便可发起定位请求
    };



    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        /**可选，设置定位模式，默认高精度LocationMode.Hight_Accuracy：高精度；
         * LocationMode. Battery_Saving：低功耗；LocationMode. Device_Sensors：仅使用设备；*/
        option.setCoorType("wgs84");
        /**可选，设置返回经纬度坐标类型，默认gcj02gcj02：国测局坐标；bd09ll：百度经纬度坐标；bd09：百度墨卡托坐标；
         海外地区定位，无需设置坐标类型，统一返回wgs84类型坐标*/
        option.setScanSpan(2000);
        /**可选，设置发起定位请求的间隔，int类型，单位ms如果设置为0，则代表单次定位，即仅定位一次，默认为0如果设置非0，需设置1000ms以上才有效*/
        option.setOpenGps(true);
        /**可选，设置是否使用gps，默认false使用高精度和仅用设备两种定位模式的，参数必须设置为true*/
        option.setLocationNotify(true);
/**可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false*/
        option.setIgnoreKillProcess(false);
        /**定位SDK内部是一个service，并放到了独立进程。设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)*/
        option.SetIgnoreCacheException(false);
        /**可选，设置是否收集Crash信息，默认收集，即参数为false*/
        option.setIsNeedAltitude(true);/**设置海拔高度*/
        //option.setWifiCacheTimeOut(5 * 60 * 1000);
        /**可选，7.2版本新增能力如果设置了该接口，首次启动定位时，会先判断当前WiFi是否超出有效期，若超出有效期，会先重新扫描WiFi，然后定位*/
        option.setEnableSimulateGps(false);
        /**可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false*/
        option.setIsNeedAddress(true);
        /**可选，设置是否需要地址信息，默认不需要*/
        mLocationClient.setLocOption(option);
        /**mLocationClient为第二步初始化过的LocationClient对象需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用*/
    }


    void initView(){
        mMViewPager = (ViewPager) findViewById(R.id.viewpagge);
        mMLinearLayout = (LinearLayout) findViewById(R.id.llbootom_container);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavView = (NavigationView) findViewById(R.id.nav_view);
        mTitleBar = (Toolbar) findViewById(R.id.titlebar);
        nickNameText = mNavView.getHeaderView(0).findViewById(R.id.nav_nickname);
        avatarImage = mNavView.getHeaderView(0).findViewById(R.id.nav_avatar);
        aCache = ACache.get(MainActivity.this);
        //数据源添加Fragment
        mFragments.add(monitorFragment);
        mFragments.add(new ToolFragment());
        //尝试活动中改变碎片的UI，使用布局和活动通信


        // 顶部导航条设置
        setSupportActionBar(mTitleBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);// 显示导航按钮
            actionBar.setHomeAsUpIndicator(R.drawable.icon_menu2);// 设置导航按钮图标
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        if(requestCode == 31){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MainActivity.this,"权限获取成功",Toast.LENGTH_SHORT).show();

                /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                //2.获取位置提供器，GPS或是NetWork
                List<String> providers = locationManager.getAllProviders();
                if ( providers.contains(LocationManager.GPS_PROVIDER)){
                    //如果是GPS
                    locationProvider = LocationManager.GPS_PROVIDER;
                }else if (providers.contains(LocationManager.NETWORK_PROVIDER)){
                    //如果是network
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                }else {

                    return;
                }
                try{
                    Location location = locationManager.getLastKnownLocation(locationProvider);
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
                            monitorFragment.longtitude.setText(strlonti);
                            String strlati =Double.toString(location.getLatitude());
                           monitorFragment.altitude.setText(strlati);
                        }
                    };
                    if(location!=null) {
                        locationManager.requestLocationUpdates(locationProvider, 60000, 2,mListener);
                        double lonti = location.getLongitude();
                        String strlonti = Double.toString(lonti);
                        monitorFragment.longtitude.setText(strlonti);
                        final String strlati = Double.toString(location.getLatitude());
                        monitorFragment.altitude.setText(strlati);
                    }
                    locationManager.requestLocationUpdates(locationProvider, 2000, 2,mListener);
                }catch (SecurityException e){
                    e.printStackTrace();
                }catch (NullPointerException error){
                    error.printStackTrace();
                    Log.d("tag","*************************************************");
                }

               /* locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                //2.获取位置提供器，GPS或是NetWork
                List<String> providers = locationManager.getAllProviders();
                if ( providers.contains(LocationManager.GPS_PROVIDER)){
                    //如果是GPS
                    locationProvider = LocationManager.GPS_PROVIDER;
                }else if (providers.contains(LocationManager.NETWORK_PROVIDER)){
                    //如果是network
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                }else {
                    Toast.makeText(this,"提供器为空",Toast.LENGTH_LONG).show();
                    Log.d("test","-----------------------");
                    return;
                }
                Location location;
                String contextService = Context.LOCATION_SERVICE;
                String provider;
                double lat;
                double lon;
                locationManager = (LocationManager) getSystemService(contextService);
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);// 高精度
                criteria.setAltitudeRequired(false);// 不要求海拔
                criteria.setBearingRequired(false);// 不要求方位
                criteria.setCostAllowed(true);// 允许有花费
                criteria.setPowerRequirement(Criteria.POWER_LOW);// 低功耗
                // 从可用的位置提供器中，匹配以上标准的最佳提供器
                provider = locationManager.getBestProvider(criteria, true);
                // 获得最后一次变化的位置
                if(ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
                    location = locationManager.getLastKnownLocation(provider);
                    Log.d("this","11111111111111111111111111111111111111111");
                    locationListener = new LocationListener() {
                        public void onStatusChanged(String provider, int status,
                                                    Bundle extras) {
                            // TODO Auto-generated method stub
                        }

                        public void onProviderEnabled(String provider) {
                            // TODO Auto-generated method stub
                        }

                        public void onProviderDisabled(String provider) {
                            // TODO Auto-generated method stub
                        }

                        public void onLocationChanged(Location location) {
                            double lat = location.getLatitude();
                            double lon = location.getLongitude();
                            Log.d("this","111111111111111111111111155555555555555555555555555");
                            monitorFragment.longtitude.setText(Double.toString(lon));
                            monitorFragment.altitude.setText(Double.toString(lat));
                            Log.e("android_lat", String.valueOf(lat)+"***********************************************************************************************************");
                            Log.e("android_lon", String.valueOf(lon)+"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        }
                    };
                    if(location!=null){
                        locationManager.requestLocationUpdates("gps",60000,1,locationListener);
                        double la = location.getLatitude();
                        double lo = location.getLongitude();
                        Log.d("this","111111111111111111111111155555555555555555555555555");
                        monitorFragment.longtitude.setText(Double.toString(lo));
                        monitorFragment.altitude.setText(Double.toString(la));
                        Log.e("android_lat", String.valueOf(la)+"***********************************************************************************************************");
                        Log.e("android_lon", String.valueOf(lo)+"++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    };
                    // 监听位置变化，2秒一次，距离10米以上
                    locationManager.requestLocationUpdates(provider, 2000, 10,
                            locationListener);
                }*/
            }else {
                Toast.makeText(MainActivity.this,"手动开启",Toast.LENGTH_SHORT).show();
            }
        };
        if(requestCode==OPEN_CANMER)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //image_uri = createImagePathUri(getApplicationContext());
                //takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
                if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takeVideoIntent, REQUEST_TAKE_PHOTO);
                };
            }
            else
            {
                Toast.makeText(this,"camera permission failed",Toast.LENGTH_LONG).show();
            }
        }
    };

    private boolean crop(Activity activity, Uri uri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(uri, "image/*");
        cropIntent.putExtra("crop", "true");
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("outputX", 200);
        cropIntent.putExtra("outputY", 200);
        cropIntent.putExtra("return-data", false);
        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        if (!isIntentAvailable(activity, cropIntent)) {
            Toast.makeText(MainActivity.this,"裁剪intent不可得",Toast.LENGTH_LONG).show();
            return false;
        } else {
            try {
                activity.startActivityForResult(cropIntent, CROP_INTENT);
                Toast.makeText(this,"crop intent success",Toast.LENGTH_LONG).show();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
    public boolean isIntentAvailable(Activity activity, Intent intent) {
        PackageManager packageManager = activity.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    private static Uri createImagePathUri(Context context) {
        Uri imageFilePath = null;
        String status = Environment.getExternalStorageState();
        SimpleDateFormat timeFormatter = new SimpleDateFormat(
                "yyyyMMdd_HHmmss", Locale.CHINA);
        long time = System.currentTimeMillis();
        String imageName = timeFormatter.format(new Date(time));
        // ContentValues是我们希望这条记录被创建时包含的数据信息
        ContentValues values = new ContentValues(3);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName);
        values.put(MediaStore.Images.Media.DATE_TAKEN, time);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
            imageFilePath = context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } else {
            imageFilePath = context.getContentResolver().insert(
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
        }
        Log.i("", "生成的照片输出路径:" + imageFilePath.toString());
        return imageFilePath;
    }
    void setListeners(){
        // 底部导航栏设置
        bottomTitleArr[0] =getResources().getString(R.string.Monitor);
        bottomTitleArr[1] = getResources().getString(R.string.Tool);
        mMViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));
        mMToolBar = new ToolBar();
        // 给底部tab添加新的控件
        mMToolBar.addBottomTab(mMLinearLayout, bottomTitleArr, bottomPic);
        mMToolBar.changeColor(1);// 设置默认第一个tab颜色为选中状态
        // viewpager监听器
        mMViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 某项ViewPage选中的时候调用。在这里顺便改变底部Tab的颜色
                mMToolBar.changeColor(1);
                // 同时修改对应fragment的标题
                mTitleBar.setTitle(bottomTitleArr[1]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // 接口回调的方式，点击底部Tab，切换不同的页面
        mMToolBar.setOnToolBarChangeListener(new ToolBar.OnToolBarChangeListener() {
            @Override
            public void onToolBarChange(int position) {
                // 切换对应的fragment,此时设置为出行监测
                mMViewPager.setCurrentItem(position);
            }
        });

        // 左拉菜单栏设置
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //处理任意菜单项点击
                switch (item.getItemId()) {
                    case R.id.nav_center:// 个人中心
                        autoStartActivity(center_test.class);
                        break;

                    case R.id.nav_setting:// 设置
                        Log.d("test","start center activity&&&&&&&&&&&&&&&&&&&&&&&&&");
                        autoStartActivity(SettingActivity.class);
                        break;

                    case R.id.nav_info:// 关于
                        autoStartActivity(AboutActivity.class);
                        break;

                    case R.id.nav_logout:// 注销
                        SharedPreferencesUtil spu = new SharedPreferencesUtil(MainActivity.this);
                        spu.setParam("isAutoLogin",false);
                        ActivityController.finishAll(LogInActivity.class);
                        ActivityController.clearAcache();
                        UserManager.clear();
                        break;

                    case R.id.nav_exit:// 退出
                        ActivityController.finishAll();
                        break;

                    default:
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        // 监听按下头像事件
        avatarImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoDialog();
            }
        });

        // 头像工具类回调
        photoUtil.setOnPhotoResultListener(new PhotoUtil.OnPhotoResultListener() {
            // 当选择图片或者拍摄图片拿到结果之后
            @Override
            public void onPhotoResult(final Uri uri) {
                if (uri != null && !TextUtils.isEmpty(uri.getPath())) {
                    final Bitmap bitmap = PhotoUtil.decodeUriAsBitmap(MainActivity.this,uri);
                    // 上传头像
                    if(UserManager.getCurrentUser().isVisitor()){// 游客不进行服务器端头像存储
                        avatarImage.setImageBitmap(bitmap);
                        return;
                    }
                    HttpUtil.uploadImage(Consts.URL_UploadImg,bitmap,new okhttp3.Callback(){
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            CommonResponse res = new CommonResponse(response.body().string());
                            String resCode = res.getResCode();
                            String resMsg = res.getResMsg();
                            // 上传成功
                            if(resCode.equals(Consts.SUCCESSCODE_UPLOADIMG)){
                                saveAvatar(bitmap);
                            }
                            showResponse(resMsg);
                        }

                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            showResponse("Network ERROR!");
                        }
                    });
                }
            }

            @Override
            public void onPhotoCancel() {

            }
        });
    }

    void initData(){
        // 初始化用户记录
        User user = UserManager.getCurrentUser();
        List<Record> recordList = Database.findRecords(user);
        // 本地数据库中无记录，则在服务器端加载
        if(user.isVisitor()){
            if(!recordList.isEmpty()){
                user.getRecordList().addAll(recordList);
                user.save();
            }
        }else{
            if(!recordList.isEmpty() || !(recordList = server.getRecords()).isEmpty()){
                user.getRecordList().addAll(recordList);
                user.save();
            }
        }

        // 初始化昵称
        String nickname = user.getNickname();
        if(user.isVisitor()){
            if(nickname!=null) {
                user.setNickname(nickname);
                user.save();
            }else{
                nickname = "Visitor";
            }
        }else{
            if(nickname != null || !(nickname = server.getNickname()).equals("null")){
                Log.e("INIT",nickname);
                user.setNickname(nickname);
                user.save();
            }else {
                nickname = "UserName";
            }
        }
        nickNameText.setText(nickname);

        // 加载头像顺序：缓存->本地数据库->服务器
        Bitmap bitmap;
        if((bitmap = aCache.getAsBitmap("avatar")) == null){// cache加载失败
            // 数据库加载失败则从服务器加载（仅非游客有效）
            if(user.isVisitor()){
                if(user.getAvatarImage() != null){
                    // 数据库加载
                    bitmap = BitmapFactory.decodeByteArray(user.getAvatarImage(),0,
                            user.getAvatarImage().length);
                    avatarImage.setImageBitmap(bitmap);
                }else{
                    avatarImage.setImageResource(R.drawable.ic_launcher);// 默认头像
                }
                return;
            }
            // 非游客
            if(user.getAvatarImage() == null){
                CommonRequest request = new CommonRequest();
                request.addRequestParam("account",user.getAccount());
                HttpUtil.sendPost(Consts.URL_DownloadImg,request.getJsonStr(),new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        CommonResponse res = new CommonResponse(response.body().string());
                        String resCode = res.getResCode();
                        String resMsg = res.getResMsg();
                        HashMap<String,String> property = res.getPropertyMap();
                        String imgStr = property.get("avatar");
                        loadAvatar(resCode,imgStr);
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        showResponse("Network ERROR");
                    }
                });
            }else{// 数据库加载
//                showResponse("数据库加载头像");
                bitmap = BitmapFactory.decodeByteArray(user.getAvatarImage(),0,
                        user.getAvatarImage().length);
                avatarImage.setImageBitmap(bitmap);
            }
        }else {
//            showResponse("缓存加载头像");
            avatarImage.setImageBitmap(bitmap);
        }
    }

    private void saveAvatar(final Bitmap bitmap){
        // 设置头像
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                avatarImage.setImageBitmap(bitmap);
            }
        });
        // 保存头像到本地数据库
        User user = UserManager.getCurrentUser();
        user.setAvatarImage(PhotoUtil.bitmap2Bytes(bitmap));
        user.save();
        // 写入缓存
        aCache.put("avatar",bitmap);
    }

    private void loadAvatar(String resCode,String imgStr){
        if (resCode.equals(Consts.SUCCESSCODE_DOWNLOADIMG) && !imgStr.equals("null") && !imgStr.equals("")) {
//            showResponse("头像："+imgStr);
            // 获取头像成功
            byte[] bytes = Base64.decode(imgStr, Base64.DEFAULT);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    avatarImage.setImageBitmap(bitmap);
                }
            });
        }else{
//            showResponse("使用默认头像");
            // 使用默认头像
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    avatarImage.setImageResource(R.drawable.ic_launcher);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 导航按钮
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            // 其他ToolBar按钮
            default:
                break;
        }
        return true;
    }

    private void showDateDialog(){
        User user = UserManager.getCurrentUser();
        if(user.isVisitor()){
            if(Database.findBirthDateByUsername(UserManager.getCurrentUser().getAccount()) == null){
                // 本地无日期信息，则弹日期填写框
                dateDialog.setCancelable(false);
                dateDialog.show(getFragmentManager(),"date");
            }
        }else{
            if(Database.findBirthDateByUsername(UserManager.getCurrentUser().getAccount()) == null){
                Date birthDate, pregnantDate;
                if((birthDate = server.getBirthDate()) != null ){
                    // 同步服务端信息至本地数据库
                    user.setBirthDate(birthDate);
                    if((pregnantDate = server.getPregnantDate())!=null)
                        user.setPregnantDate(pregnantDate);
                    user.save();
                }else {
                    // 本地和服务端均无日期信息，则弹日期填写框
                    dateDialog.setCancelable(false);
                    dateDialog.show(getFragmentManager(),"date");
                }
            }
        }
    }
    static final int REQUEST_TAKE_PHOTO = 2;
    static final int OPEN_CANMER = 4;
    static final int CROP_INTENT = 5;
    Uri image_uri;//该Uri必须在赋值之后使用否则是空值
    void showPhotoDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.uploadAvatar));
        final String[] choices = {getResources().getString(R.string.takePhoto), getResources().getString(R.string.chooseFromGallery)};
        // 设置一个下拉的列表选择项
        builder.setItems(choices, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch(which)
                {
                    case 0:// 相机
                        int checkCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);
                        if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.CAMERA},OPEN_CANMER);
                            return;
                        }else{
                            Intent takeVideoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //image_uri = createImagePathUri(getApplicationContext());
                            //takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
                            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takeVideoIntent, REQUEST_TAKE_PHOTO);
                            };
                        }
                        break;
                    case 1:// 本地相册
                        photoUtil.selectPicture(MainActivity.this,getApplicationContext());
                        break;

                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bundle extras = intent.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //crop(MainActivity.this,image_uri);
            avatarImage.setImageBitmap(imageBitmap);
        };
        if(requestCode==CROP_INTENT&&resultCode==RESULT_OK)
        {
            Bundle extras = intent.getExtras();
            Bitmap image = (Bitmap)extras.get("data");
            avatarImage.setImageBitmap(image);
        }

    }




    private void showResponse(final String msg) {
        Log.e("MainActivity",msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        finishInitDate(dateDialog.pregnantDate);
    }

    void finishInitDate(Date pregnantDate){
        if(pregnantDate != null){
            // 更新UI界面
            long days = Util.getPregnantDays(pregnantDate);
            long months = Util.getPregnantMonths(pregnantDate);
            long weeks = Util.getPregnantWeeks(pregnantDate);
            monitorFragment.getLocation(MainActivity.this);
        }
    }

    class MyFragmentAdapter extends FragmentPagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    //存放上次点击“返回键”的时刻
    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //判断用户是否点击了“返回键”
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //与上次点击返回键时刻作差,大于2000ms是误操作
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, getResources().getString(R.string.exit_hint), Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();// 记录本次点击“返回键”的时刻
            } else {
                //小于2000ms退出程序
                ActivityController.finishAll();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
