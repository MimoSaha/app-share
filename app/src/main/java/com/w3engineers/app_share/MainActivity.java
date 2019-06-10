package com.w3engineers.app_share;

import com.w3engineers.appshare.application.ui.InAppShareControl;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void startUI() {
        findViewById(R.id.open).setOnClickListener(v -> InAppShareControl.getInstance()
                .startInAppShareProcess(MainActivity.this, null));
    }
}
