package com.namakoti.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.namakoti.R;
import com.namakoti.base.BaseNavigationActivity;
import com.namakoti.beans.UserInfoBean;

/**
 * Created by anusha on 12/5/2017.
 */

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                long userId = UserInfoBean.getInstance().getUserId(SplashActivity.this);
                if(userId != 0){
                    UserInfoBean.getInstance().setUserId(userId, SplashActivity.this);
                    Intent intent = new Intent(SplashActivity.this, BaseNavigationActivity.class);//HomeActivity
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        }, 3000);
    }
}
