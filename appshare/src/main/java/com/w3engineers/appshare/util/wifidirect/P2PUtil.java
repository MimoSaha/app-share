package com.w3engineers.appshare.util.wifidirect;

import android.net.wifi.p2p.WifiP2pDevice;
import android.text.TextUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Enumeration;


/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-07-16 at 11:14 AM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: meshsdk.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-07-16 at 11:14 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-07-16 at 11:14 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class P2PUtil {

    private static final String GO_PREFIX = "DIRECT-";

    public static boolean hasItem(Collection<WifiP2pDevice> wifiP2pDevices) {
        return wifiP2pDevices != null && wifiP2pDevices.size() > 0;
    }


    public static WiFiDevicesList getList(Collection<WifiP2pDevice> wifiP2pDevices) {
        if (hasItem(wifiP2pDevices)) {
            WiFiDevicesList p2pDevices = new WiFiDevicesList();
            p2pDevices.addAll(wifiP2pDevices);
            return p2pDevices;
        }

        return null;
    }


    /**
     * Is the connected SSID is WiFi direct or P2P SSID
     * @param connectedSSID
     * @return
     */
    public static boolean isConnectedWithPotentialGO(String connectedSSID) {
        return !TextUtils.isEmpty(connectedSSID) && connectedSSID.replaceAll("\"", "").
                startsWith(GO_PREFIX);
    }

}
