package com.booster.avivast.antivirus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PackageBroadcastReceiver extends BroadcastReceiver 
{
    static IPackageChangesListener _listener;
	
    static public void setPackageBroadcastListener(IPackageChangesListener listener)
    {
    	_listener=listener;
    }
    
    @Override
    public void onReceive(Context ctx, Intent intent) 
    {
    	/*Uri data = intent.getData();
    	Log.d("Info", "Action: " + intent.getAction());
    	Log.d("Info", "The DATA: " + data);*/
    	
    	if(_listener!=null && Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction()))
    		_listener.OnPackageAdded(intent);
    	if(_listener!=null && Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction()))
    		_listener.OnPackageRemoved(intent);
   	
    }
}
