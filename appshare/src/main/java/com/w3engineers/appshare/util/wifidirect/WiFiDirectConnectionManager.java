package com.w3engineers.appshare.util.wifidirect;

import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-04-10 at 12:32 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-04-10 at 12:32 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-04-10 at 12:32 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class WiFiDirectConnectionManager implements WiFiDirectManagerLegacy.P2PCommunicatior {

    public static final int WIFI_P2P_PORT = 3214;
    private ServerPeers mServerPeers;
    private MeshXAPListener mMeshXAPListener;
    private MeshXLCListener mMeshXLCListener;
    private ClientPeers mClientPeers;
    private Map<String, Peer> mIpPeerMap;

    public WiFiDirectConnectionManager(MeshXAPListener meshXAPListener, MeshXLCListener meshXLCListener) {

        mIpPeerMap = new ConcurrentHashMap<>();
        this.mMeshXAPListener = meshXAPListener;
        this.mMeshXLCListener = meshXLCListener;
        mServerPeers = new ServerPeers(null, WIFI_P2P_PORT);
        mServerPeers.setDaemon(true);
        mServerPeers.start();

    }

    @Override
    public void sendData(Peer peer, String data) {
        if(peer != null && !TextUtils.isEmpty(data) && peer.isPeerAlive()) {
            peer.write(data);
        }
    }

    public void sendData(String ip, String data) {

        sendData(mIpPeerMap.get(ip), data);
    }

}
