package com.w3engineers.appshare.util.wifidirect;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-03-28 at 12:39 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-03-28 at 12:39 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-03-28 at 12:39 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class SoftAccessPointSearcher extends P2PServiceSearcher {

    public interface ServiceFound {
        void onServiceFoundSuccess(String ssid, String passPhrase, WifiP2pDevice wifiP2pDevice);
    }

    private ServiceFound mServiceFound;
    public boolean mIsConnecting;

    public void setServiceFound(ServiceFound serviceFound) {
        mServiceFound = serviceFound;
    }

    public SoftAccessPointSearcher(Context context) {
        super(context);
        mServiceType = Constants.Service.TYPE;
    }

    @Override
    protected void onDesiredServiceFound(String ssid, String passPhrase, WifiP2pDevice wifiP2pDevice) {
        if(mServiceFound != null && mIsAlive) {
            mIsConnecting = true;
            mServiceFound.onServiceFoundSuccess(ssid, passPhrase, wifiP2pDevice);
        }
    }

    @Override
    public boolean start() {
        mIsConnecting = false;
        return super.start();
    }

    @Override
    public void stop() {
        super.stop();
        mIsConnecting = false;
    }
}
