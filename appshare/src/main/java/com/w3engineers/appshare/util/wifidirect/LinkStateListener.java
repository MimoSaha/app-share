package com.w3engineers.appshare.util.wifidirect;



public interface LinkStateListener {
    /**
     *
     * @param nodeId
     * @param transportState
     * @param msg
     */

    void onTransportInit(String nodeId, String publicKey, TransportState transportState, String msg);

    /**
     * Called when transport discovered new device and established connection with it.
     *

     */
    void linkConnected(String nodeId);

    /**
     * <p>Other mesh id from different network</p>
     *
     * @param nodeId : String

     */
    void onMeshLinkFound(String nodeId);

    /**
     * Called when connection to device is closed explicitly from either side
     * or because device is out of range.
     */
    void linkDisconnected(String nodeId);

    /**
     * Called when new data frame is received from remote device.
     *
     * @param frameData frame data received from remote device
     */
    void linkDidReceiveFrame(String senderId, byte[] frameData);

    /**
     * <P>Mesh link disconnected</P>
     *
     * @param nodeId :
     */
    void onMeshLinkDisconnect(String nodeId);

    /**
     * <p>Message delivered ack</p>
     *
     * @param messageId : Long message sent id
     * @param status :
     */
    void onMessageDelivered(String messageId, int status);


}
