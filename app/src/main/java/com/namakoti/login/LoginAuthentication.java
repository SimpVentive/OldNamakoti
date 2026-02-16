package com.namakoti.login;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.namakoti.R;

public class LoginAuthentication extends AppCompatActivity {
    TextView textView_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_authentication);

    }
}
