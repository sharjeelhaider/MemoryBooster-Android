package com.booster.avivast.antivirus;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hexdump on 16/02/16.
 */
public class StaticTools
{
    //Notifications
    public static final String TAG = "NotificationUtils";
    private static final int NOTIFICATION_DEFAULT_ON = 1000;
    private static final int NOTIFICATION_DEFAULT_OFF = 4000;
    private static final int NOTIFICATION_DEFAULT_COLOR = Color.YELLOW;

    public static void notificatePush(Context context, int notificationId,int iconDrawableId,
                                      String tickerText, String contentTitle, String contentText, Intent intent)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(iconDrawableId)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setTicker(tickerText);

        // Because clicking the notification opens a new ("special") activity, there's no need to create an artificial back stack.
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);

        // Gets an instance of the NotificationManager service
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        notifyMgr.notify(notificationId, mBuilder.build());
    }

    public static void notificatePermanentPush(Context context, int notificationId,int iconDrawableId,
                                               String tickerText, String contentTitle, String contentText, Intent intent)
    {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(iconDrawableId)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setTicker(tickerText)
                .setOngoing(true);

        // Because clicking the notification opens a new ("special") activity, there's no need to create an artificial back stack.
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);

        // Gets an instance of the NotificationManager service
        NotificationManager notifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        notifyMgr.notify(notificationId, mBuilder.build());
    }

    //Media
    public static boolean isSDAvailable()
    {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getSDPath()
    {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getInternalDataPath(Context c)
    {
        return new ContextWrapper(c).getFilesDir().getPath();
    }

    public static boolean existsFile(String filePath)
    {
        File f = new File(filePath);
        if(f.exists() && !f.isDirectory())
            return true;
        else
            return false;
    }

    public static boolean existsFolder(String folderPath)
    {
        File f = new File(folderPath);
        if(f.exists() && f.isDirectory())
            return true;
        else
            return false;
    }

    public static boolean existsInternalStorageFile(Context ctx, String internalRelativePath)
    {
        String fullPath=StaticTools.getInternalDataPath(ctx)+internalRelativePath;
        return existsFile(fullPath);
    }

    public static boolean existsSDFile(Context ctx, String sdRelativePath)
    {
        String fullPath=StaticTools.getSDPath()+sdRelativePath;
        return existsFile(fullPath);
    }


    public static boolean deleteFile(String filePath)
    {
        File f= new File(filePath);
        if(f.exists() && !f.isDirectory())
            return f.delete();
        else
        {
            //Log.i("IO", "The file you want to delete does not exist or is a folder");
            return false;
        }
    }

    public static StatFs getSDData()
    {
        StatFs statFS=new StatFs(getSDPath());

        return statFS;
    }

    public static File createTempFile(Context context, String prefix, String extension)
    {
        File outputDir = context.getCacheDir();
        File outputFile=null;
        try
        {
            outputFile = File.createTempFile(prefix, extension, outputDir);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }

        return outputFile;
    }


    public static void copyAssetFileToCacheFile(Context context,String assetFile, File cacheFile)
    {
        try
        {
            InputStream inStream = context.getAssets().open(assetFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            _copyAssetFile(br, cacheFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    public static boolean copyAssetFileToFileSystem(Context context,String assetFile, String dstFile)
    {
        File toFile = new File(dstFile);
        return copyAssetFileToFileSystem(context,assetFile,toFile);

    }

    public static boolean copyAssetFileToFileSystem(Context context, String assetFile, File dstFile)
    {
        boolean success=true;

        try
        {
            InputStream inStream = context.getAssets().open(assetFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
            _copyAssetFile(br, dstFile);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            success=false;
        }

        return success;

    }


    private static void _copyAssetFile(BufferedReader br, File toFile) throws IOException
    {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(toFile));

            int in;
            while ((in = br.read()) != -1) {
                bw.write(in);
            }
        } finally {
            if (bw != null) {
                bw.close();
            }
            br.close();
        }
    }

    public static String loadJSONFromAsset(Context context,String file)
    {
        String json = null;
        try
        {
            InputStream is = context.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String loadJSONFromFile(Context context,String filePath)
    {
        StringBuilder text=new StringBuilder();
        try
        {
            BufferedReader br=new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = br.readLine()) != null)
            {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return null;
        }

        return text.toString();
    }

    static public String getFileExtension(String fileName)
    {
        try {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }

    static public String loadTextFile(String filePath) throws IOException
    {
        //Get the text file
        File file = new File(filePath);

        //Read text from file
        StringBuilder text = new StringBuilder();

        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null)
            {
                text.append(line);
                text.append('\n');
            }
        }
        finally
        {
            br.close();
        }

        return text.toString();
    }

    static public void writeTextFile(String filePath, String text) throws IOException
    {
        BufferedWriter bw=null;
        try
        {
            bw = new BufferedWriter(new FileWriter(filePath));
            bw.write(text);
        }
        finally
        {
            bw.close();
        }
    }


    static public boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //Activitiets
    public static String getPackageName(final Context context)
    {
        return context.getPackageName();
    }

    /*public static boolean isPackageInstalled(final Context context, final String packageName)
    {
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages)
        {
            if (packageInfo.packageName.equals(packageName))
                return true;
        }

        return false;
    }*/

    public static boolean isPackageInstalled(Context context,String targetPackage)
    {
        PackageManager pm=context.getPackageManager();
        try
        {
            PackageInfo info=pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }
        return true;
    }

    public static List<PackageInfo> getSystemApps(final Context context, List<PackageInfo> appsToFilter)
    {

        List<PackageInfo> filteredPackgeInfo = new ArrayList<PackageInfo>();

        PackageInfo packInfo = null;

        int mask= ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;

        for (int i = 0; i < appsToFilter.size(); i++)
        {
            packInfo = appsToFilter.get(i);
            if ((packInfo.applicationInfo.flags & mask) != 0)
            {
                filteredPackgeInfo.add(packInfo);
            }
        }

        return filteredPackgeInfo;
    }

    public static List<PackageInfo> getNonSystemApps(final Context context, List<PackageInfo> appsToFilter)
    {

        List<PackageInfo> filteredPackgeInfo = new ArrayList<PackageInfo>();

        PackageInfo packInfo = null;

        int mask=ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;

        for (int i = 0; i < appsToFilter.size(); i++)
        {
            packInfo = appsToFilter.get(i);
            if ((packInfo.applicationInfo.flags & mask) == 0)
            {
                filteredPackgeInfo.add(packInfo);
            }
        }

        return filteredPackgeInfo;
    }

    public static void logPackageNames(List<PackageInfo> packages)
    {
        PackageInfo pi = null;
        for (int i = 0; i < packages.size(); ++i)
        {
            pi = packages.get(i);
            Log.d("Package", pi.packageName);
        }
    }

    public static List<PackageInfo> getApps(final Context context, int packageManagerPermissions)
    {
        return context.getPackageManager().getInstalledPackages(packageManagerPermissions);
    }

    public static String getAppNameFromPackage(final Context context, final String packageName)
    {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        String appName = "";
        try
        {
            appName = (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA));
        }
        catch (PackageManager.NameNotFoundException ex)
        {
            appName = "Unkown app";
        }

        return appName;
    }


    public static Drawable getIconFromPackage(String packageName, Context context)
    {

        final PackageManager pm = context.getPackageManager();
        Drawable icon = null;

        try
        {
            icon = pm.getApplicationIcon(packageName);

        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return icon;

    }


    public static boolean checkIfAppWasInstalledThroughGooglePlay(Context context, String packageName)
    {

        final PackageManager packageManager = context.getPackageManager();

        try
        {
            final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            if ("com.android.vending".equals(packageManager.getInstallerPackageName(applicationInfo.packageName)))
            {
                // App was installed by Play Store
                //Sacar error similar a este: Title: apk desconocida MSG: La instalacion de apk desde fuentes desconocidas puede ser peligroso

                return true;
            }
        } catch (final PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
            return false;
        }

        return false;

    }


    public static boolean checkIfUSBDebugIsEnabled(Context context)
    {

        if (Settings.Secure.getInt(context.getContentResolver(), Settings.Global.ADB_ENABLED, 0) == 1)
        {
            // debugging enabled
            //Sacar error similar a este: Title: Depuracion USB MSG: Cuando la depuracion de USB esta activada, los intrusos pueden acceder a sus datos privados.
            return true;
        } else
        {
            //debugging does not enabled
            return false;
        }
    }


    public static ActivityInfo[] getActivitiesInPackage(Context context, String packageName, int packageManagerPermissions ) throws PackageManager.NameNotFoundException
    {
        PackageInfo pi=getPackageInfo(context,packageName,packageManagerPermissions);
        return pi.activities;
    }

    public static PackageInfo getPackageInfo(Context context, String packageName, int packageManagerPermissions) throws PackageManager.NameNotFoundException
    {
        return context.getPackageManager().getPackageInfo(packageName, packageManagerPermissions);
    }

    public static boolean packageInfoHasPermission(PackageInfo packageInfo, String permissionName)
    {
        if(packageInfo.requestedPermissions==null)
            return false;

        for(String permInfo :  packageInfo.requestedPermissions)
        {
            if(permInfo.equals(permissionName))
                return true;
        }

        return false;
    }

    public static boolean checkIfUnknownAppIsEnabled(Context context)
    {

        if (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS,0) == 1)
        {

            return true;
        } else
        {

            return false;
        }

    }

    public static void openSecuritySettings(Context context)
    {

        context.startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));


    }

    public static void openDeveloperSettings(Context context)
    {

        context.startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));


    }
    //Example fillParams("My name is #1 and I have #2 years", "#", John, 12)
    public static String fillParams(String data, String paramStr, String ... args)
    {
        int total = 0;

        for (int i = 0; i < args.length; i++)
            data=data.replace(paramStr+(i+1),args[i]);

        return data;
    }

    public static String capitalize(String source)
    {
        return source.substring(0, 1).toUpperCase() + source.substring(1);
    }

    public static String hexStrToStr(String hex)
    {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hex.length(); i+=2)
        {
            String str = hex.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }

    //Encode
    public static String mixUp(String str, int interleaveRange)
    {

        StringBuffer sb = new StringBuffer(str);

        for (int i = 0; i < str.length() - interleaveRange; ++i) {
            char c = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(i + interleaveRange));
            sb.setCharAt(i + interleaveRange, c);
        }

        return sb.toString();
    }

    //Decode
    public static String unMixUp(String str, int interleaveRange)
    {

        StringBuffer sb = new StringBuffer(str);

        for (int i = str.length() - 1; i >= interleaveRange; --i)
        {
            char c = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(i - interleaveRange));
            sb.setCharAt(i - interleaveRange, c);
        }

        return sb.toString();
    }

    public static String padRight(String s, int n, char paddingChar)
    {
        return String.format("%1$-" + n + "s", s).replace(' ', paddingChar);
    }

    public static String padLeft(String s, int n, char paddingChar)
    {
        return String.format("%1$" + n + "s", s).replace(' ',paddingChar);
    }

    public static void convertFileSizeToString(long size, String[] outputParts)
    {
        if(outputParts.length!=2)
            throw new IllegalArgumentException("output parts must be an array of length 2");

        final String[] units = new String[] { "b", "Kb", "Mb", "Gb", "Tb", "Pb" };

        if(size <= 0)
        {
            outputParts[0]="0";
            outputParts[1]="Kb";
        }
        else
        {
            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

            if (digitGroups > units.length - 1)
            {
                outputParts[0] = "Too big";
                outputParts[1] = "";
            }
            else
            {
                outputParts[0] =new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) ;
                outputParts[1] = units[digitGroups];
            }
        }
    }

    public static String convertFileSizeToString(long size)
    {
        final String[] units = new String[] { "b", "Kb", "Mb", "Gb", "Tb", "Pb" };

        if(size <= 0)
        {
            return "0Kb";
        }
        else
        {
            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

            if (digitGroups > units.length - 1)
            {
                return "Too big";
            }
            else
            {
                return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups))+units[digitGroups] ;
            }
        }
    }

    static public boolean stringMatchesMask(String packageName, String mask)
    {
        boolean wildcard=false;

        if(mask.charAt(mask.length()-1)=='*')
        {
            wildcard=true;
            mask=mask.substring(0,mask.length()-2);
        }
        else
            wildcard=false;

        if(wildcard==true)
        {
            if (packageName.startsWith(mask))
                return true;
            else
                return false;
        }
        else
        {
            if(packageName.equals(mask))
                return true;
            else
                return false;
        }

    }

    //Views
    @SuppressLint("NewApi")
    static public void setViewBackgroundDrawable(View view,Drawable drawable)
    {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN)
        {
            view.setBackgroundDrawable(drawable);
        }
        else
        {
            view.setBackground(drawable);
        }
    }

    //Service
    public static boolean isServiceRunning(Context context,Class<?> serviceClass)
    {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services)
        {
            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName()))
            {
                return true;
            }
        }
        return false;
    }

    public static void openMarketURL(Context context,String marketUrl, String webUrl)
    {
        try
        {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(marketUrl)));
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl)));
        }
    }

    @SuppressWarnings("unchecked")
    static public <T> T deserializeFromFile(String fileName) throws FileNotFoundException, IOException
    {
        T data = null;
        try
        {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            data = (T) ois.readObject();
            ois.close();
            fis.close();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return data;
    }

    public static <T> T deserializeFromDataFolder(Context ctx, String rootRelativePath)
    {
        T obj = null;

        try
        {

            //Internal
            String path = StaticTools.getInternalDataPath(ctx) + File.separatorChar + rootRelativePath;
            if (StaticTools.existsFile(path))
            {
                obj = StaticTools.deserializeFromFile(path);
            }
        } catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return obj;
    }

    static public void serializeToFile(String fileName,Serializable obj) throws IOException
    {
        File file= new File(fileName);
        String fileParentFolder=file.getParent();
        File parentPath=new File(fileParentFolder);

        if(fileParentFolder!=null)
        {
            if(!StaticTools.existsFolder(fileParentFolder))
                parentPath.mkdirs();
        }

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

		/*try
		{*/
        fos = new FileOutputStream(fileName);
        oos = new ObjectOutputStream(fos);
        oos.writeObject(obj);
        oos.close();
        fos.close();
		/*}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/
    }

    public static void serializeToDataFolder(Context ctx, Serializable obj, String rootRelativePath) throws IOException
    {

        String internalPath= StaticTools.getInternalDataPath(ctx);
        String finalPath=internalPath+File.separatorChar+rootRelativePath;
        serializeToFile(finalPath, obj);
    }
}
