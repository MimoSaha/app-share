package com.w3engineers.appshare.util.wifidirect;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Handler;

import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION;
import static android.net.wifi.p2p.WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-03-28 at 12:19 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-03-28 at 12:19 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-03-28 at 12:19 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/

/**
 * Discovers peers and provided service type. If found then call {@link #onDesiredServiceFound(String, String, WifiP2pDevice)}
 */
public abstract class P2PServiceSearcher implements WifiP2pManager.ChannelListener {

    private enum ServiceState {
        NONE,
        DiscoverPeer,
        DiscoverService
    }

    volatile boolean mIsAlive = true;
    private Context mContext;
    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;
    private PeerReceiver mPeerReceiver;
    private WifiP2pManager.PeerListListener mPeerListListener;
    private WifiP2pManager.DnsSdServiceResponseListener mDnsSdServiceResponseListener;
    private ServiceState mServiceState = ServiceState.NONE;
    /**
     * set type of service we are looking for
     */
    String mServiceType = Constants.Service.TYPE;

    public P2PServiceSearcher(Context context) {
        mContext = context;

        mWifiP2pManager = (WifiP2pManager) mContext.getSystemService(Context.WIFI_P2P_SERVICE);

        mChannel = mWifiP2pManager.initialize(mContext, mContext.getMainLooper(), this);

        mPeerReceiver = new PeerReceiver(mP2PStateListener);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filter.addAction(WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        filter.addAction(WIFI_P2P_PEERS_CHANGED_ACTION);
        mContext.registerReceiver(mPeerReceiver, filter);

        mPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {

                final WifiP2pDeviceList pers = peers;
                int numm = 0;
                for (WifiP2pDevice peer : pers.getDeviceList()) {
                    numm++;
                }

                if (numm > 0) {
                    startServiceDiscovery();
                } else {
                    startPeerDiscovery();
                }
            }
        };

        mDnsSdServiceResponseListener = new WifiP2pManager.DnsSdServiceResponseListener() {

            public void onDnsSdServiceAvailable(String instanceName, String serviceType, WifiP2pDevice device) {

                if (serviceType.startsWith(mServiceType)) {


                    String[] separated = instanceName.split(":");
                    if (separated.length > 1) {

                        final String networkSSID = separated[0];
                        final String networkPass = separated[1];
                        final String serviceAdvertisersBtName = separated.length > 2 ? separated[2] : null;

                        if (ConnectionLinkCache.getInstance().isBtNameExistInConnectedSet(serviceAdvertisersBtName)
                                || ConnectionLinkCache.getInstance().isSsidNameExistInConnectedSet(networkSSID)) {
                            MeshLog.i(" [MeshX]Service not accepting as peers is in same network. BT: "
                                    + serviceAdvertisersBtName + ":SSID:" + networkSSID);
                        } else {
                            onDesiredServiceFound(networkSSID, networkPass, device);
                        }
                    }

                } else {
                    MeshLog.i("  Not our Service, :" + Constants.Service.TYPE + "!=" + serviceType + ":");
                }

                startPeerDiscovery();
            }
        };
    }

    protected abstract void onDesiredServiceFound(String ssid, String passPhrase, WifiP2pDevice wifiP2pDevice);

    public boolean start() {

        if (mWifiP2pManager == null) {
            return false;
        }

        mWifiP2pManager.setDnsSdResponseListeners(mChannel, mDnsSdServiceResponseListener, null);
        mWifiP2pManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                startPeerDiscovery();
            }

            @Override
            public void onFailure(int reason) {
                MeshLog.i("  Failed reason -> "+reason);
            }
        });

        return true;
    }

    public void restart() {
        start();
    }

    private void startServiceDiscovery() {

        mWifiP2pManager.clearServiceRequests(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

                WifiP2pDnsSdServiceRequest request = WifiP2pDnsSdServiceRequest.newInstance(Constants.Service.TYPE);
                final Handler handler = new Handler();
                mWifiP2pManager.addServiceRequest(mChannel, request, new WifiP2pManager.ActionListener() {

                    public void onSuccess() {
                        handler.postDelayed(new Runnable() {
                            //There are supposedly a possible race-condition bug with the service discovery
                            // thus to avoid it, we are delaying the service discovery start here
                            public void run() {
                                mWifiP2pManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
                                    public void onSuccess() {
                                        MeshLog.i(" Started service discovery");
                                        mServiceState = ServiceState.DiscoverService;
                                    }

                                    public void onFailure(int reason) {
                                        MeshLog.e(" Starting service discovery failed, error code -> " + reason);
                                    }
                                });
                            }
                        }, Constants.Service.DISCOVERY_DELAY);
                    }

                    public void onFailure(int reason) {
                        MeshLog.e(" Adding service request failed, error code -> " + reason);
                        // No point starting service discovery
                    }
                });
            }

            @Override
            public void onFailure(int reason) {
                MeshLog.i(" Failed reason " + reason);

            }
        });
    }

    private void startPeerDiscovery() {
        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                mServiceState = ServiceState.DiscoverPeer;
                MeshLog.i(" Started peer discovery");
            }

            public void onFailure(int reason) {
                MeshLog.e(" Starting peer discovery failed, error code " + reason);
            }
        });
    }

    private void stopPeerDiscovery() {
//                public static final int ERROR               = 0;
//                public static final int P2P_UNSUPPORTED     = 1;
//                public static final int BUSY                = 2;
        // TODO: 8/21/2019
        //Check:
        // 1. whether it fails for blank peerDiscovery stop.
        // 2. Is there any way to detect peer disoovery running or not?
        mWifiP2pManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
            }

            public void onFailure(int reason) {

            }
        });
    }

    // TODO: 8/21/2019
    // 1. Does it fail if no service request present?
    // 2. Is there any way to check service discovery running or not?
    private void stopServiceDiscovery() {
        mWifiP2pManager.clearServiceRequests(mChannel, new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                MeshLog.i(" Cleared service requests");
            }

            public void onFailure(int reason) {
                MeshLog.i(" Clearing service requests failed, error code -> " + reason);
            }
        });
    }


    public void stop() {
        mIsAlive = false;
        try {
            mContext.unregisterReceiver(mPeerReceiver);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        stopServiceDiscovery();
        stopPeerDiscovery();
    }


    private P2PStateListener mP2PStateListener = new P2PStateListener() {
        @Override
        public void onP2PStateChange(int state) {

        }

        @Override
        public void onP2PPeersStateChange() {
            if (mServiceState != ServiceState.DiscoverService) {
                mWifiP2pManager.requestPeers(mChannel, mPeerListListener);
            }
        }

        @Override
        public void onP2PConnectionChanged() {
            startPeerDiscovery();
        }

        @Override
        public void onP2PDisconnected() {
            startPeerDiscovery();
        }

        @Override
        public void onP2PPeersDiscoveryStarted() {

        }

        @Override
        public void onP2PPeersDiscoveryStopped() {
            startPeerDiscovery();
        }
    };

    @Override
    public void onChannelDisconnected() {

    }
}