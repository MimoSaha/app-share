package com.w3engineers.appshare.util.wifidirect;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-04-10 at 12:36 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-04-10 at 12:36 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-04-10 at 12:36 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public interface PeerListener {

    void onPeerConnect(Peer peer);

    // TODO: 4/10/2019 heartbeat requires to decide final disconnection from Mesh layer
    void onPeerDisconnect(Peer peer);
    void onData(Peer peer, String data);

}
