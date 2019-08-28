package com.w3engineers.appshare.util.wifidirect;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-04-10 at 4:06 PM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-04-10 at 4:06 PM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-04-10 at 4:06 PM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class WiFiDirectConfig {

    public boolean mIsGroupOwner, mIsClient;

    /**
     * Configure whether our system should be greedy to connect to desired network.
     */
    public boolean mIsForceFulReconnectionAllowed = true;

}
