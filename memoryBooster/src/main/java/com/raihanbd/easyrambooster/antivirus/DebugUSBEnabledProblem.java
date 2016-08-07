package com.raihanbd.easyrambooster.antivirus;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import geniuscloud.memory.booster.R;


/**
 * Created by hexdump on 15/01/16.
 */
public class DebugUSBEnabledProblem extends SystemProblem
{
    static public final String kSerializationType="usb";


    //Esto si quiers lo puedes poner  dentro de las funciones directamente. Es como debería haber sido
    //En vez de crearte aquí las constantes
    final int kUsbIconResId= R.drawable.usb_icon;
    final int kUsbDescriptionId=R.string.usb_message;
    final int kUsbTitleId= R.string.system_app_usb_menace_title;
    final int kWhiteListAddText= R.string.usb_add_whitelist_message;
    final int kWhiteListRemoveText=R.string.usb_remove_whitelist_message;
    final boolean kUSBIsDangerousMenace=false;

    //Factory method
    public DebugUSBEnabledProblem()
    {
    }

    //public String getSystemProblemType() { return kUSBProblemType; }

    public ProblemType getType() { return ProblemType.SystemProblem;}

    public String getSerializationTypeString() {return kSerializationType; }


    public String getWhiteListOnAddDescription(Context context)
    {
        return context.getString(kWhiteListAddText);
    }

    public String getWhiteListOnRemoveDescription(Context context)
    {
        return context.getString(kWhiteListRemoveText);
    }

    public String getTitle(Context context)
    {
        return context.getString(kUsbTitleId);
    }
    public String getSubTitle(Context context)
    {
        return context.getString(R.string.usb_title);
    }

    public String getDescription(Context context)
    {
        return context.getString(kUsbDescriptionId);
    }

    public Drawable getIcon(Context context)
    {
        return ContextCompat.getDrawable(context, kUsbIconResId);
    }

    public Drawable getSubIcon(Context context)
    {
        return ContextCompat.getDrawable(context, R.drawable.gear);
    }

    public void doAction(Context context)
    {
        StaticTools.openDeveloperSettings(context);

    }

    public boolean isDangerous()
    {
        return false;
    }

    public boolean problemExists(Context context)
    {
        return StaticTools.checkIfUSBDebugIsEnabled(context);
    }
    
}
