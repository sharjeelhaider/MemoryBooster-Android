package com.raihanbd.easyrambooster.antivirus;




import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;


public class IconExtractor
{



    public void getIcon(String packageName, ImageView imageView, Context context)
    {

        final PackageManager pm = context.getPackageManager();


        try
        {
            Drawable icon = pm.getApplicationIcon(packageName);
            imageView.setImageDrawable(icon);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }


    }

public String getAppName (Context context, String packageName, TextView appName)
{


    final PackageManager pm = context.getApplicationContext().getPackageManager();

    ApplicationInfo ai;

    try
    {
        ai = pm.getApplicationInfo( packageName, 0);
    }
    catch (final PackageManager.NameNotFoundException e)
    {
        ai = null;
    }


    String name = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");


    appName.setText(name);

    return name;

}
}
