package com.lemonxq_laplace.pregnantmonitor.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lemonxq_laplace.pregnantmonitor.Data.User;
import com.lemonxq_laplace.pregnantmonitor.R;
import com.lemonxq_laplace.pregnantmonitor.Util.CommonRequest;
import com.lemonxq_laplace.pregnantmonitor.Util.CommonResponse;
import com.lemonxq_laplace.pregnantmonitor.Util.Consts;
import com.lemonxq_laplace.pregnantmonitor.Util.HttpUtil;
import com.lemonxq_laplace.pregnantmonitor.Util.SharedPreferencesUtil;
import com.lemonxq_laplace.pregnantmonitor.Util.UserManager;
import com.lemonxq_laplace.pregnantmonitor.Util.Util;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;


public class LogInActivity extends BaseActivity {

    private ProgressBar progressBar;
    private Button loginBtn;
    private Button registerBtn;
    private TextView visitorText;
    private EditText accountText;
    private EditText passwordText;
    private CheckBox isRememberPwd;
    private CheckBox isAutoLogin;

    private String account;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initComponents();
        setListeners();
        requestPermission();
        // 自动填充
        SharedPreferencesUtil spu = new SharedPreferencesUtil(this);
        Boolean isRemember = (Boolean) spu.getParam("isRememberPwd",false);
        Boolean isAutoLogin = (Boolean) spu.getParam("isAutoLogin",false);
        // SharedPreference获取用户账号密码，存在则填充
        String account = (String) spu.getParam("account","");
        String pwd = (String)spu.getParam("pwd","");
        if(!account.equals("") && !pwd.equals("")){
            if(isRemember){
                accountText.setText(account);
                passwordText.setText(pwd);
                isRememberPwd.setChecked(true);
            }
            if(isAutoLogin)
                Login();
        }

    }

    final int CAMERA_OK=203;

    private void requestPermission()
    {
        if (Build.VERSION.SDK_INT>22){
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(LogInActivity.this, new String[]{android.Manifest.permission.CAMERA},CAMERA_OK);

            }
        }

    }

    @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        switch (requestCode) {
            case CAMERA_OK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //这里已经获取到了摄像头的权限，想干嘛干嘛了可以 }else { //这里是拒绝给APP摄像头权限，给个提示什么的说明一下都可以。
                    // Toast.makeText(MainActivity.this,"请手动打开相机权限",Toast.LENGTH_SHORT).show(); }
                    // break; default: break;
                } else {
                    Toast.makeText(getApplicationContext(), "签到功能无法使用", Toast.LENGTH_SHORT).show();
                }
        }
    }

    void initComponents(){
        loginBtn = findViewById(R.id.login);
        registerBtn = findViewById(R.id.register);
        visitorText = findViewById(R.id.visitor);
        accountText = findViewById(R.id.account);
        passwordText = findViewById(R.id.password);
        isRememberPwd = findViewById(R.id.login_remember);
        isAutoLogin = findViewById(R.id.login_auto);
        progressBar = findViewById(R.id.progressbar);

        LitePal.getDatabase();// 建立数据库
        UserManager.clear();
    }

    void setListeners(){
        loginBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        registerBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        visitorText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 若已有游客账号则以游客身份登录，不存在则新建游客账号
                User visitor = DataSupport.where("isVisitor = ?","1")
                                          .findFirst(User.class);
                if(visitor == null){
                    visitor = new User();
                    visitor.setAccount("Visitor");
                    visitor.setPassword("Visitor");
                    visitor.setVisitor(true);
                    visitor.save();
                }
                UserManager.setCurrentUser(visitor);
                autoStartActivity(MainActivity.class);
            }
        });
    }

    /**
     *  POST方式Login
     */
    private void Login() {
        // 创建请求体对象
        CommonRequest request = new CommonRequest();

        // 前端参数校验，防SQL注入
        account = Util.StringHandle(accountText.getText().toString());
        password = Util.StringHandle(passwordText.getText().toString());

        // 检查数据格式是否正确
        String resMsg = checkDataValid(account,password);
        if(!resMsg.equals("")){
            showResponse(resMsg);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);// 显示进度条
        OptionHandle(account,password);// 处理自动登录及记住密码

        // 填充参数
        request.addRequestParam("account",account);
        request.addRequestParam("pwd",password);

        // POST请求
        HttpUtil.sendPost(Consts.URL_Login, request.getJsonStr(), new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                CommonResponse res = new CommonResponse(response.body().string());
                String resCode = res.getResCode();
                String resMsg = res.getResMsg();
                // 登录成功
                if (resCode.equals(Consts.SUCCESSCODE_LOGIN)) {
                    // 查找本地数据库中是否已存在当前用户,不存在则新建用户并写入
                    User user = DataSupport.where("account=?",account).findFirst(User.class);
                    if(user == null){
                        user = new User();
                        user.setAccount(account);
                        user.setPassword(password);
                        user.setVisitor(false);
                        user.save();
                    }
                    UserManager.setCurrentUser(user);// 设置当前用户

                    autoStartActivity(MainActivity.class);
                }
                showResponse(resMsg);
            }
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                showResponse("Network ERROR");
            }
        });
    }

    private void showResponse(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LogInActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String checkDataValid(String account,String pwd){
        if(TextUtils.isEmpty(account) | TextUtils.isEmpty(pwd))
            return getResources().getString(R.string.null_hint);
        if(account.length() != 7 && pwd.length() != 11)
            return getResources().getString(R.string.account_invalid_hint);
        return "";
    }

    void OptionHandle(String account,String pwd){
        SharedPreferences.Editor editor = getSharedPreferences("UserData",MODE_PRIVATE).edit();
        SharedPreferencesUtil spu = new SharedPreferencesUtil(this);
        if(isRememberPwd.isChecked()){
            editor.putBoolean("isRememberPwd",true);
            // 保存账号密码
            spu.setParam("account",account);
            spu.setParam("pwd",pwd);
        }else{
            editor.putBoolean("isRememberPwd",false);
        }
        if(isAutoLogin.isChecked()){
            editor.putBoolean("isAutoLogin",true);
        }else{
            editor.putBoolean("isAutoLogin",false);
        }
        editor.apply();
    }

}
