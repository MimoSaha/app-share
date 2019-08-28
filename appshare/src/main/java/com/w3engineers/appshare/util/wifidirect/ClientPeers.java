package com.w3engineers.appshare.util.wifidirect;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-04-10 at 11:41 AM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-04-10 at 11:41 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-04-10 at 11:41 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/

/**
 * Form Legacy WiFi Client link
 */
public class ClientPeers {

    private Peer mPeer;
    private PeerListener mPeerListener;
    private InetAddress mInetAddress;
    private int mPort;

    public ClientPeers(PeerListener peerListener, InetAddress inetAddress, int port) {
        mPeerListener = peerListener;
        mInetAddress = inetAddress;
        mPort = port;

        init();
    }

    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Socket clientSocket = new Socket(mInetAddress, mPort);

                    mPeer = new Peer(clientSocket, mPeerListener);
                    mPeer.start();

                    if (mPeerListener != null) {
                        mPeerListener.onPeerConnect(mPeer);
                    }

                    try {// TODO: 5/8/2019 deletethis delay?
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                   // String ip = AddressUtil.getLocalIpAddress();
                    //write("hello from - " + ip);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void write(String data) {
        if(mPeer != null && mPeer.isPeerAlive()) {
            mPeer.write(data);
        }
    }

    public Peer getPeer() {
        return mPeer;
    }
}
