package com.w3engineers.appshare.util.wifidirect;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.text.TextUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-03-28 at 11:13 AM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-03-28 at 11:13 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-03-28 at 11:13 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class SoftAccessPoint implements WifiP2pManager.ConnectionInfoListener, WifiP2pManager.ChannelListener {

    private final int SERVICE_BROADCASTING_INTERVAL = 10 * 1000;
    private final long DEVICE_DISCONNECTOR_DECIDER_DELAY = 10 * 1000;
    public static final String OWN_AP_LOG_PREFIX = "[OWN]";

    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;
    private Context mContext;
    private PeerReceiver mPeerReceiver;
    private String mNetworkName = "", mPassphrase = "", mInetAddress;
    private MeshXLogListener mMeshXLogListener;
    private WiFiDevicesList mLastWifiP2pDeviceList;
    private SoftAPStateListener mSoftAPStateListener;
    private String mMyBTName;
    public boolean mIsAlive;
    private String mServiceString;

    public interface ServiceStateListener {
        void onServiceRemoved(boolean isRemoved);
    }

    public interface SoftAPStateListener {
        /**
         * Soft AP triggered on or off
         *
         * @param isEnabled
         * @param password
         */
        void onSoftAPChanged(boolean isEnabled, String Ssid, String password);

        /**
         * Connetced with given nodes. This is called only for the first time it connects with few devices
         *
         * @param wifiP2pDevices
         */
        void onSoftApConnectedWithNodes(Collection<WifiP2pDevice> wifiP2pDevices);

        /**
         * Calls every time when GO became empty
         */
        void onSoftApDisConnectedWithNodes(String ip);
    }

    public SoftAccessPoint(Context context, SoftAPStateListener softAPStateListener) {

        //Read my BT name. Hardcoded string
        // mMyBTName = SharedPref.read(Constant.KEY_DEVICE_BLE_NAME);

        mLastWifiP2pDeviceList = new WiFiDevicesList();
        mSoftAPStateListener = softAPStateListener;
        mContext = context;

        mWifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);

        if (mWifiP2pManager != null) {
            mChannel = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), this);

            mPeerReceiver = new PeerReceiver(mP2PStateListener);
            IntentFilter filter = new IntentFilter();
            filter.addAction(WIFI_P2P_STATE_CHANGED_ACTION);
            filter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION);
            filter.addAction(WIFI_P2P_PEERS_CHANGED_ACTION);
            try {
                mContext.registerReceiver(mPeerReceiver, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void setMeshXLogListener(MeshXLogListener meshXLogListener) {
        mMeshXLogListener = meshXLogListener;
    }

    private WifiP2pManager.GroupInfoListener mGroupInfoListener = new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            try {

                int numm = 0;
                for (WifiP2pDevice peer : mLastWifiP2pDeviceList) {
                    numm++;
                    // MeshLog.v("Client " + numm + " : " + peer.deviceName + " " + peer.deviceAddress);
                }

                if (mNetworkName.equals(group.getNetworkName()) && mPassphrase.equals(group.getPassphrase())) {

                    // MeshLog.v("Already have local service for " + mNetworkName + " ," + mPassphrase);

                } else {

                    mNetworkName = group.getNetworkName();
                    mPassphrase = group.getPassphrase();
                    if (mMeshXLogListener != null) {
                        mMeshXLogListener.onLog(OWN_AP_LOG_PREFIX + " SSID - " + mNetworkName + "::Passphrase - " + mPassphrase);
                    }


//                    mMyBTName = "sample string to represent my BT name";
                    MeshLog.e("BT NAME ADDED -> " + mMyBTName);
                    mServiceString = group.getNetworkName() + ":" +
                            group.getPassphrase() + (TextUtils.isEmpty(mMyBTName) ? "" : ":" + mMyBTName);
                    startLocalService(mServiceString);
                    if (mSoftAPStateListener != null) {
                        mSoftAPStateListener.onSoftAPChanged(true, mNetworkName, mPassphrase);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private WifiP2pManager.GroupInfoListener mGroupInfoListenerToClearIfExist = new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
            {
                if (group != null) {

                    mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {

                            createSoftAp();
                        }

                        @Override
                        public void onFailure(int reason) {
                        }
                    });
                } else {
                    createSoftAp();
                }
            }
        }
    };

    public boolean start() {

        MeshLog.i(" Triggered P2P device search-sap-start");
        if (mWifiP2pManager == null) {
            return false;
        }

        mWifiP2pManager.requestGroupInfo(mChannel, mGroupInfoListenerToClearIfExist);

        mIsAlive = true;
        return true;
    }

    public void restart() {
        //Does remove GI and then start it
        start();
    }

    /**
     * Before calling make sure no group is present.
     */
    private void createSoftAp() {
        mWifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                MeshLog.i(" Creating Soft AP");
            }

            @Override
            public void onFailure(int reason) {
//                public static final int ERROR               = 0;
//                public static final int P2P_UNSUPPORTED     = 1;
//                public static final int BUSY                = 2;
                // MeshLog.v("Soft AP Failed. Reason " + reason);
            }
        });
    }

    // TODO: 8/21/2019
    // 1. Is there any way to check group existence?
    // 2. Does it fail if we try to remove if group does not exist?
    // - Jukka implemntation what?
    public void removeGroup() {
        mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                MeshLog.i(" Cleared Local Group ");
                if (mSoftAPStateListener != null) {
                    mSoftAPStateListener.onSoftAPChanged(false, null, mPassphrase);
                }
            }

            public void onFailure(int reason) {
                MeshLog.i(" Clearing Local Group failed, error code " + reason);
            }
        });
    }

    public void startLocalService() {
        if (!TextUtils.isEmpty(mServiceString)) {
            startLocalService(mServiceString);
        }
    }

    private Runnable mServiceBroadcastingRunnable = new Runnable() {
        @Override
        public void run() {
            mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onFailure(int error) {
                }
            });
            AndroidUtil
                    .postBackground(mServiceBroadcastingRunnable, SERVICE_BROADCASTING_INTERVAL);
        }
    };

    private void startLocalService(String instance) {

        Map<String, String> record = new HashMap<>();
        record.put("available", "visible");

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(instance,
                Constants.Service.TYPE, record);

        MeshLog.i(" Add local service" + instance);
        mWifiP2pManager.addLocalService(mChannel, service, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                MeshLog.i(" Added local service");
                AndroidUtil
                        .postBackground(mServiceBroadcastingRunnable, 1 * 1000);
            }

            public void onFailure(int reason) {
                //MeshLog.v("Adding local service failed, error code" + reason);
            }
        });
    }

    private void stopLocalServices() {

        stopLocalServices(null);
    }

    public void stopLocalServices(ServiceStateListener serviceStateListener) {
        mNetworkName = mPassphrase = null;

        mWifiP2pManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                MeshLog.i(" Cleared local services");
                if (serviceStateListener != null) {
                    serviceStateListener.onServiceRemoved(true);
                }
            }

            public void onFailure(int reason) {
                //MeshLog.v("Clearing local services failed, error code " + reason);
                if (serviceStateListener != null) {
                    serviceStateListener.onServiceRemoved(false);
                }
            }
        });
    }

    public void Stop() {
        mIsAlive = false;
        try {
            mContext.unregisterReceiver(mPeerReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        stopLocalServices();
        removeGroup();
        mLastWifiP2pDeviceList = null;
    }

    /**
     * @return how many peers are connected at this moment with this service???
     */
    public int getConnectedPeersCount() {
        return mLastWifiP2pDeviceList == null ? 0 : mLastWifiP2pDeviceList.size();
    }

    /**
     * @return how many peers are connected at this moment with this service???
     */
    public Collection<WifiP2pDevice> getConnectedPeers() {
        return mLastWifiP2pDeviceList;
    }

    private P2PStateListener mP2PStateListener = new P2PStateListener() {
        @Override
        public void onP2PStateChange(int state) {

        }

        @Override
        public void onP2PPeersStateChange() {

        }

        /**
         * group formed from above {@link SoftAccessPoint#createSoftAp()} method call
         */
        @Override
        public void onP2PConnectionChanged() {
            mWifiP2pManager.requestConnectionInfo(mChannel, SoftAccessPoint.this);
        }

        @Override
        public void onP2PConnectionChanged(Collection<WifiP2pDevice> wifiP2pDevices) {
            String st = P2PUtil.getLogString(wifiP2pDevices);

            WiFiDevicesList devices = P2PUtil.getList(wifiP2pDevices);
            if (P2PUtil.hasItem(devices)) {
                WiFiDevicesList addedList = devices.copy();
                addedList.substract(mLastWifiP2pDeviceList);
                //ensure new devices arrived
                if (P2PUtil.hasItem(devices)) {
                    //adding new unique devices
                    mLastWifiP2pDeviceList.addAll(addedList);
//                    if(mSoftAPStateListener != null && addedList.size() > 0) {
//                        mSoftAPStateListener.onSoftApConnectedWithNodes(addedList);
//                    }
                }


                if (P2PUtil.hasItem(mLastWifiP2pDeviceList)) {

                    WiFiDevicesList possibleDisconnectedList = mLastWifiP2pDeviceList.copy();
                    possibleDisconnectedList.substract(devices);

                    // TODO: 7/24/2019 optimize ping to stop pinging same device within certain time
                    //Received mac missing earlier entry so possible disconnection occurred
                    if (possibleDisconnectedList.size() > 0) {
                        MeshLog.v("[Meshx]Possible remove event for GO");
                        new DeviceDisconnector().startWatching();
                    }
                }
            } else if (P2PUtil.hasItem(mLastWifiP2pDeviceList)) {

                new DeviceDisconnector().startWatching();
            }
        }

        @Override
        public void onP2PDisconnected() {

        }

        @Override
        public void onP2PPeersDiscoveryStarted() {

        }

        @Override
        public void onP2PPeersDiscoveryStopped() {

        }
    };


    @Override
    public void onChannelDisconnected() {

    }

    /**
     * Connection info available on {@link WifiP2pManager#requestConnectionInfo(WifiP2pManager.Channel, WifiP2pManager.ConnectionInfoListener)}
     * call from {@link P2PStateListener#onP2PConnectionChanged()}
     *
     * @param info
     */
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {

        if (info.isGroupOwner) {

            mInetAddress = info.groupOwnerAddress.getHostAddress();
            mWifiP2pManager.requestGroupInfo(mChannel, mGroupInfoListener);
        }
    }

    private class DeviceDisconnector {

        private List<String> mIpList;
        private Executor mExecutor;
        private Pinger.PingListener mPingListener = (ip, isReachable) -> {

            if (!isReachable && !TextUtils.isEmpty(ip)) {
                mSoftAPStateListener.onSoftApDisConnectedWithNodes(ip);
            }
        };

        public DeviceDisconnector() {
        }

        void startWatching() {

           /* new Thread(() -> {
                mIpList = RouteManager.getInstance().getConnectedIpAddress(RoutingEntity.Type.WiFi);
                if(CollectionUtil.hasItem(mIpList)) {

                    mExecutor = Executors.newFixedThreadPool(mIpList.size());

                    MeshLog.v("[watching] pinging: %s", mIpList.toString());

                    for (String ip : mIpList) {

                        mExecutor.execute( new Pinger(ip, mPingListener));
                    }
                }
            }).start();*/
        }
    }
}
