package com.lemonxq_laplace.pregnantmonitor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import com.lemonxq_laplace.pregnantmonitor.R;
import com.lemonxq_laplace.pregnantmonitor.fragment.MonitorFragment;
import com.lemonxq_laplace.pregnantmonitor.nearby_location.App;
import com.lemonxq_laplace.pregnantmonitor.nearby_location.LocationService;
import com.lemonxq_laplace.pregnantmonitor.nearby_location.PermissionActivity;
import com.lemonxq_laplace.pregnantmonitor.nearby_location.PlaceAdapter;

public class Show_location extends PermissionActivity implements AdapterView.OnItemClickListener, OnGetGeoCoderResultListener
        , BaiduMap.OnMapStatusChangeListener, OnGetPoiSearchResultListener{

    private MapView mAtyMainMapView;
    private ListView mAtyMainListView;

    private PlaceAdapter mPlaceAdapter;

    private BaiduMap mBaiduMap;
    private GeoCoder mGeoCoder;
    private PoiSearch mPoiSearch;
    private MonitorFragment monitorFragment = new MonitorFragment();
    private LocationService locationService;
    private final int LOCATION_REQUEST_CODE = 1001;
    private final int SPEECH_REQUEST_CODE = 1002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        initView();
        initListener();
        requestPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, LOCATION_REQUEST_CODE);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAtyMainMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAtyMainMapView.onPause();
    }

    @Override
    public void onStop() {
        locationService.unregisterListener(mBDLocationListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAtyMainMapView.onDestroy();
    }

    @Override
    public void permissionSuccess(int requestCode) {
        super.permissionSuccess(requestCode);
        if (requestCode == LOCATION_REQUEST_CODE) { //权限通过
            initLocation();
        }
    }

    @Override
    public void permissionFail(int requestCode) {
        super.permissionFail(requestCode);
        //权限不通过
    }

    private BDLocationListener mBDLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (null != bdLocation && bdLocation.getLocType() != BDLocation.TypeServerError) {
                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
                mBaiduMap.animateMapStatus(u);
                mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption()).location(latLng));
                locationService.unregisterListener(mBDLocationListener);
                locationService.stop(); //停止定位服务
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    };

    // 初始化
    private void initView() {

        mAtyMainMapView = (MapView) findViewById(R.id.aty_main_map_view);
        mAtyMainListView = (ListView) findViewById(R.id.aty_main_list_view);

        mPoiSearch = PoiSearch.newInstance();
        mGeoCoder = GeoCoder.newInstance();
        mAtyMainMapView.setLogoPosition(LogoPosition.logoPostionCenterTop);
        mPlaceAdapter = new PlaceAdapter(this, new ArrayList<PoiInfo>());
        mAtyMainListView.setAdapter(mPlaceAdapter);

        mBaiduMap = mAtyMainMapView.getMap();
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
        mBaiduMap.setMapStatus(msu);
    }

    // 相关Listener设置
    private void initListener() {
        mAtyMainListView.setOnItemClickListener(this);
        mGeoCoder.setOnGetGeoCodeResultListener(this);
        mBaiduMap.setOnMapStatusChangeListener(this);
        mPoiSearch.setOnGetPoiSearchResultListener(this);
    }

    // 定位初始化，并开始定位
    private void initLocation() {
        locationService = ((App) getApplication()).locationService;
        locationService.registerListener(mBDLocationListener);
        int type = getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }
        locationService.start();// 定位SDK
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        /*
        PoiInfo poi = (PoiInfo)mPlaceAdapter.getItem(position);
        String address = poi.address;
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("place",address);
        startActivity(intent);
        */
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {
        // 地图移动完成后，根据地图的中点坐标重新获取附近建筑坐标
        mGeoCoder.reverseGeoCode((new ReverseGeoCodeOption()).location(new LatLng(mapStatus.target.latitude, mapStatus.target.longitude)));
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
            // 没有找到检索结果
        }
        // 获取反向地理编码结果
        else {
            mPlaceAdapter.clear();
            if (reverseGeoCodeResult.getPoiList() != null) {
                // 当前位置信息列表
                mPlaceAdapter.addAll(reverseGeoCodeResult.getPoiList());
            }

        }
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {
        if (poiResult == null || poiResult.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(this, "没有搜索内容", Toast.LENGTH_SHORT).show();
            return;
        }
        //显示位置列表
        mPlaceAdapter.replaceAll(poiResult.getAllPoi());
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

}
