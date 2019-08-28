package com.w3engineers.appshare.application.ui;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.w3engineers.appshare.R;
import com.w3engineers.appshare.util.helper.InAppShareUtil;
import com.w3engineers.appshare.util.helper.NetworkConfigureUtil;

import pl.droidsonroids.gif.GifImageView;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
public class InAppShareActivity extends AppCompatActivity {

    private InAppShareViewModel inAppShareViewModel;
    private Toolbar toolbar;
    private GifImageView progressBar;
    private ScrollView scrollView;
    private TextView wifiId, wifiPass, wifiUrl;
    private ImageView qrCode;
    public static boolean permissionForOreo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_share);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        setStatusBarColor();

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ///isLocationPermissionEnable();
            //enableLocationSettings();
        }

        if (!permission) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                String packageName = getPackageName();
                intent.setData(Uri.parse("package:" + packageName));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 119);
            } else {
                appShareStart();
            }
        } else {
            appShareStart();
        }
    }

    private void setStatusBarColor() {

        int statusBarColor = statusBarColor();

        if (statusBarColor > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = this.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, statusBarColor));
            }
        }
    }

    protected int statusBarColor() {
        return R.color.colorPrimary;
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

                    wifiPass.setVisibility(View.VISIBLE);
                    wifiPass.setText(getPasswordText());

                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        wifiPass.setVisibility(View.VISIBLE);
                        wifiPass.setText(getPasswordText());
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                        wifiPass.setVisibility(View.VISIBLE);
                        wifiPass.setText(getIpText());
                    }*/

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

    private SpannableString getIpText() {
        String ip = NetworkConfigureUtil.getInstance().getNetworkIp();
        String ipText = String.format(getResources().getString(R.string.using_ip), ip);
        SpannableString spannableString = new SpannableString(ipText);

        int startIndex = ipText.length() - ip.length();

        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorGradientPrimary)),
                startIndex, ipText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            inAppShareViewModel.offWifidirect();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        inAppShareViewModel.stopServerProcess();
        inAppShareViewModel.resetAllInfo();
    }

    private void disableState() {
        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        inAppShareViewModel.offWifidirect();
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

    public boolean isLocationPermissionEnable() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            permissionForOreo = false;
            return false;
        }
        permissionForOreo = true;
        return true;
    }

    private final int REQUEST_ENABLE_LOCATION_SYSTEM_SETTINGS = 101;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enableLocationSettings() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setFastestInterval(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)
                .setAlwaysShow(false);

        Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build());

        task.addOnCompleteListener(task1 -> {
            try {
                LocationSettingsResponse response = task1.getResult(ApiException.class);
                permissionForOreo = true;
            } catch (ApiException exception) {
                switch (exception.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            ResolvableApiException resolvable = (ResolvableApiException) exception;
                            resolvable.startResolutionForResult(InAppShareActivity.this, REQUEST_ENABLE_LOCATION_SYSTEM_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
    }
}
