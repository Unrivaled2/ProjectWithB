package com.lemonxq_laplace.pregnantmonitor.fragment;


import android.content.Context;
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

import com.lemonxq_laplace.pregnantmonitor.R;
import com.lemonxq_laplace.pregnantmonitor.Util.CommonRequest;
import com.lemonxq_laplace.pregnantmonitor.Util.Consts;
import com.lemonxq_laplace.pregnantmonitor.Util.HttpUtil;
import com.lemonxq_laplace.pregnantmonitor.Util.Util;
import com.lemonxq_laplace.pregnantmonitor.activity.AnalyzeActivity;
import com.lemonxq_laplace.pregnantmonitor.activity.FStepActivity;

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

    private View view;
    private VideoView videoview;
    private SeekBar seekBar;
    private TextView black1;
    private LinearLayout linearLayout;
    private ImageView imageView1;
    private RelativeLayout relativeLayout1;
    private RelativeLayout relativeLayout2;
    private TextView black2;
    private ImageView imageview2;
    private TextView black3;
    private Button up_load;
    private Uri videouri;
    private File mediaFile;
    private TextView image1_text;
    private TextView image2_text;
    private Handler mhandler;
    private static final int VIDEO_CAPTURE = 101;
    public HttpUtil util;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tool, container, false);
        InitComponent();
        up_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                take_video();
                //playVideo();
            }
        });

        return view;

    }

    private void InitComponent() {
        videoview = view.findViewById(R.id.vv_player);
        seekBar = view.findViewById(R.id.sb_select);
        mhandler = new Handler();
        black1 = view.findViewById(R.id.black);
        linearLayout = view.findViewById(R.id.show_image);
        imageView1 = view.findViewById(R.id.iv_head1);
        black2 = view.findViewById(R.id.images_blank);
        imageview2 = view.findViewById(R.id.iv_head2);
        black3 = view.findViewById(R.id.black3);
        up_load = view.findViewById(R.id.get_video_sigh_up);
        relativeLayout1 = view.findViewById(R.id.relativelayout);
        relativeLayout2 = view.findViewById(R.id.relativelayout2);
        image1_text = view.findViewById(R.id.myImageViewText);
        image2_text = view.findViewById(R.id.myImageViewText2);
        util = new HttpUtil();

    }

    private void take_video(){
        /*Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myvideo.mp4");
        if(mediaFile.exists()){
            mediaFile.delete();
        }
        videouri = Uri.fromFile(mediaFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,videouri);
        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        //getActivity().startActivityForResult(intent,VIDEO_CAPTURE);
        //getActivity().startActivity(intent);
        Context context = getActivity();
        if(intent.resolveActivity(context.getPackageManager())!= null){
            startActivityForResult(intent,VIDEO_CAPTURE);
        }*/
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res,R.mipmap.ic_launcher);
        final File file = change_to_file(bitmap);
        new Thread(new Runnable() {
            @Override
            public void run() {
                uploadFileAndString(Consts.URL_UPLOAD_IMAGE,file.getName(),file);
            }
        }).start();
    };


    private void uploadFileAndString(String actionUrl, String newName, File uploadFile) {
        String end = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            URL url = new URL(actionUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /* 允许Input、Output，不使用Cache */
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            /* 设置传送的method=POST */
            con.setRequestMethod("POST");
            /* setRequestProperty */
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            con.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            con.connect();
            /* 设置DataOutputStream */
            DataOutputStream ds = new DataOutputStream(con.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; "
                    + "name=\"image01\";filename=\"" + newName + "\"" + end);
            ds.writeBytes(end);
            /* 取得文件的FileInputStream */
            FileInputStream fStream = new FileInputStream(uploadFile);
            /* 设置每次写入1024bytes */
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int length = -1;
            /* 从文件读取数据至缓冲区 */
            while ((length = fStream.read(buffer)) != -1) {
                /* 将资料写入DataOutputStream中 */
                ds.write(buffer, 0, length);
            }
            ds.writeBytes(end);

            // -----
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data;name=\"name\"" + end);
            ds.writeBytes(end + URLEncoder.encode("xiexiezhichi", "UTF-8")
                    + end);
            // -----

            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            fStream.close();
            ds.flush();

            /* 取得Response内容 */
            InputStream is = con.getInputStream();
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            String result = b.toString();
            Toast.makeText(getContext(),result,Toast.LENGTH_SHORT);
            con.disconnect();

            /* 关闭DataOutputStream */
            ds.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File change_to_file(Bitmap bitmap){
        if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)) {
            String sdCardDir = Environment.getExternalStorageDirectory() + "/BLImage/";
            File dirfile = new File(sdCardDir);
            if (!dirfile.exists()) {
                dirfile.mkdirs();
            }
            File file = new File(sdCardDir, "face.jpg");
            FileOutputStream out;
            try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Toast.makeText(getContext(), "store success", Toast.LENGTH_SHORT);
            return file;
        }
        return null;
    };

    public static void uploadImage(String address, Bitmap bitmap, okhttp3.Callback callback){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//将Bitmap转成Byte[]
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//压缩
        //String imgStr = Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT);//加密转换成String

        CommonRequest commonrequest = new CommonRequest();
        //commonrequest.addRequestParam("name","20171002196");
        //commonrequest.addRequestParam("class_name","117171");
        commonrequest.addRequestParam("image01",baos.toByteArray().toString());
        RequestBody requestBody = RequestBody.create(JSON,commonrequest.getJsonStr());
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
    public static File saveBitmapFile(Bitmap bitmap, String filepath){
        File file=new File(filepath);//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == -1) {
               Toast.makeText(getContext(),"video record successfully",Toast.LENGTH_SHORT).show();
                MediaMetadataRetriever media = new MediaMetadataRetriever();
                media.setDataSource(mediaFile.getPath());
                Bitmap image1 = media.getFrameAtTime(1);
                Bitmap image2 = media.getFrameAtTime(1,MediaMetadataRetriever.OPTION_CLOSEST);
                imageView1.setImageBitmap(image1);
                image1_text.setText("");
                image2_text.setText("");
                imageview2.setImageBitmap(image2);
            } else if (resultCode == 0) {
                Toast.makeText(getContext(),"video record canceled",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),"video capture failed",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playVideo() {

        videoview.setVideoURI(videouri);
        videoview.start();
        // Updating progress bar
        updateProgressBar();
    }

    private void updateProgressBar() {
        mhandler.postDelayed(updateTimeTask, 100);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {
            seekBar.setProgress(videoview.getCurrentPosition());
            seekBar.setMax(videoview.getDuration());
            mhandler.postDelayed(this, 100);
        }
    };

    public void onProgressChanged(SeekBar seekbar, int progress,boolean fromTouch) {

    }
    public void onStartTrackingTouch(SeekBar seekbar) {
        mhandler.removeCallbacks(updateTimeTask);
    }
    public void onStopTrackingTouch(SeekBar seekbar) {
        mhandler.removeCallbacks(updateTimeTask);
        videoview.seekTo(seekbar.getProgress());
        updateProgressBar();
    }



}

