package com.w3engineers.appshare.util.wifidirect;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.annotation.IntRange;

import java.util.Collection;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-03-18 at 3:04 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-03-18 at 3:04 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-03-18 at 3:04 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public interface P2PStateListener {

    void onP2PStateChange(@IntRange(from = 1, to = 2) int state);
    void onP2PPeersStateChange();
    void onP2PConnectionChanged();
    void onP2PDisconnected();
    default void onP2PConnectionChanged(Collection<WifiP2pDevice> wifiP2pDevices) {}
    void onP2PPeersDiscoveryStarted();
    void onP2PPeersDiscoveryStopped();

}
