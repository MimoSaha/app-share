package com.w3engineers.appshare.util.wifidirect;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-03-28 at 1:36 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-03-28 at 1:36 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-03-28 at 1:36 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Collection;

/**
 * Will use this Manager class for all WiFi direct or WiFiP2P related tasks. Support Legacy Client
 * connectivity
 */
public class WiFiDirectManagerLegacy {

    private final int START_TASK_ONLY_AP = 1;
    private final int START_TASK_ONLY_SEARCH = 2;
    private final int START_TASK_ALL = 3;
    private final long SOFT_DELAY_TO_START_P2P_SERVICES = 1000;

    public interface P2PCommunicatior {
        void sendData(Peer peer, String data);
    }

    private WiFiStateMonitor mWiFiStateMonitor;
    private WiFiDirectConfig mWiFiDirectConfig;
    private WiFiDirectConnectionManager mWiFiDirectConnectionManager;
    private Context mContext;
    private WiFiConnectionHelper mWiFiConnectionHelper;
    private static WiFiDirectManagerLegacy sWiFiDirectManagerLegacy;
    private SoftAccessPoint mSoftAccessPoint;
    private SoftAccessPointSearcher mSoftAccessPointSearcher;
    private WiFiClient mWiFiClient;
    private MeshXLogListener mMeshXLogListener;
    private MeshXAPListener mMeshXAPListener;
    private MeshXLCListener mMeshXLCListener;
    private SoftAccessPoint.SoftAPStateListener mSoftAPStateListener = new SoftAccessPoint.SoftAPStateListener() {
        @Override
        public void onSoftAPChanged(boolean isEnabled, String SsidName, String password) {
            if (mMeshXAPListener != null) {
                mMeshXAPListener.onSoftAPStateChanged(isEnabled, SsidName, password);
            }
        }

        @Override
        public void onSoftApConnectedWithNodes(Collection<WifiP2pDevice> wifiP2pDevices) {
            if (mMeshXAPListener != null) {
                mMeshXAPListener.onGOConnectedWith(wifiP2pDevices);
            }

            if (mSoftAccessPointSearcher != null) {
                mSoftAccessPointSearcher.stop();
            }
        }

        @Override
        public void onSoftApDisConnectedWithNodes(String ip) {
            if (mMeshXAPListener != null) {

                mMeshXAPListener.onGODisconnectedWith(ip);

            }
        }
    };

    private SoftAccessPointSearcher.ServiceFound mServiceFound = new SoftAccessPointSearcher.ServiceFound() {
        @Override
        public void onServiceFoundSuccess(String ssid, String passPhrase, WifiP2pDevice p2pDevice) {
            if (mMeshXLogListener != null) {
                mMeshXLogListener.onLog("[FOUND] SSID - " + ssid + "::Passphrase - " + passPhrase);
            }

            if (mSoftAccessPoint != null) {
                mSoftAccessPoint.Stop();
                mSoftAccessPoint = null;
            }

            if (mSoftAccessPointSearcher != null) {
                mSoftAccessPointSearcher.stop();
                mSoftAccessPointSearcher = null;
            }

            mWiFiClient.connect(ssid, passPhrase);

            if (mMeshXLogListener != null) {

                StringBuilder devices = new StringBuilder();
                //Building device details log
                if (mSoftAccessPoint != null) {
                    Collection<WifiP2pDevice> wifiP2pDevices = mSoftAccessPoint.getConnectedPeers();
                    for (WifiP2pDevice wifiP2pDevice : wifiP2pDevices) {
                        devices.append(wifiP2pDevice.deviceName).append("-").
                                append(wifiP2pDevice.deviceAddress).append(",");
                    }
                    //removing last comma
                    int index = devices.lastIndexOf(",");
                    if (index != -1) {
                        devices.replace(index, index + 1, "");
                    }
                }

                mMeshXLogListener.onLog("[NOT-CONNECTING] " + (mSoftAccessPoint == null ?
                        "null" : ("count::" + mSoftAccessPoint.getConnectedPeersCount() + "::" + devices)));
            }
        }
    };

    private WiFiClient.ConneectionListener mConnectionListener = new WiFiClient.ConneectionListener() {

        /**
         * We are connected. Check whether we are connected with any GO. If not then we disconnect
         * and in turns re attemp for connection
         * @param wifiConnectionInfo
         */
        @Override
        public void onConnected(final WifiInfo wifiConnectionInfo) {

            if (P2PUtil.isConnectedWithPotentialGO(wifiConnectionInfo.getSSID())) {

                if (mMeshXLCListener != null) {
                    mMeshXLCListener.onConnectWithGO(wifiConnectionInfo.getSSID());
                }

                if (mSoftAccessPointSearcher != null) {
                    mSoftAccessPointSearcher.stop();
                }

            } else {
                //Disconnect from the network and restart required services
                if (mWiFiDirectConfig != null && mWiFiDirectConfig.mIsForceFulReconnectionAllowed &&
                        mWiFiClient.isConnected()) {
                    mWiFiClient.disConnect();
                }
            }

        }

        @Override
        public void onTimeOut() {
            if (mWiFiClient != null && !mWiFiClient.isConnected()) {

                if (mMeshXLogListener != null) {
                    mMeshXLogListener.onLog("[OnTimeOut]");
                }

                if (mWiFiDirectConfig != null && mWiFiDirectConfig.mIsClient) {
                    reAttemptServiceDiscovery();
                }
            }
        }

        /**
         * We are connected so enabling GO and Service searcher
         */
        @Override
        public void onDisconnected() {

            if (mMeshXLCListener != null) {
                mMeshXLCListener.onDisconnectWithGO();
            }

            if (mMeshXLogListener != null) {
                mMeshXLogListener.onLog("[onDisconnected]");
            }
            reAttemptServiceDiscovery();
        }

        /**
         * It considers client connection state of GO part. Make sure we are not re initiating GO
         * while it has any valid client connected
         */
        private void reAttemptServiceDiscovery() {
            if (mMeshXLogListener != null) {
                mMeshXLogListener.onLog("[reAttemptServiceDiscovery]");
            }
            MeshLog.v("[reAttemptServiceDiscovery]");

            AndroidUtil.postDelay(() -> {
                if (mSoftAccessPoint == null) {

                    start();

                } else {

                    if (mSoftAccessPoint.getConnectedPeersCount() < 1) {
                        start();
                    } else {
                        start(START_TASK_ONLY_SEARCH);
                    }
                }
            }, SOFT_DELAY_TO_START_P2P_SERVICES);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public synchronized static WiFiDirectManagerLegacy getInstance(Context context,
                                                                   MeshXAPListener meshXAPListener,
                                                                   MeshXLCListener meshXLCListener,
                                                                   WiFiDirectConfig wiFiDirectConfig) {
        if (sWiFiDirectManagerLegacy == null) {
            synchronized (WiFiDirectManagerLegacy.class) {
                if (sWiFiDirectManagerLegacy == null) {
                    sWiFiDirectManagerLegacy = new WiFiDirectManagerLegacy(context, meshXAPListener,
                            meshXLCListener, wiFiDirectConfig);
                }
            }
        }
        return sWiFiDirectManagerLegacy;
    }

    /**
     * You must ensure to call {@link #getInstance(Context, MeshXAPListener, MeshXLCListener, WiFiDirectConfig)} before this method. Otherwise it will
     * return null
     *
     * @return
     */
    public static WiFiDirectManagerLegacy getInstance() {
        return sWiFiDirectManagerLegacy;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public WiFiDirectManagerLegacy(Context context, MeshXAPListener meshXAPListener,
                                   MeshXLCListener meshXLCListener, WiFiDirectConfig wiFiDirectConfig) {

        mWiFiDirectConfig = wiFiDirectConfig;
        mContext = context;
        mWiFiConnectionHelper = new WiFiConnectionHelper(mContext);
        mWiFiDirectConnectionManager = new WiFiDirectConnectionManager(meshXAPListener, meshXLCListener);
        mMeshXAPListener = meshXAPListener;
        this.mMeshXLCListener = meshXLCListener;

        mWiFiClient = new WiFiClient(mContext);
        mWiFiClient.setConnectionListener(mConnectionListener);
        mWiFiClient.setMeshXLogListener(mMeshXLogListener);
    }

    public void start() {

        start(null);

    }

    public void start(Context context) {
        if (mContext == null && context != null) {
            mContext = context;
        }

        /*HardwareStateManager hardwareStateManager = new HardwareStateManager();
        hardwareStateManager.init(mContext);
        hardwareStateManager.resetWifi(isEnable -> {
            if (isEnable) {

                if (mWiFiStateMonitor == null) {
                    mWiFiStateMonitor = new WiFiStateMonitor(mContext, isEnabled -> {

                        if (!isEnabled) {//Somehow WiFi turned off
                            mWiFiStateMonitor.destroy();//Would start receive upon reenable
                            mWiFiStateMonitor = null;
                            start(mContext);//reenabling WiFi
                        }
                    });
                    mWiFiStateMonitor.init();
                }

                mWiFiConnectionHelper.disconnect();
                mWiFiConnectionHelper.disableAllConfiguredWiFiNetworks();

                int task = -1;
                if (mWiFiDirectConfig.mIsClient && mWiFiDirectConfig.mIsGroupOwner) {
                    task = START_TASK_ALL;

                } else if (mWiFiDirectConfig.mIsGroupOwner) {
                    task = START_TASK_ONLY_AP;

                } else if (mWiFiDirectConfig.mIsClient) {
                    task = START_TASK_ONLY_SEARCH;
                }

                start(task);
            }
        });*/

        int task = -1;
        if (mWiFiDirectConfig.mIsClient && mWiFiDirectConfig.mIsGroupOwner) {
            task = START_TASK_ALL;

        } else if (mWiFiDirectConfig.mIsGroupOwner) {
            task = START_TASK_ONLY_AP;

        } else if (mWiFiDirectConfig.mIsClient) {
            task = START_TASK_ONLY_SEARCH;
        }

        start(task);
    }

    public void start(int startTask) {

        if (startTask == START_TASK_ONLY_AP || startTask == START_TASK_ALL) {
            if (mWiFiDirectConfig.mIsGroupOwner) {
                if (mSoftAccessPoint == null) {
                    mSoftAccessPoint = new SoftAccessPoint(mContext, mSoftAPStateListener);
                    mSoftAccessPoint.setMeshXLogListener(mMeshXLogListener);

                    mSoftAccessPoint.start();
                } else {
                    mSoftAccessPoint.restart();
                }
            }

            if (startTask == START_TASK_ONLY_AP) {
                if (mSoftAccessPointSearcher == null) {
                    mSoftAccessPointSearcher = new SoftAccessPointSearcher(mContext);
                }

                mSoftAccessPointSearcher.stop();
            }
        }

        if (startTask == START_TASK_ONLY_SEARCH || startTask == START_TASK_ALL) {
            if (mWiFiDirectConfig.mIsClient) {
                if (mSoftAccessPointSearcher == null) {
                    mSoftAccessPointSearcher = new SoftAccessPointSearcher(mContext);
                    mSoftAccessPointSearcher.setServiceFound(mServiceFound);

                    mSoftAccessPointSearcher.start();
                } else {
                    mSoftAccessPointSearcher.restart();
                }
            }

            if (startTask == START_TASK_ONLY_SEARCH) {
                if (mSoftAccessPoint == null) {
                    mSoftAccessPoint = new SoftAccessPoint(mContext, mSoftAPStateListener);
                }

                mSoftAccessPoint.Stop();
            }
        }
    }

    /**
     * Re broadcast service
     */
    // TODO: 7/30/2019 check by not removing, only broadcasting
    public boolean reBroadCastService() {
        if (mSoftAccessPoint != null && mSoftAccessPoint.mIsAlive) {
            mSoftAccessPoint.stopLocalServices(isRemoved -> {
                if (isRemoved && mSoftAccessPoint != null) {
                    mSoftAccessPoint.startLocalService();
                }
                MeshLog.i(" [MeshX]Rebroadcasting:isRemoved-" + isRemoved);
            });

            return true;
        }

        return false;
    }

    public boolean isAlive() {
        return (mSoftAccessPoint != null && mSoftAccessPoint.mIsAlive) ||
                (mSoftAccessPointSearcher != null && mSoftAccessPointSearcher.mIsAlive);
    }

    public boolean isMeMaster() {
        return (mSoftAccessPoint != null && mSoftAccessPoint.mIsAlive);
    }

    public boolean isConnecting() {
        return mSoftAccessPointSearcher != null && mSoftAccessPointSearcher.mIsConnecting;
    }

    public void destroy() {
        sWiFiDirectManagerLegacy = null;

        //FIXME: context null issue
        // mContext = null;

        //todo
        if (mMeshXAPListener != null) {
            mMeshXAPListener.onSoftAPStateChanged(false, null, null);
        }

        if (mMeshXLCListener != null) {
            if (mWiFiClient != null && mWiFiClient.isConnected()) {
                mMeshXLCListener.onDisconnectWithGO();
            }
        }

        if (mSoftAccessPoint != null) {
            mSoftAccessPoint.Stop();
        }

        if (mSoftAccessPointSearcher != null) {
            mSoftAccessPointSearcher.stop();
        }

        if (mWiFiClient != null) {
            mWiFiClient.destroy();
        }

        if (mWiFiStateMonitor != null) {
            mWiFiStateMonitor.destroy();
        }
    }

    public void setMeshXLogListener(MeshXLogListener meshXLogListener) {
        mMeshXLogListener = meshXLogListener;
    }

    public void toggleGO(boolean newState) {

        if (newState != mWiFiDirectConfig.mIsGroupOwner) {
            mWiFiDirectConfig.mIsGroupOwner = newState;
            if (newState) {
                start(START_TASK_ONLY_AP);
            } else {
                mSoftAccessPoint.Stop();
            }
        }
    }

    public void toggleLC(boolean newState) {

        if (newState != mWiFiDirectConfig.mIsClient) {
            mWiFiDirectConfig.mIsClient = newState;
            if (newState) {
                start(START_TASK_ONLY_SEARCH);
            } else {
                if (mSoftAccessPointSearcher != null) {
                    mSoftAccessPointSearcher.stop();
                }
            }
        }
    }
}
