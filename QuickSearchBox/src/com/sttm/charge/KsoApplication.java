package com.sttm.charge;

import android.app.Application;

public class KsoApplication extends Application {    
    @Override    
    public void onCreate() {    
        super.onCreate();    
        KsoExceptionHandler crashHandler = KsoExceptionHandler.getInstance();    
        crashHandler.init(getApplicationContext());    
    }    
}    