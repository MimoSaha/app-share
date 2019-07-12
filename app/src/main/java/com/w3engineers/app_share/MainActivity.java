package com.w3engineers.app_share;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.w3engineers.appshare.application.ui.InAppShareControl;

public class MainActivity extends AppCompatActivity implements InAppShareControl.AppShareCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.open).setOnClickListener(v -> InAppShareControl.getInstance()
                .startInAppShareProcess(MainActivity.this, this));
    }

    @Override
    public void closeRmService() {
        Log.v("MIMO_SAHA::", "closeRmService");
    }

    @Override
    public void successShared() {
        Log.v("MIMO_SAHA::", "successShared");
    }

    @Override
    public void closeInAppShare() {
        Log.v("MIMO_SAHA::", "closeInAppShare");
    }
}
