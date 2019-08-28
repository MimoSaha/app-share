package com.w3engineers.appshare.util.wifidirect;

import java.util.ArrayList;
import java.util.UUID;

public class Constant {

    public static String RANDOM_STATE = "random_state";
    public static String KEY_DEVICE_SSID_NAME = "ssid_name";
    public static String KEY_DEVICE_BLE_NAME = "device_ble";
    public static String KEY_USER_ID = "id";
    public static String KEY_USER_NAME = "name";
    public static String KEY_WIFI_BLE_NAME = "wifi_ble";

    public static String KEY_MULTIVERSE_URL = "multiverse_url";

    public static String WIFI_AP_BLE_PREFIX = "Az";

    public static final String P2P_BLE_PREFIX = "star-";


    public static final String BLE_CHANNEL_PREFIX = "f520cf2c-6487-11e7-907b-";

    public static final String NAME_SECURE = "BluetoothChatSecure";
    public static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application
    public static final UUID MY_UUID_SECURE = UUID
            .fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    public static final UUID MY_UUID_INSECURE = UUID
            .fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");


    public static String KEY_DEBUG = "debug";

    public static String MASTER_IP_ADDRESS = "192.168.49.1";

    public static String KEY_P2P_MASTER = "p2p_master";

    public static String KEY_SETTING_AP_MODE = "setting_ap_mode";

    public static String KEY_BLUETOOTH_ENABLE = "bluetooth";

    public static String KEY_DEVICE_AP_MODE = "device_ap_mode";
    public static String KEY_DEVICE_P2P_MODE = "device_p2p_mode";

    public static String KEY_AUTO_MESH_MODE = "mesh_mode";

    public static String KEY_NETWORK_PREFIX = "network_prefix";
    public static String KEY_BLE_CONNECTION_MODE = "ble_mode";
    public static String BLE_CONNECTION_MODE_SERVER = "SERVER";
    public static String BLE_CONNECTION_MODE_CLIENT = "CLIENT";

    public static String LATEST_UPDATE = "latest_update";
    public static long SELLER_MINIMUM_WARNING_DATA = 1024; // The format is byte


    private static ArrayList<UUID> mUuids;

    public static ArrayList<UUID> generateUUIDs() {

        if (mUuids == null) {

            mUuids = new ArrayList<>();

            // generate unique uuids for the session
            mUuids.add(UUID.fromString("b7746a40-c758-4868-aa19-7ac6b3475dfc"));
            mUuids.add(UUID.fromString("2d64189d-5a2c-4511-a074-77f199fd0834"));
            mUuids.add(UUID.fromString("e442e09a-51f3-4a7b-91cb-f638491d1412"));
            mUuids.add(UUID.fromString("a81d6504-4536-49ee-a475-7d96d09439e4"));
            mUuids.add(UUID.fromString("a81d6504-45a3-44ee-a477-7d97d09469e4"));
            mUuids.add(UUID.fromString("a81d6504-4b3f-48fe-a476-7d98d09459e4"));
            mUuids.add(UUID.fromString("a81d6504-4d3c-43ee-a485-7d99d09449e4"));
        }
        return mUuids;
    }

    /****************From WHERE******************/
    public static final int USER_TYPE_DIRECT = 300;
    public static final int USER_TYPE_VIA_ME = 301;


    public interface netShare {
        String PREF_KEY_IS_SHARING_INTERNET = "PREF_KEY_IS_SHARING_INTERNET";
        int ROLE_NOT_INTERESTED = 0;
        int ROLE_SHARER = 1;
        int ROLE_CONSUMER = 2;
        int ROLE_CONSUMER_MASTER = 3;
        int SSID_POSTFIX_LENGTH = 6;
    }

    public interface SellerStatus {
        int PURCHASE = 0;
        int PURCHASING = 1;
        int PURCHASED = 2;
        int CONNECTING = 3;
        int CONNECTED = 4;
        int DISCONNECT = 5;
        int DISCONNECTING = 6;
        int DISCONNECTED = 7;
        int CLOSE = 8;
        int CLOSING = 9;
        int CLOSED = 10;
    }

    public interface MessageStatus {
        int SENDING = 0;
        int SEND = 1;
        int DELIVERED = 2;
        int RECEIVED = 3;
        int FAILED = 4;
    }

    public interface UserTpe {
        int WIFI = 1;
        int BLUETOOTH = 2;
        int INTERNET = 3;
    }

    public interface DataType {
        int USER_LIST = 1;
        int USER_MESSAGE = 2;
    }

    public interface IntentKeys {
        String NUMBER_OF_ACTIVE_BUYER = "num_of_active_buyer";
        String SHARED_DATA_AMOUNT = "shared_data_amount";
        String ACTIVE_BUYER_LIST = "ACTIVE_BUYER_LIST";
    }

    public interface TimeoutPurpose {
        int INIT_PURCHASE = 6;
        int INIT_CHANNEL = 7;
        int INIT_ETHER = 8;
    }
}
