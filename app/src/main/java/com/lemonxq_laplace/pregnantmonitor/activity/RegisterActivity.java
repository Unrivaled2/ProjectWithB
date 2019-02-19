package com.lemonxq_laplace.pregnantmonitor.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lemonxq_laplace.pregnantmonitor.R;
import com.lemonxq_laplace.pregnantmonitor.Util.CommonRequest;
import com.lemonxq_laplace.pregnantmonitor.Util.CommonResponse;
import com.lemonxq_laplace.pregnantmonitor.Util.Consts;
import com.lemonxq_laplace.pregnantmonitor.Util.HttpUtil;
import com.lemonxq_laplace.pregnantmonitor.Util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author: Lemon-XQ
 * @date: 2018/2/6
 */

public class RegisterActivity extends BaseActivity{

    private ImageView back;
    private Button registerBtn;
    private Button cancelBtn;
    private EditText accountText;
    private EditText pwdText;
    private EditText username;
    private EditText studentNumber;
    private EditText confirmPwdText;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initComponents();
        setListeners();
    }

    void initComponents(){
        back = findViewById(R.id.back);
        registerBtn = findViewById(R.id.register_confirm);
        cancelBtn = findViewById(R.id.register_cancel);
        accountText = findViewById(R.id.register_account);
        pwdText = findViewById(R.id.register_pwd);
        confirmPwdText = findViewById(R.id.register_pwd_confirm);
        username = findViewById(R.id.username);
        studentNumber =findViewById(R.id.studentNumber);
        progressDialog = new ProgressDialog(RegisterActivity.this);
    }

    void setListeners(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });// 设置返回键监听
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     *  POST方式Register
     */
    private void register() {
        // 创建请求体对象
        CommonRequest request = new CommonRequest();

        // 前端参数校验，防SQL注入
        String account = Util.StringHandle(accountText.getText().toString());
        String pwd = Util.StringHandle(pwdText.getText().toString());
        String pwd_confirm = Util.StringHandle(confirmPwdText.getText().toString());
        String userName = Util.StringHandle(username.getText().toString());
        String stunumber = Util.StringHandle(studentNumber.getText().toString());

        // 检查数据格式是否正确
       String resMsg = checkDataValid(account,pwd,pwd_confirm,account,stunumber);
        if(!resMsg.equals("")){
            showResponse(resMsg);
            return;
        }

        // 填充参数
        request.addRequestParam("username","lijingtao");
        request.addRequestParam("class_name","117171");
        request.addRequestParam("name","20171002196");
        request.addRequestParam("secret","1234");


        JSONObject object1 = new JSONObject();
        try{
            object1.put("username",userName);
            object1.put("class_name",account);
            object1.put("name",stunumber);
            object1.put("secret",pwd);

        }catch (Exception e){
            e.printStackTrace();
        }


        // POST请求
        HttpUtil.sendPost(Consts.URL_Register, object1.toString(), new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                CommonResponse res = new CommonResponse(response.body().string());
                String resCode = res.getResCode();
                String resMsg = res.getResMsg();
                // 显示注册结果
                showResponse(resMsg);
                // 注册成功
                if (resCode.equals(Consts.SUCCESSCODE_REGISTER)) {
                    finish();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void showResponse(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
             Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String checkDataValid(String account,String pwd,String pwd_confirm,String userName,String studentnumber){
        if(TextUtils.isEmpty(account) | TextUtils.isEmpty(pwd) | TextUtils.isEmpty(pwd_confirm))
            return getResources().getString(R.string.null_hint);
        if(!pwd.equals(pwd_confirm))
            return getResources().getString(R.string.not_equal_hint);
        if(account.length() != 6)
            return getResources().getString(R.string.account_invalid_hint);
        if(studentnumber.length() != 11)
            return getResources().getString(R.string.invlid_stunumber);
        return "";
    }
}
