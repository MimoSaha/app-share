package com.w3engineers.app_share;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.w3engineers.appshare.application.ui.InAppShareControl;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.open).setOnClickListener(v -> InAppShareControl.getInstance()
                .startInAppShareProcess(MainActivity.this, null));
    }
}
