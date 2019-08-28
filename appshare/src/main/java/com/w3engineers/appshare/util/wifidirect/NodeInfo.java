package com.w3engineers.appshare.util.wifidirect;

public class NodeInfo {
    private String userId;
    private String publicKey;
    private int userMode;
    private int userType;
    private String ssidName;
    private String bleName;
    public String mMacAddress;

    public NodeInfo(String userId, String ssidName, String bleName, String publicKey, int userMode, int userType) {
        this.userId = userId;
        this.ssidName = ssidName;
        this.bleName = bleName;
        this.publicKey = publicKey;
        this.userMode = userMode;
        this.userType = userType;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }


    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getUserType() {
        return this.userType;
    }

    public int getUserMode() {
        return userMode;
    }

    public void setUserMode(int userMode) {
        this.userMode = userMode;
    }

    public String getSsidName() {
        return ssidName;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public void setSsidName(String ssidName) {
        this.ssidName = ssidName;
    }

    @Override
    public String toString() {
        return "NodeInfo{" +
                "publicKey='" + publicKey + '\'' +
                ", isInternetUser=" + userMode +
                ", userMode=" + userMode +
                ", ssidName='" + ssidName + '\'' +
                ", bleName='" + bleName + '\'' +
                ", mMacAddress='" + mMacAddress + '\'' +
                '}';
    }
}
