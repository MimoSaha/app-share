package com.w3engineers.appshare.util.wifidirect;

/**
 * ============================================================================
 * Copyright (C) 2019 W3 Engineers Ltd - All Rights Reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * <br>----------------------------------------------------------------------------
 * <br>Created by: Ahmed Mohmmad Ullah (Azim) on [2019-03-28 at 11:46 AM].
 * <br>----------------------------------------------------------------------------
 * <br>Project: MeshX.
 * <br>Code Responsibility: <Purpose of code>
 * <br>----------------------------------------------------------------------------
 * <br>Edited by :
 * <br>1. <First Editor> on [2019-03-28 at 11:46 AM].
 * <br>2. <Second Editor>
 * <br>----------------------------------------------------------------------------
 * <br>Reviewed by :
 * <br>1. <First Reviewer> on [2019-03-28 at 11:46 AM].
 * <br>2. <Second Reviewer>
 * <br>============================================================================
 **/
public class Constants {

    public interface Service {
        /**
         * Identity of broadcasting service. Try to keep it short. It has cost with discovery
         * performance.
         */
//        String TYPE = "Fe";
        String TYPE = "Az";
        long DISCOVERY_DELAY = 1000;//In ms
    }
}
