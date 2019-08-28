package com.w3engineers.appshare.util.wifidirect;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.util.List;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-04-04 at 10:58 AM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-04-04 at 10:58 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-04-04 at 10:58 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/

/**
 * All native WiFi adapter related helper functions
 */
public class WiFiConnectionHelper {

    private WifiManager mWifiManager;

    public WiFiConnectionHelper(Context context) {
        mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public boolean disconnect() {
        if(mWifiManager != null) {
            return mWifiManager.disconnect();
        }
        return false;
    }

    /**
     * Disable all configured wifi network. We want to stop any automatic connection.
     */
    public void disableAllConfiguredWiFiNetworks() {
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();

        if(configuredNetworks != null) {

            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                if (wifiConfiguration != null && wifiConfiguration.networkId != -1) {
                    mWifiManager.disableNetwork(wifiConfiguration.networkId);
                }
            }
        }
    }

    /**
     * Disable all configured wifi network. We want to stop any automatic connection.
     */
    public int getConfiguredWiFiNetworkId(String SSID) {
        if(TextUtils.isEmpty(SSID)) {
            return -1;
        }
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();

        for(WifiConfiguration wifiConfiguration : configuredNetworks) {
            if(wifiConfiguration != null && wifiConfiguration.networkId != -1) {
                if(SSID.equals(wifiConfiguration.SSID)) {
                    return wifiConfiguration.networkId;
                }
            }
        }

        return -1;
    }

    public boolean connect(String ssid, String passPhrase) {

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", passPhrase);

        //// TODO: 7/17/2019
        //It automates SSID connection whenever available so it gives us huge performance benefit.
        //To leverage the benefit we need to have according support. Would add later
//        wifiConfig.hiddenSSID = true;

        int networkId = getConfiguredWiFiNetworkId(ssid);
        if(networkId != -1) {
            wifiConfig.networkId = networkId;

            networkId = mWifiManager.updateNetwork(wifiConfig);

            if(networkId == -1) {
                networkId = this.mWifiManager.addNetwork(wifiConfig);

            }
        } else {
            networkId = this.mWifiManager.addNetwork(wifiConfig);

        }
        mWifiManager.enableNetwork(networkId, true);
        return mWifiManager.reconnect();
    }

    public WifiInfo getConnectionInfo() {

        return mWifiManager.getConnectionInfo();
    }
}
