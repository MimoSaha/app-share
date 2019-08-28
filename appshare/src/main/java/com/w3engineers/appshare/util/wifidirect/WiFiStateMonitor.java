package com.w3engineers.appshare.util.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-08-19 at 5:00 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: meshsdk.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-08-19 at 5:00 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-08-19 at 5:00 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class WiFiStateMonitor {

    private int mLastState =  WifiManager.WIFI_STATE_UNKNOWN;
    private Context mContext;
    private BroadcastReceiver mWiFiStateMonitor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(mWiFiAdapterStateListener != null) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN);

                //Update iff state changes
                if(mLastState != wifiState) {

                    if (wifiState == WifiManager.WIFI_STATE_ENABLED ||
                            wifiState == WifiManager.WIFI_STATE_DISABLED) {

                        mWiFiAdapterStateListener.onStateChanged(
                                wifiState == WifiManager.WIFI_STATE_ENABLED);
                    }
                    mLastState = wifiState;
                }
            }
        }
    };

    public interface WiFiAdapterStateListener {
        /**
         * @param isEnabled true if WiFi turned on, otherwise false
         */
        void onStateChanged(boolean isEnabled);
    }

    private WiFiAdapterStateListener mWiFiAdapterStateListener;

    public WiFiStateMonitor(Context context, WiFiAdapterStateListener wiFiAdapterStateListener) {

        this.mContext = context;
        mWiFiAdapterStateListener = wiFiAdapterStateListener;
    }

    /**
     * Init BroadCast Receiver
     */
    public void init() {

        final IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mContext.registerReceiver(mWiFiStateMonitor, filter);
    }

    public void destroy() {

        if(mContext != null) {
            try {
                mContext.unregisterReceiver(mWiFiStateMonitor);

            } catch (IllegalArgumentException illegalArgumentException) {

                illegalArgumentException.printStackTrace();
            }
        }
    }
}
