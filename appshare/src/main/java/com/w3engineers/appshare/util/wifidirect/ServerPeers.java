package com.w3engineers.appshare.util.wifidirect;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-04-10 at 11:51 AM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-04-10 at 11:51 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-04-10 at 11:51 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/

/**
 * Form Server links to initiate new connections
 */
public class ServerPeers extends Thread {

    private PeerListener mPeerListener;
    private int mPort;
    private ConcurrentHashMap<String, Peer> mLinksConcurrentHashMap;

    public ServerPeers(PeerListener peerListener, int port) {
        mPeerListener = peerListener;
        mPort = port;
        mLinksConcurrentHashMap = new ConcurrentHashMap<>();
    }

    @Override
    public void run() {
        super.run();
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(mPort);

            while (true) {
                Socket socket = serverSocket.accept();

                Peer peer = new Peer(socket, mPeerListener);
                peer.setDaemon(true);
                peer.start();

                mLinksConcurrentHashMap.put(socket.getInetAddress().getHostAddress(), peer);
                if(mPeerListener != null) {
                    mPeerListener.onPeerConnect(peer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
