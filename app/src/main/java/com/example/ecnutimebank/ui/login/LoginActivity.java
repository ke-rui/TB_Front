package com.example.ecnutimebank.ui.login;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;

import com.example.ecnutimebank.BaseActivity;
import com.example.ecnutimebank.entity.User;
import com.example.ecnutimebank.helper.AppConst;
import com.example.ecnutimebank.R;
import com.example.ecnutimebank.helper.JsonCallBack;
import com.example.ecnutimebank.helper.Result;
import com.example.ecnutimebank.helper.ResultCode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private Button btGo;
    private CardView cv;
    private FloatingActionButton fab;
    private TextInputLayout usernameInput, passwdInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        setListener();
    }
    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btGo = findViewById(R.id.bt_go);
        cv = findViewById(R.id.cv);
        fab = findViewById(R.id.fab);
        usernameInput = findViewById(R.id.username_input);
        passwdInput = findViewById(R.id.passwd_input);

        /*
        ???????????????TextInputLayout??????
         */
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void afterTextChanged(Editable s) {
                boolean flag = verifyLoginTeleInfo(s.toString());
                if(flag){
                    usernameInput.setErrorEnabled(false);
                    btGo.setClickable(true);
                }else {
                    usernameInput.setError(getString(R.string.username_input_error));
                    btGo.setClickable(false);
                }
            }
        });

        /*
        ????????????TextInputLayout??????
         */
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean flag = verifyLoginPasswdInfo(s.toString());
                if(flag){
                    passwdInput.setErrorEnabled(false);
                    btGo.setClickable(true);
                }else{
                    passwdInput.setError(getString(R.string.passwd_input_error));
                    btGo.setClickable(false);
                }
            }
        });
    }

    private void setListener() {
        btGo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                final String telephone = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                login(telephone, password);
            }
        });
        //??????????????????
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, fab, fab.getTransitionName());
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class), options.toBundle());
            }
        });
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }


    private void failedLoginToast(){
        Toast toast = Toast.makeText(this,"????????????,??????????????????????????????",Toast.LENGTH_LONG);
        toast.show();
    }
    private void successLoginToast(){
        Toast toast = Toast.makeText(this, "???????????????", Toast.LENGTH_LONG);
        toast.show();
    }
    private void login(String telephone,String password){
        OkGo.<Result<User>>post(AppConst.User.login)
                .tag(this)
                .params("telephone", telephone)
                .params("password", password)
                .execute(new JsonCallBack<Result<User>>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSuccess(Response<Result<User>> response) {
                        Log.d("login", response.body().getMessage());
                        if (response.body().getCode() == ResultCode.SUCCESS.getCode()) {
                            successLoginToast();
                            User curUser = response.body().getData();
                            //??????????????????
                            SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_MULTI_PROCESS);
                            sp.edit()
                                    .putInt("userId",curUser.getUserId())
                                    .putString("userName",curUser.getUserName())
                                    .putString("telephone", curUser.getUserTelephone())
                                    .putString("password", curUser.getUserPassword())
                                    .apply();
                            //????????????
                            Explode explode = new Explode();
                            explode.setDuration(500);
                            getWindow().setExitTransition(explode);
                            getWindow().setEnterTransition(explode);
                            //????????????
                            ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
                            Intent i2 = new Intent(LoginActivity.this, BaseActivity.class);
                            startActivity(i2, oc2.toBundle());
                        }
                        else{
                            SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_MULTI_PROCESS);
                            sp.edit().clear().apply();
                            failedLoginToast();
                        }
                    }
                });
        //todo ?????????????????????????????????????????????????????? ????????????
        startActivity(new Intent(LoginActivity.this, BaseActivity.class));
    }
    /*
    ??????????????????????????????
    */
    private boolean verifyLoginTeleInfo(String telephone){
        if(TextUtils.isEmpty(telephone))
            return false;
        /*?????????????????????????????????????????????
         * ????????????: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
         * ????????????: 130,131,132,145,155,156,170,171,175,176,185,186
         * ????????????: 133,149,153,170,173,177,180,181,189
         */
        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";// "[1]"???????????????????????????????????????"[0-9]"???????????????0-9???????????????"[5,7,9]"???????????????5,7,9??????????????????,[^4]?????????4?????????????????????,\\d{9}"????????????????????????0???9???????????????9??????
        return telephone.matches(telRegex);
    }
    /*
    ????????????????????????
    */
    private boolean verifyLoginPasswdInfo(String passwd){
        if(TextUtils.isEmpty(passwd))
            return false;
        /*
        ????????????????????????6~18????????????????????????
         */
        String regex = "^[a-zA-Z0-9]{6,18}$";
        return passwd.matches(regex);
    }
}

