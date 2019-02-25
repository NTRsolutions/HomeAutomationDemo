package com.example.brijesh.homeautomationdemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.brijesh.homeautomationdemo.databinding.ActivitySplashScreenBinding;

public class SplashScreenActivity extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash_screen);

        binding.ivSplashLogo.animate().alpha(1.0f).setDuration(1500).start();

        mHandler = new Handler();
        mRunnable = () -> openDashboard();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.postDelayed(mRunnable, 3000);
    }

    private void openDashboard() {
        if (HomeApplianceApp.getInstance().isNetworkAvailable()){
            startActivity(new Intent(this,SubUnsubActivity.class));
            finish();
        }else{
            Toast.makeText(SplashScreenActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }

    }
}
