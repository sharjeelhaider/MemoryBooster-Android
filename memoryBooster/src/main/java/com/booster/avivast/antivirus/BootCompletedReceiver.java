package com.booster.avivast.antivirus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hexdump on 15/01/16.
 */
public class BootCompletedReceiver extends BroadcastReceiver
{
    final String _logTag=BootCompletedReceiver.class.getSimpleName();
    @Override
    public void onReceive(Context context, Intent intent)
    {
        //Log.d(_logTag,"======> Received boot device intent");

        if(!StaticTools.isServiceRunning(context,MonitorShieldService.class))
        {
            //Log.d(_logTag,"======> BootCompleteReceiver:onReceive: MonitorShieldService not running. Starting it...");
            Intent myIntent = new Intent(context, MonitorShieldService.class);
            context.startService(myIntent);
        }
        else
        {
            //Log.d(_logTag,"======> BootCompleteReceiver:onReceive: MonitorShieldService was running. No need to start it again.");
        }
    }
}