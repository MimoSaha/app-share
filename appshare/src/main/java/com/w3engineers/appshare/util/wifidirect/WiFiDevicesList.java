package com.w3engineers.appshare.util.wifidirect;

import android.net.wifi.p2p.WifiP2pDevice;
import android.text.TextUtils;


import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-07-23 at 11:34 AM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: meshsdk.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-07-23 at 11:34 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-07-23 at 11:34 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class WiFiDevicesList extends ConcurrentLinkedQueue<WifiP2pDevice> {

    private static Object lock = new Object();

    public WiFiDevicesList() {
        super();
    }

    public WiFiDevicesList(Collection<? extends WifiP2pDevice> c) {
        super(c);
    }

    @Override
    public boolean addAll(Collection<? extends WifiP2pDevice> wifiP2pDevices) {

        boolean isAdded = false;
        for( WifiP2pDevice wifiP2pDevice : wifiP2pDevices) {
            if(contains(wifiP2pDevice)) {
               continue;
            }

            isAdded = add(wifiP2pDevice);
        }

        return isAdded;
    }

    public synchronized boolean substract(WiFiDevicesList wifiP2pDevices) {
        if(!P2PUtil.hasItem(wifiP2pDevices))
            return false;

        synchronized (lock) {
        for(WifiP2pDevice wifiP2pDevice : this) {
            if(wifiP2pDevices.contains(wifiP2pDevice)) {
                    remove(wifiP2pDevice);
                }
            }
        }

        return true;
    }

    @Override
    public boolean contains(Object o) {
        if(o == null)
            return false;

        WifiP2pDevice wifiP2pDevice = (WifiP2pDevice) o;
        String address = wifiP2pDevice.deviceAddress;
        if(TextUtils.isEmpty(address)) {
            return false;
        }

        for(WifiP2pDevice wifiP2pDevice1 : this) {
            if(address.equals(wifiP2pDevice1.deviceAddress)) {
                return true;
            }
        }

        return false;
    }

    public WiFiDevicesList copy() {
        return new WiFiDevicesList(this);
    }
}
