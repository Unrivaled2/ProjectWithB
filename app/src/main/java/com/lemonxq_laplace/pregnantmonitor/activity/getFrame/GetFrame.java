package com.lemonxq_laplace.pregnantmonitor.activity.getFrame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lemonxq_laplace.pregnantmonitor.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
public class GetFrame extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private TextureView textureView;
    private ImageView ivPic;
    private Button btnTakePic;
    private Button take_again;
    private Button upload;
    private ImageView image01;
    private ImageView image02;
    private Camera mCamera;
    public int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_frame);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        textureView.setSurfaceTextureListener(this);
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCallBack();
            }
        });
        take_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count=0;
            }
        });

    }

    private void initData() {
        int numberOfCameras = Camera.getNumberOfCameras();// 获取摄像头个数
        if(numberOfCameras<1){
            Toast.makeText(this, "没有相机", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void initView() {
        textureView = (TextureView) findViewById(R.id.texture_view);
//        textureView.setRotation(0); // // 设置预览角度，并不改变获取到的原始数据方向(与Camera.setDisplayOrientation(0)相同)
       // ivPic = (ImageView) findViewById(R.id.iv_pic);
        btnTakePic = (Button) findViewById(R.id.btn_takePic);
        take_again = (Button)findViewById(R.id.take_gain);
        upload = (Button)findViewById(R.id.sign_up);
        image01 = (ImageView)findViewById(R.id.image01);
        image02 = (ImageView)findViewById(R.id.image02);

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        // 打开相机 0后置 1前置
        mCamera = Camera.open(1);
        if (mCamera != null) {
            // 设置相机预览宽高，此处设置为TextureView宽高
            Camera.Parameters params = mCamera.getParameters();
            params.setPreviewSize(width, height);
            // 设置自动对焦模式
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.setParameters(params);
            }
            try {
//                mCamera.setDisplayOrientation(0);// 设置预览角度，并不改变获取到的原始数据方向
                // 绑定相机和预览的View
                mCamera.setPreviewTexture(surface);
                // 开始预览
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addCallBack() {
        if(mCamera!=null){
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    Camera.Size size = camera.getParameters().getPreviewSize();
                    try{
                        YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                        if(image!=null){
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
                            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                            Toast.makeText(getApplicationContext(),"one image",Toast.LENGTH_SHORT).show();
                            if(count==1)
                            {
                                image01.setImageBitmap(bmp);
                            }
                            else if(count==5){
                                image02.setImageBitmap(bmp);
                            }
                            count++;
                            stream.close();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return false;
    }
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
}