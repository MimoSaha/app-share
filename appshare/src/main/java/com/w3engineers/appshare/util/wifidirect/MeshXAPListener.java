package com.w3engineers.appshare.util.wifidirect;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-03-19 at 11:37 AM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-03-19 at 11:37 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-03-19 at 11:37 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/


import android.net.wifi.p2p.WifiP2pDevice;

import java.util.Collection;

/**
 * Contains method to communicate at App layer
 */
public interface MeshXAPListener {

    void onSoftAPStateChanged(boolean isEnabled, String Ssid, String password);

    void onGOConnectedWith(Collection<WifiP2pDevice> wifiP2pDevices);

    void onGODisconnectedWith(String ip);
}
