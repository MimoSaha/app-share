package com.w3engineers.appshare.util.wifidirect;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;


/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-03-28 at 12:57 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-03-28 at 12:57 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-03-28 at 12:57 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/

/**
 * Maintain WiFi client related scenarios
 */
public class WiFiClient implements WiFiClientState {

    private final long WIFI_CONNECTION_TIMEOUT = 30 * 1000L;
    public interface ConneectionListener {
        void onConnected(WifiInfo wifiConnectionInfo);
        void onTimeOut();
        void onDisconnected();
    }

    private WiFiConnectionHelper mWiFiHelper;
    private WiFiClientStateReceiver mWiFiClientStateReceiver;
    private volatile boolean mIsConnected;

    private ConneectionListener mConnectionListener;
    private Runnable mTimeOutTask = new Runnable() {
        @Override
        public void run() {
            if(!mIsConnected) {
                mConnectionListener.onTimeOut();
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WiFiClient(Context context) {
        mWiFiClientStateReceiver = new WiFiClientStateReceiver(context, this);

        mWiFiHelper = new WiFiConnectionHelper(context);
    }


    /**
     * Start connecting with provided ssid with given passphrase. This method works only
     * if it is not connected with any network.
     * @return
     * @param ssid
     * @param passPhrase
     */
    public boolean connect(String ssid, String passPhrase) {

        if(!mIsConnected) {
            if(mWiFiHelper.connect(ssid, passPhrase)) {
                AndroidUtil.postBackground(mTimeOutTask, WIFI_CONNECTION_TIMEOUT);
            }
        }

        return true;
    }

    /**
     * Disassociate from currently active network.
     * @return
     */
    public boolean disConnect() {

        if(mIsConnected) {

            mWiFiHelper.disconnect();

        }

        return true;
    }

    public void setConnectionListener(ConneectionListener conneectionListener) {
        mConnectionListener = conneectionListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void destroy() {
        if(mWiFiClientStateReceiver != null) {
            mWiFiClientStateReceiver.destroy();
        }
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    @Override
    public void onConnected() {

        AndroidUtil.removeBackground(mTimeOutTask);

        if(!mIsConnected) {
            mIsConnected = true;
            if (mConnectionListener != null) {
                mConnectionListener.onConnected(mWiFiHelper.getConnectionInfo());
            }

        }
    }

    @Override
    public void onDisconnected() {
        mIsConnected = false;
        if(mConnectionListener != null) {
            mConnectionListener.onDisconnected();
        }
    }
}
