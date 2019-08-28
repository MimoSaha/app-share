package com.w3engineers.appshare.util.wifidirect;

import android.util.Patterns;


import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-07-24 at 1:53 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: meshsdk.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-07-24 at 1:53 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-07-24 at 1:53 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/

/**
 * Generic class to check whether given IP has the app alive or not. Respond using callback
 * {@link PingListener}
 */
public class Pinger implements Runnable {

    private static final int SOCKET_CONNECTION_TIMEOUT = 8000;

    public interface PingListener {
        void onPingResponse(String ip, boolean isReachable);
    }

    private String mIp;
    private PingListener mPingListener;

    public Pinger(String toIp, PingListener pingListener) {

        this.mIp = toIp;
        this.mPingListener = pingListener;
    }

    @Override
    public void run() {

        if (Patterns.IP_ADDRESS.matcher(mIp).matches()) {

            boolean isSuccess = false;

            try {
                //Random data to ping. One byte only
                byte[] pingData = {69};

                Socket socket = new Socket();

                InetAddress address = InetAddress.getByName(mIp);
                SocketAddress socketAddress = new InetSocketAddress(address, 5500);
                socket.connect(socketAddress, SOCKET_CONNECTION_TIMEOUT);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.write(pingData);
                dos.flush();
                dos.close();
                socket.close();
                isSuccess = true;
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (mPingListener != null) {
                mPingListener.onPingResponse(mIp, isSuccess);
            }
        }

    }
}
