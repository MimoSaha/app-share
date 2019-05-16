package com.w3engineers.appshare;
 
/*
============================================================================
Copyright (C) 2019 W3 Engineers Ltd. - All Rights Reserved.
Unauthorized copying of this file, via any medium is strictly prohibited
Proprietary and confidential
============================================================================
*/

import android.app.Application;
import android.content.Context;

import com.w3engineers.appshare.util.lib.InAppShareWebController;
import com.w3engineers.ext.strom.App;

public class AppShareApplication extends App {

    public static Context context;

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        context = getApplicationContext();
        InAppShareWebController.getInAppShareWebController().initContext(base);
    }

    public static Context getAppShareContext() {
        return context;
    }

}
