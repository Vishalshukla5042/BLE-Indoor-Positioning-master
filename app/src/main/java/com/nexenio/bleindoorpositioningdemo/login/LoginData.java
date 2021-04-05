package com.nexenio.bleindoorpositioningdemo.login;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nexenio.bleindoorpositioningdemo.R;
import com.nexenio.bleindoorpositioningdemo.login.ui.main.LoginDataFragment;


public class LoginData extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_data_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, LoginDataFragment.newInstance())
                    .commitNow();
        }
    }
}