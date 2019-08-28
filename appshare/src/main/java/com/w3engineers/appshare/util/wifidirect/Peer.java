package com.w3engineers.appshare.util.wifidirect;

import android.text.TextUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-04-09 at 5:02 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-04-09 at 5:02 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-04-09 at 5:02 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class Peer extends Thread {

    public static final int SOCKET_TIMEOUT = 5 * 1000;
    private Socket mSocket;
    private ObjectOutputStream mObjectOutputStream;
    private ObjectInputStream mObjectInputStream;
    private boolean mIsAlive;
    private PeerListener mPeerListener;
    public String mId;


    @Override
    public String toString() {
        String st = TextUtils.isEmpty(mId) ? "" : "ID:"+mId + "-";
        if(mSocket != null) {
            st += "IP:" + mSocket.getInetAddress().getHostAddress();
        }
        return st;
    }

    public Peer(Socket socket, PeerListener peerListener) {
        mSocket = socket;
        mPeerListener = peerListener;
    }


    @Override
    public void run() {
        try {

            mObjectOutputStream = new ObjectOutputStream(mSocket.getOutputStream());
            mObjectInputStream = new ObjectInputStream(mSocket.getInputStream());
            String receivedData;

            while (true) {
                try {
                    mIsAlive = true;
                    // Read from the InputStream
                    receivedData = mObjectInputStream.readUTF();
                    if (receivedData == null) {
                        break;
                    }

                    if(mPeerListener != null) {
                        mPeerListener.onData(Peer.this, receivedData);
                    }

                } catch (EOFException eoe) {
                    eoe.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mIsAlive = false;
            try {
                if(mObjectOutputStream != null) {
                    mObjectInputStream.close();
                }
                if(mPeerListener != null) {
                    mPeerListener.onPeerDisconnect(this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send data to specified stream
     * @param data
     */
    public boolean write(String data) {
        try {
            mObjectOutputStream.writeUTF(data);
            mObjectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean isPeerAlive() {
        return mIsAlive;
    }

    public String getIp() {
        return mSocket == null ? null : mSocket.getInetAddress().getHostAddress();
    }
}
