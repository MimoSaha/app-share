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
    private Set<String> mConnectedSsidNames;
    private Set<String> mConnectedBleNames;
    private static ConnectionLinkCache sConnectionLinkCache;

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
}
