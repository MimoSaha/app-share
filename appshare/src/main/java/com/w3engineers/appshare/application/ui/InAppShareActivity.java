package com.w3engineers.appshare.application.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.w3engineers.appshare.R;
import com.w3engineers.appshare.util.helper.InAppShareUtil;
import com.w3engineers.appshare.util.helper.NetworkConfigureUtil;
import com.w3engineers.ext.strom.application.ui.base.BaseActivity;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
public class InAppShareActivity extends BaseActivity {

    private InAppShareViewModel inAppShareViewModel;

//    private ActivityInAppShareBinding activityInAppShareBinding;
//    private ActivityAppShareBinding activityInAppShareBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_in_app_share;
    }

    @Override
    protected int getToolbarId() {
        return R.id.toolbar;
    }

    @Override
    protected int statusBarColor() {
        return R.color.colorPrimary;
    }

    private ProgressBar progressBar;
    private ScrollView scrollView;
    private TextView wifiId, wifiPass, wifiUrl;
    private ImageView qrCode;

    @Override
    protected void startUI() {

//        activityInAppShareBinding = (ActivityAppShareBinding) getViewDataBinding();

        setTitle(getString(R.string.settings_share_app));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initUI();

        disableState();

        inAppShareViewModel = getViewModel();

        boolean permission = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(this);
        }

        if (!permission) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 119);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_SETTINGS},101);
            }
        } else {
            appShareStart();
        }
    }

    private void appShareStart() {
        inAppShareViewModel.startInAppShareProcess();
        uiOperationServerAddress();
        inAppShareViewModel.checkInAppShareState();
    }

    private void initUI() {
        progressBar = findViewById(R.id.appShare_progress);
        scrollView = findViewById(R.id.scroll_view);
        wifiId = findViewById(R.id.share_wifi_id);
        wifiPass = findViewById(R.id.share_wifi_id_pass);
        wifiUrl = findViewById(R.id.text_view_url);
        qrCode = findViewById(R.id.image_view_qr_code);
    }

    private void uiOperationServerAddress() {

        inAppShareViewModel.appShareStateLiveData.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean isInAppShareEnable) {
                if (isInAppShareEnable) {

                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);

                    // Expose all server side info
                    wifiId.setText("\"" + NetworkConfigureUtil.getInstance().getNetworkName() + "\"");
                    wifiPass.setText(getPasswordText());
                    wifiUrl.setText(InAppShareUtil.getInstance().serverAddress);
                    qrCode.setImageBitmap(InAppShareUtil.getInstance().serverAddressBitmap);

                } else {
                    disableState();
                }
            }
        });
    }

    private SpannableString getPasswordText() {
        String pass = NetworkConfigureUtil.getInstance().getNetworkPass();
        String passText = String.format(getResources().getString(R.string.using_password), pass);
        SpannableString spannableString = new SpannableString(passText);

        int startIndex = passText.length() - pass.length();

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGradientPrimary)),
                startIndex, passText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 119) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.System.canWrite(this)) {
                appShareStart();
                return;
            }
        }
        finish();
    }

    @Override
    protected void stopUI() {
        super.stopUI();
        // Stop In app share server
        inAppShareViewModel.stopServerProcess();
        inAppShareViewModel.resetAllInfo();
    }

    private void disableState() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Reset and restart RM service if RM is stopped
        inAppShareViewModel.resetRM();
    }

    private InAppShareViewModel getViewModel() {
        return ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new InAppShareViewModel(getApplication());
            }
        }).get(InAppShareViewModel.class);
    }
}
