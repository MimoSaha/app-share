package com.w3engineers.appshare.util.wifidirect;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConnectionLinkCache {
    private static final int RANDOM_DELAY_MAX = 55;
    private static final int RANDOM_DELAY_MIN = 40;
    private volatile Map<String, Link> discoveredDirectLinkMap;
    private volatile Map<String, Link> discoveredMeshLinkMap;
    private LinkStateListener linkStateListener;
    private volatile Map<String, Boolean> userDataSyncMap;
    private String mNodeId;
    private long USER_PING_TIME = 15000;
    private Map<String, NodeInfo> meshIdAndNodeInfoMap;
    private Set<String> mConnectedSsidNames;
    private Set<String> mConnectedBleNames;
    private static ConnectionLinkCache sConnectionLinkCache;
    private volatile ConcurrentMap<String, Link> internetUserLinkMap;

    private ConnectionLinkCache() {
    }

    //synchronized method to control simultaneous access
    synchronized public static ConnectionLinkCache getInstance() {
        if (sConnectionLinkCache == null) {
            // if instance is null, initialize
            sConnectionLinkCache = new ConnectionLinkCache();
        }
        return sConnectionLinkCache;
    }

    public ConnectionLinkCache initConnectionLinkCache(LinkStateListener linkStateListener, String myNodeId) {
        discoveredDirectLinkMap = Collections.synchronizedMap(new HashMap<>());
        discoveredMeshLinkMap = Collections.synchronizedMap(new HashMap<>());
        userDataSyncMap = Collections.synchronizedMap(new HashMap<>());
        meshIdAndNodeInfoMap = new HashMap<>();
        mConnectedSsidNames = Collections.synchronizedSet(new HashSet<>());
        mConnectedBleNames = Collections.synchronizedSet(new HashSet<>());
        internetUserLinkMap = new ConcurrentHashMap<>();
        this.linkStateListener = linkStateListener;
        this.mNodeId = myNodeId;
        HandlerUtil.postBackground(connectedLinkedUiPassRunnable, generateRandomDelayTime());
        return this;
    }


    public synchronized void clearWifiLinks(LinkStateListener linkStateListener) {
        List<String> wifiMeshIds = getWifiMeshIds();
        for (String item : wifiMeshIds) {
            discoveredMeshLinkMap.remove(item);
            userDataSyncMap.remove(item);
            if (linkStateListener != null) {
                linkStateListener.onMeshLinkDisconnect(item);
            }
        }
    }

    public synchronized void clearBleMeshLinks(LinkStateListener linkStateListener) {
        List<String> bleMeshIds = getBleMeshNodeIds();
        for (String item : bleMeshIds) {
            discoveredMeshLinkMap.remove(item);
            userDataSyncMap.remove(item);
            if (linkStateListener != null) {
                linkStateListener.onMeshLinkDisconnect(item);
            }
        }
    }

    public synchronized boolean isMeshConnected() {
        return discoveredDirectLinkMap.size() > 0;
    }

    public synchronized boolean isWifiUserConnected() {
        for (Map.Entry<String, Link> item : discoveredDirectLinkMap.entrySet()) {
            Link link = item.getValue();
            if (link.getType() == Link.Type.WIFI) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean isBleUserConnected() {
        for (Map.Entry<String, Link> item : discoveredDirectLinkMap.entrySet()) {
            Link link = item.getValue();
            if (link.getType() == Link.Type.BT) {
                return true;
            }
        }
        return false;
    }


    public synchronized Link getDirectConnectedLink(String nodeId) {
        return discoveredDirectLinkMap.get(nodeId);
    }

    public synchronized boolean isDirectLinkAlive(String nodeId) {
        Link link = discoveredDirectLinkMap.get(nodeId);

        if (link != null && link.isConnected()) {
            return true;
        }
        return false;
    }

    public synchronized boolean isDirectLinkExist(String nodeId) {
        return discoveredDirectLinkMap.containsKey(nodeId);
    }

    public synchronized List<Link> getDirectConnectedLinks() {
        return new ArrayList<>(discoveredDirectLinkMap.values());
    }

    public synchronized List<String> getWifiMeshIds() {
        List<String> meshIdList = new ArrayList<>();

        for (Map.Entry<String, Link> item : discoveredMeshLinkMap.entrySet()) {
            Link link = item.getValue();

            if (link.getType() == Link.Type.WIFI) {
                meshIdList.add(item.getKey());
            }
        }
        return meshIdList;
    }

    /*public synchronized List<String> getDirectUserIds() {
        List<String> meshIdList = new ArrayList<>();

        for (Map.Entry<String, Link> item : discoveredDirectLinkMap.entrySet()) {
            meshIdList.add(item.getKey());
        }
        return meshIdList;
    }

    public synchronized List<String> getMeshUserIds() {
        List<String> meshIdList = new ArrayList<>();

        for (Map.Entry<String, Link> item : discoveredMeshLinkMap.entrySet()) {
            meshIdList.add(item.getKey());
        }
        return meshIdList;
    }*/

    public synchronized List<Link> getWifiMeshLinks() {
        List<Link> meshLinkList = new ArrayList<>();

        for (Map.Entry<String, Link> item : discoveredMeshLinkMap.entrySet()) {
            Link link = item.getValue();

            if (link.getType() == Link.Type.WIFI) {
                meshLinkList.add(item.getValue());
            }
        }
        return meshLinkList;
    }

    public synchronized List<Link> getDirectWifiLinks() {
        List<Link> directLinkList = new ArrayList<>();
        for (Map.Entry<String, Link> item : discoveredDirectLinkMap.entrySet()) {
            Link link = item.getValue();

            if (link.getType() == Link.Type.WIFI) {
                directLinkList.add(link);
            }
        }
        return directLinkList;
    }

    public synchronized List<Link> getDirectBleLinks() {
        List<Link> directLinkList = new ArrayList<>();
        for (Map.Entry<String, Link> item : discoveredDirectLinkMap.entrySet()) {
            Link link = item.getValue();
            if (link.getType() == Link.Type.BT) {
                directLinkList.add(link);
            }
        }
        return directLinkList;
    }

    public synchronized List<String> getDirectWifiNodeIds() {
        List<String> directNodeIdList = new ArrayList<>();
        for (Map.Entry<String, Link> item : discoveredDirectLinkMap.entrySet()) {
            Link link = item.getValue();

            if (link.getType() == Link.Type.WIFI) {
                directNodeIdList.add(item.getKey());
            }
        }
        return directNodeIdList;
    }

    public synchronized List<String> getDirectBleNodeIds() {
        List<String> directNodeIdList = new ArrayList<>();
        for (Map.Entry<String, Link> item : discoveredDirectLinkMap.entrySet()) {
            Link link = item.getValue();

            if (link.getType() == Link.Type.BT) {
                directNodeIdList.add(item.getKey());
            }
        }
        return directNodeIdList;
    }

    //Mesh link


    public synchronized Link getMeshConnectedLinks(String nodeId) {
        return discoveredMeshLinkMap.get(nodeId);
    }


    public synchronized List<Link> getMeshConnectedLinks() {
        return new ArrayList<>(discoveredMeshLinkMap.values());
    }

    public synchronized List<String> getMeshConnectedIds() {
        return new ArrayList<>(discoveredMeshLinkMap.keySet());
    }

    public synchronized List<String> getBleMeshNodeIds() {
        List<String> meshBleIds = new ArrayList<>();

        for (Map.Entry<String, Link> item : discoveredMeshLinkMap.entrySet()) {

            Link link = item.getValue();

            if (link.getType() == Link.Type.BT) {
                meshBleIds.add(item.getKey());
            }
        }
        return meshBleIds;
    }


    public synchronized List<Link> getBleMeshLinks() {
        List<Link> meshBleIds = new ArrayList<>();
        for (Map.Entry<String, Link> item : discoveredMeshLinkMap.entrySet()) {
            Link link = item.getValue();

            if (link.getType() == Link.Type.BT) {
                meshBleIds.add(link);
            }
        }
        return meshBleIds;
    }

    public int getLinkCount(int type) {
        if (type == 1) {
            return getDirectWifiLinks().size();
        }
        if (type == 2) {
            return getWifiMeshLinks().size();
        }
        if (type == 3) {
            return getDirectBleLinks().size();
        }
        if (type == 4) {
            return getBleMeshLinks().size();
        }
        return 0;
    }

    public Link getLinkById(String nodeId) {

        Link link = discoveredDirectLinkMap.get(nodeId);
        if (link != null)
            return link;

        link = discoveredMeshLinkMap.get(nodeId);
        if (link != null)
            return link;

        link = internetUserLinkMap.get(nodeId);

        return link;

    }

    public void clearAllLinks() {
        for (Map.Entry<String, Link> item : discoveredDirectLinkMap.entrySet()) {
            String key = item.getKey();
            Link link = item.getValue();
            link.disconnect();
            if (key != null) {
                linkStateListener.onMeshLinkDisconnect(key);
            }
        }

        for (Map.Entry<String, Link> item : discoveredMeshLinkMap.entrySet()) {
            String key = item.getKey();
            Link link = item.getValue();
            link.disconnect();
            if (key != null) {
                linkStateListener.onMeshLinkDisconnect(key);
            }
        }

        discoveredDirectLinkMap.clear();
        discoveredMeshLinkMap.clear();
        userDataSyncMap.clear();

    }

    public Link.Type getLinkType(String meshId) {

        Link link = discoveredDirectLinkMap.get(meshId);

        if (link != null)
            return link.getType();

        link = discoveredMeshLinkMap.get(meshId);
        if (link != null)
            return link.getType();

        return Link.Type.NA;
    }


    private int generateRandomDelayTime() {
        Random random = new Random();
        int randValue = random.nextInt((RANDOM_DELAY_MAX - RANDOM_DELAY_MIN) + 1) + RANDOM_DELAY_MIN;
        return randValue * 1000;
    }

    public NodeInfo getNodeInfoById(String nodeId) {
        return meshIdAndNodeInfoMap.get(nodeId);
    }


    public void addNodeInfo(String userNodeId, NodeInfo userNodeInfo) {
        MeshLog.e("NODE info added in list: "+userNodeInfo.toString());
        meshIdAndNodeInfoMap.put(userNodeId, userNodeInfo);
        mConnectedBleNames.add(userNodeInfo.getBleName());
        mConnectedSsidNames.add(userNodeInfo.getSsidName());
    }

    public void removeNodeInfo(String nodeId) {
        NodeInfo nodeInfo = meshIdAndNodeInfoMap.get(nodeId);
        if (nodeInfo != null) {
            mConnectedBleNames.remove(nodeInfo.getBleName());
            mConnectedSsidNames.remove(nodeInfo.getSsidName());
        }
        meshIdAndNodeInfoMap.remove(nodeId);
    }

    // will check bt name existence in connection hop
    public boolean isBtNameExistInConnectedSet(String btName) {
        if (!TextUtils.isEmpty(btName)) {
            return mConnectedBleNames.contains(btName);
        }
        return false;
    }

    // will check ssid name existence in connection hop
    public boolean isSsidNameExistInConnectedSet(String ssidName) {
        if (!TextUtils.isEmpty(ssidName)) {
            return mConnectedSsidNames.contains(ssidName);
        }
        return false;
    }

    public synchronized void removeDirectLink(String nodeId) {
        discoveredDirectLinkMap.remove(nodeId);
        discoveredMeshLinkMap.clear();
    }

    public synchronized void addMeshLink(String nodeId, NodeInfo nodeInfo, Link link) {
        discoveredMeshLinkMap.put(nodeId, link);
        addNodeInfo(nodeId, nodeInfo);
    }

    public void updateInfoInUserDataSyncMap(String mNodeId) {
        if (mNodeId == null) return;

        userDataSyncMap.put(mNodeId, true);
    }

    public void addInfoInUserDataSyncMap(String mNodeId) {
        if (mNodeId == null) return;

        userDataSyncMap.put(mNodeId, false);
    }

    public void removeInfoFromUserDataSyncMap(String mNodeId) {
        if (mNodeId == null) return;

        userDataSyncMap.remove(mNodeId);
    }

    private Runnable connectedLinkedUiPassRunnable = new Runnable() {
        @Override
        public void run() {
            MeshLog.i(" User Info checking with discovered ID");
            HandlerUtil.postBackground(this, generateRandomDelayTime());
        }
    };


    public synchronized void removeMeshLink(String nodeId) {
        discoveredMeshLinkMap.remove(nodeId);
    }

    public String getConnectedBtSet() {
        return mConnectedBleNames.toString();
    }

    public String getConnectedSsidSet() {
        return mConnectedSsidNames.toString();
    }

    public void clearBtConnectedSet() {
        mConnectedBleNames.clear();
    }

    public void clearSsidConnectedSet() {
        mConnectedSsidNames.clear();
    }
/*
    public WifiLink getAddressFromIp(String ip) {
        if(TextUtils.isEmpty(ip))
            return null;

        for(Map.Entry<String, Link> entry : discoveredDirectLinkMap.entrySet()) {
            Link link = entry.getValue();
            if(link instanceof WifiLink) {
                WifiLink wifiLink = (WifiLink) link;
                if(ip.equals(wifiLink.nodeIpAddress)) {
                    return wifiLink;
                }
            }
        }
        return null;
    }*/

    //NEWLY ADDED
    public void addInternetLink(String nodeId, Link link) {
        internetUserLinkMap.put(nodeId, link);
    }

    public Link getInternetConnectionLink(String nodeId) {
        return internetUserLinkMap.get(nodeId);
    }

    public void removeAllInternetLink() {
        internetUserLinkMap.clear();
    }

    public List<Link> getInternetLinks() {
        return new ArrayList<>(internetUserLinkMap.values());
    }

    public void removeInternetLink(String nodeID) {
        internetUserLinkMap.remove(nodeID);
    }

    public Map<String, Link> getInternetCacheMap() {
        return internetUserLinkMap;
    }

    public boolean isLinkExist(String nodeId) {
        //TODO code needs to be changed
        if (nodeId == null) return false;

        Link link = discoveredDirectLinkMap.get(nodeId);

        if (link != null)
            return true;

        link = discoveredMeshLinkMap.get(nodeId);

        if (link != null)
            return true;

        return false;
    }


}
