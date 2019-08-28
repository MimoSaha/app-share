package com.w3engineers.appshare.util.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.w3engineers.appshare.application.ui.InAppShareActivity;
import com.w3engineers.appshare.application.ui.InAppShareControl;
import com.w3engineers.appshare.util.wifidirect.MeshXAPListener;
import com.w3engineers.appshare.util.wifidirect.WiFiDirectConfig;
import com.w3engineers.appshare.util.wifidirect.WiFiDirectManagerLegacy;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Random;

/*
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

public class NetworkConfigureUtil implements MeshXAPListener {

    @NonNull
    public String SSID_Key = "m3s4rn9t3st";
    @NonNull
    public String SSID_IP = "192.168.43.20";
    @NonNull
    public String SSID_Name = "";
    private WifiManager wifiManager;
    private boolean isRmOff = false;

    private Context context;
    private NetworkCallback networkCallback;

    @SuppressLint("StaticFieldLeak")
    private static NetworkConfigureUtil networkConfigureUtil = new NetworkConfigureUtil();

    private WiFiDirectManagerLegacy wiFiDirectManagerLegacy;

    private NetworkConfigureUtil() {
        context = InAppShareControl.getInstance().getAppShareContext();
        wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @NonNull
    public static NetworkConfigureUtil getInstance() {
        return networkConfigureUtil;
    }


    @Override
    public void onSoftAPStateChanged(boolean isEnabled, String Ssid, String password) {
        Log.d("WifiDirectTest", "ssid: " + Ssid + " Password: " + password);

        if (isEnabled) {
            SSID_Name = Ssid;
            SSID_Key = password;

            triggerNetworkCall();
        }
    }

    @Override
    public void onGOConnectedWith(Collection<WifiP2pDevice> wifiP2pDevices) {

    }

    @Override
    public void onGODisconnectedWith(String ip) {

    }

    public void offWifiDirect() {
        wiFiDirectManagerLegacy.destroy();
    }

    public interface NetworkCallback {
        void networkName();
    }

    /**
     * Using a callback, when network is prepared to share app
     *
     * @param networkCallback - get instance from implemented class
     * @return - this class for using cyclic api
     */
    @NonNull
    public NetworkConfigureUtil setNetworkCallback(@NonNull NetworkCallback networkCallback) {
        this.networkCallback = networkCallback;
        return this;
    }

    /**
     * Concern of this method is establish a network using hotspot or wifi.
     * If device have a established mesh network then use this network system
     * otherwise prepare a hotspot manually
     *
     * @return - Using a boolean for accessing in disposable
     */
    public boolean startRouterConfigureProcess() {

        try {
            /*if (isApOn()) {
                Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);

                SSID_Name = wifiConfig.SSID;
                triggerNetworkCall();

            } else {*/
            InAppShareControl.AppShareCallback appShareCallback = InAppShareControl.getInstance().getAppShareCallback();
            if (appShareCallback != null) {
                appShareCallback.closeRmService();
            }
//                RmDataHelper.getInstance().stopRmService();
//
            isRmOff = true;
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
                //hotspotConfigure();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //turnOnHotspot();
            }

            startWifiDirect();

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                setRandomIp();
            }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void setRandomIp() {
        Random ran = new Random();
        int x = ran.nextInt(253) + 2;
        SSID_IP = "192.168.43." + String.valueOf(x);
    }

    /**
     * Preparing a manual hotspot
     * when device node is not in a mesh network
     */
    private void hotspotConfigure() {
        wifiManager.setWifiEnabled(false);
        WifiConfiguration wifiConfiguration = new WifiConfiguration();

        String networkNamePrefix = "Mesh-";
        SSID_Name = networkNamePrefix + "AppShare";

        wifiConfiguration.SSID = SSID_Name;

        wifiConfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);


        try {
            Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            setWifiApMethod.invoke(wifiManager, wifiConfiguration, true);

            Method stateMethod = wifiManager.getClass().getMethod("getWifiApState");
            stateMethod.setAccessible(true);

            boolean startPoint = true;
            int AP_STATE_ENABLED = 13;

            while (startPoint) {
                if ((Integer) stateMethod.invoke(wifiManager, (Object[]) null) == AP_STATE_ENABLED) {
                    startPoint = false;
                    triggerNetworkCall();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check device hotspot is enable or not
     *
     * @return - wifi ap state
     */
    private boolean isApOn() {
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * The purpose of this method is triggering network callback
     */
    private void triggerNetworkCall() {
        if (networkCallback != null) {
            networkCallback.networkName();
        }
    }

    /**
     * Using a boolean toggle for checking RM
     * is enable or disable
     *
     * @return - the RM enable state
     */
    public boolean isRmOff() {
        return isRmOff;
    }

    /**
     * Set RM off state false
     * When you restart the RM after completing the app sharing process
     *
     * @param rmOff - set RM current state
     */
    public void setRmOff(boolean rmOff) {
        isRmOff = rmOff;
    }

    @NonNull
    public String getNetworkPass() {
        return SSID_Key;
    }

    public String getNetworkIp() {
        return SSID_IP;
    }

    @NonNull
    public String getNetworkName() {
        return SSID_Name;
    }


    /**
     * Reset all properties when in app share process is completed
     */
    public void resetNetworkConfigureProperties() {
        SSID_Name = "";
    }

    private String TAG = "GAMIRUDDIN";
    private WifiManager.LocalOnlyHotspotReservation mReservation;

    @RequiresApi(api = Build.VERSION_CODES.O)
    void turnOnHotspot() {
        if (!InAppShareActivity.permissionForOreo) {
            Log.d(TAG, "Location service is off. Turn on location");
            return;
        }
        WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (manager != null) {
            manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {

                @Override
                public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                    super.onStarted(reservation);
                    Log.d(TAG, "Wifi Hotspot is on now");
                    mReservation = reservation;

                    mReservation.getWifiConfiguration();
                    SSID_Key = mReservation.getWifiConfiguration().preSharedKey;
                    SSID_Name = mReservation.getWifiConfiguration().SSID;
                    Log.d(TAG, "onStarted: key :" + SSID_Key + " ussid :" + SSID_Name);
                    triggerNetworkCall();
                }

                @Override
                public void onStopped() {
                    //super.onStopped();
                    Log.d(TAG, "onStopped: ");
                }

                @Override
                public void onFailed(int reason) {
                    //super.onFailed(reason);
                    Log.d(TAG, "onFailed: ");
                }
            }, null);
        }
    }


    private void startWifiDirect() {
        WiFiDirectConfig wiFiDirectConfig = new WiFiDirectConfig();
        wiFiDirectConfig.mIsGroupOwner = true;
        wiFiDirectManagerLegacy = WiFiDirectManagerLegacy.getInstance(context, this, null,
                wiFiDirectConfig);
        wiFiDirectManagerLegacy.start();
    }

}
