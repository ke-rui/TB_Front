package com.example.ecnutimebank.ui.login;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ecnutimebank.R;
import com.example.ecnutimebank.entity.User;
import com.example.ecnutimebank.helper.AppConst;
import com.example.ecnutimebank.helper.JsonCallBack;
import com.example.ecnutimebank.helper.Result;
import com.example.ecnutimebank.helper.ResultCode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.RequestBody;


public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private EditText rePassword;

    private FloatingActionButton fab;
    private CardView cvAdd;
    private Button nextButton;

    private TextInputLayout usernameInput, passwdInput, repeatpasswdInput;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ShowEnterAnimation();
        initView();
        setListener();
    }
    private void setListener() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRegister();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });

    }

    private void failedRegisterToast(){
        Toast toast = Toast.makeText(this,"????????????,??????????????????????????????",Toast.LENGTH_LONG);
        toast.show();
    }
    private void successRegisterToast(){
        Toast toast = Toast.makeText(this, "???????????????", Toast.LENGTH_LONG);
        toast.show();
    }
    private void RepasswdNotEqualToast(){
        Toast toast = Toast.makeText(this, "???????????????????????????????????????", Toast.LENGTH_LONG);
        toast.show();
    }
    private void userRegister() {
        //todo ??????????????????
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String repeatPassword = rePassword.getText().toString().trim();
        User newUser = new User();
        newUser.setUserName(username);
        boolean flag = true;

        if(!verifyRegisterUsernameInfo(username) || !verifyRegisterPasswdInfo(password)){
            flag = false;
            failedRegisterToast();
        }
        if(!password.equals(repeatPassword)){
            flag = false;
            RepasswdNotEqualToast();
        }
        if(flag){
            newUser.setUserPassword(password);
            HashMap params = new HashMap<>();
            params.put("userName",username);
            params.put("userTelephone",username);
            params.put("userPassword",password);
            JSONObject jsonObject = new JSONObject(params);
            OkGo.<Result<User>>post(AppConst.User.register)
                    .tag(this)
                    .upJson(jsonObject)
                    .execute(new JsonCallBack<Result<User>>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onSuccess(Response<Result<User>> response) {
                            Log.d("register", response.body().getMessage());
                            if (response.body().getCode() == ResultCode.SUCCESS.getCode()) {
                                successRegisterToast();
//                                Log.d("register", "????????????!");
                                animateRevealClose();
                            }
                            else{
                                failedRegisterToast();
//                                Log.d("register", "????????????!");
                            }
                        }
                    });
        }
    }

    private void initView() {
        fab = findViewById(R.id.fab);
        nextButton = findViewById(R.id.bt_register);
        cvAdd = findViewById(R.id.cv_add);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        rePassword = findViewById(R.id.et_repeatpassword);

        usernameInput = findViewById(R.id.username_input);
        passwdInput = findViewById(R.id.passwd_input);
        repeatpasswdInput = findViewById(R.id.repeatpasswd_input);
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

            @Override
            public void afterTextChanged(Editable s) {
                boolean flag = verifyRegisterUsernameInfo(s.toString());
                if(flag){
                    usernameInput.setErrorEnabled(false);
                    nextButton.setClickable(true);
                }else {
                    usernameInput.setError(getString(R.string.username_input_error));
                    nextButton.setClickable(false);
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
                boolean flag = verifyRegisterPasswdInfo(s.toString());
                if(flag){
                    passwdInput.setErrorEnabled(false);
                    nextButton.setClickable(true);
                }else{
                    passwdInput.setError(getString(R.string.passwd_input_error));
                    nextButton.setClickable(false);
                }
            }
        });

        /*
        ????????????????????????TextInputLayout??????
         */
        rePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean flag = verifyRegisterPasswdInfo(s.toString());
                if(flag){
                    repeatpasswdInput.setErrorEnabled(false);
                    nextButton.setClickable(true);
                }else {
                    repeatpasswdInput.setError(getString(R.string.passwd_input_error));
                    nextButton.setClickable(false);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    /*
        ?????????????????????????????????
    */
    private boolean verifyRegisterUsernameInfo(String telephone){
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
    ????????????????????????????????????????????????
    */
    private boolean verifyRegisterPasswdInfo(String passwd){
        if(TextUtils.isEmpty(passwd))
            return false;
        /*
        ????????????????????????6~18????????????????????????
         */
        String regex = "^[a-zA-Z0-9]{6,18}$";
        return passwd.matches(regex);
    }
}
