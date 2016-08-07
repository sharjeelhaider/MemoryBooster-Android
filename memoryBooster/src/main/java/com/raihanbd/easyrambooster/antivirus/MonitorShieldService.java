package com.raihanbd.easyrambooster.antivirus;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import geniuscloud.memory.booster.R;

/**
 * Created by hexdump on 14/01/16.
 * Based on stack overflow link: http://stackoverflow.com/questions/20594936/communication-between-activity-and-service
 *
 */
public class MonitorShieldService extends Service
{
    final String _logTag=MonitorShieldService.class.getSimpleName();

    private final IBinder _binder=new MonitorShieldLocalBinder();

    PackageBroadcastReceiver _packageBroadcastReceiver;

    Set<PackageData> _whiteListPackages;
    public Set<PackageData> getWhiteListPackages() { return _whiteListPackages; }
    Set<PackageData> _blackListPackages;
    public Set<PackageData> getBlackListPackages(){return _blackListPackages;}
    Set<PackageData> _blackListActivities;
    public Set<PackageData> getBlackListActivities() { return _blackListActivities;}
    Set<PermissionData> _suspiciousPermissions;
    public Set<PermissionData> getSuspiciousPermissions() { return _suspiciousPermissions;}
    UserWhiteList _userWhiteList=null;
    public UserWhiteList getUserWhiteList() { return _userWhiteList;}
    MenacesCacheSet _menacesCacheSet =null;
    public MenacesCacheSet getMenacesCacheSet() { return _menacesCacheSet; }
    private int _appIcon = R.drawable.ic_launcher;
    IClientInterface _clientInterface=null;
    public void registerClient(IClientInterface clientInterface) { _clientInterface=clientInterface;}

    static int _currentNotificationId=0;
    @Override
    public void onCreate()
    {
        super.onCreate();

        //Log.i("CRESAN", "################## Service OnCreate called");

        /*NotificationTools.notificatePermanentPush(MonitorShieldService.this, 0xFF00, _appIcon, "", "", "",
                new Intent(MonitorShieldService.this, AntivirusActivity.class));*/

        _packageBroadcastReceiver = new PackageBroadcastReceiver();
        _packageBroadcastReceiver.setPackageBroadcastListener(new IPackageChangesListener()
        {

            public void OnPackageAdded(Intent i)
            {
                String packageName = i.getData().getSchemeSpecificPart();
                scanApp(packageName);
            }

            public void OnPackageRemoved(Intent intent)
            {
                //TODO: Add code to make ourselves sure that not installed apps will appear in userwhitelist or results fragment
                /*String packageName = intent.getData().getSchemeSpecificPart();
                boolean removed= ProblemsDataSetTools.removeAppProblemByPackage(_menacesCacheSet,packageName);
                if(removed)
                    Log.e(_logTag,">>>>>>>>>>>>>>>>>>>  The application "+packageName+" was removed from menace list because it was uninstalled.");
                else
                    Log.e(_logTag,">>>>>>>>>>>>>>>>>>>  The application "+packageName+" could no be removed from menaceCache while being uninstalled. ERRRRRORRRRRRRR!!!!!!");

                _menacesCacheSet.writeToJSON();

                removed=ProblemsDataSetTools.removeAppProblemByPackage(_userWhiteList,packageName);
                if(removed)
                    Log.e(_logTag,">>>>>>>>>>>>>>>>>>>  The application "+packageName+" was removed from white list because it was uninstalled.");
                else
                    Log.e(_logTag,">>>>>>>>>>>>>>>>>>>  The application "+packageName+" could no be removed from white while being uninstalled. ERRRRRORRRRRRRR!!!!!!");

                _userWhiteList.writeToJSON();*/
            }
        });

        IntentFilter packageFilter = new IntentFilter();
        packageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        packageFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);

        packageFilter.addDataScheme("package");
        this.registerReceiver(_packageBroadcastReceiver, packageFilter);

        _loadDataFiles();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        this.unregisterReceiver(_packageBroadcastReceiver);
        _packageBroadcastReceiver = null;

        //Log.d("CRESAN", "################## Service onDestroy command called");
    }

    @Override
    public IBinder onBind(Intent i)
    {
        return _binder;
    }

    //returns the getInstance of the service
    public class MonitorShieldLocalBinder extends Binder
    {
        public MonitorShieldService getServiceInstance()
        {
            return MonitorShieldService.this;
        }
    }

    public interface IClientInterface
    {
        //Called when a menace is found by the watchdog
        public void onMonitorFoundMenace(IProblem menace);
        //All packages to scan can be useful if the client wants to do for example some animation to cheat :P
        public void onScanResult(List<PackageInfo> allPackages, Set<IProblem> menacesFound);
    }

    private void _loadDataFiles()
    {

        _whiteListPackages=new HashSet<PackageData>();
        _blackListPackages=new HashSet<PackageData>();
        _blackListActivities=new HashSet<PackageData>();
        _suspiciousPermissions= new HashSet<PermissionData>();

        //Build/Load user list
        _userWhiteList=new UserWhiteList(this);
        //Build/Load MenaceCache list
        _menacesCacheSet = new MenacesCacheSet(this);

        //Load WhiteList
        try
        {
            String jsonFile= StaticTools.loadJSONFromAsset(this, "whiteList.json");
            JSONObject obj = new JSONObject(jsonFile);

            JSONArray m_jArry = obj.getJSONArray("data");

            for (int i = 0; i < m_jArry.length(); i++)
            {
                JSONObject temp = m_jArry.getJSONObject(i);
                PackageData pd=new PackageData(temp.getString("packageName"));
                _whiteListPackages.add(pd);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        //Load blackPackagesList
        try
        {
            String jsonFile= StaticTools.loadJSONFromAsset(this, "blackListPackages.json");
            JSONObject obj = new JSONObject(jsonFile);

            JSONArray m_jArry = obj.getJSONArray("data");

            for (int i = 0; i < m_jArry.length(); i++)
            {
                JSONObject temp = m_jArry.getJSONObject(i);
                PackageData pd=new PackageData(temp.getString("packageName"));
                _blackListPackages.add(pd);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        //Load blackActivitiesList
        try
        {
            String jsonFile= StaticTools.loadJSONFromAsset(this, "blackListActivities.json");
            JSONObject obj = new JSONObject(jsonFile);

            JSONArray m_jArry = obj.getJSONArray("data");

            for (int i = 0; i < m_jArry.length(); i++)
            {
                JSONObject temp = m_jArry.getJSONObject(i);
                PackageData pd=new PackageData(temp.getString("packageName"));
                _blackListActivities.add(pd);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        //Load permissions data
        try
        {
            String jsonFile= StaticTools.loadJSONFromAsset(this, "permissions.json");
            JSONObject obj = new JSONObject(jsonFile);

            JSONArray m_jArry = obj.getJSONArray("data");

            for (int i = 0; i < m_jArry.length(); i++)
            {
                JSONObject temp = m_jArry.getJSONObject(i);
                PermissionData pd=new PermissionData(temp.getString("permissionName"),temp.getInt("dangerous"));
                _suspiciousPermissions.add(pd);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    protected boolean _checkIfPackageInWhiteList(String packageName, Set<PackageData> whiteListPackages)
    {
        for (PackageData packageInfo :  whiteListPackages)
        {
            String packageMask=packageInfo.getPackageName();
            if(StaticTools.stringMatchesMask(packageName, packageMask))
                return true;
        }

        return false;
    }

    public void scanFileSystem()
    {
        //Scan installed packages
        List<PackageInfo> allPackages= StaticTools.getApps(this, PackageManager.GET_ACTIVITIES | PackageManager.GET_PERMISSIONS);
        List<PackageInfo> nonSystemApps=StaticTools.getNonSystemApps(this,allPackages);

        //Packages with problems will be stored here
        Set<IProblem> tempBadResults=new HashSet<IProblem>();

        //Filter white listed apps
        List<PackageInfo> potentialBadApps=_removeWhiteListPackagesFromPackageList(nonSystemApps, _whiteListPackages);
        potentialBadApps=_removeWhiteListPackagesFromPackageList(potentialBadApps, ProblemsDataSetTools.getAppProblemsAsPackageDataList(_userWhiteList));

        Scanner.scanForBlackListedActivityApps(potentialBadApps, _blackListActivities, tempBadResults);
        Scanner.scanForSuspiciousPermissionsApps(potentialBadApps, _suspiciousPermissions, tempBadResults);
        Scanner.scanInstalledAppsFromGooglePlay(this, potentialBadApps, tempBadResults);
        Scanner.scanSystemProblems(this, _userWhiteList, tempBadResults);
        /*for (AppProblem p : tempBadResults)
        {
            Log.d(_logTag, "======PACKAGE "+p.getPackageName()+" GPlay install: "+p.getInstalledThroughGooglePlay());
            if(p.getActivityData().size()>0)
            {
                Log.d(_logTag, "=========BLACK-ACTIVITIES>");
                for (ActivityData ad : p.getActivityData())
                {
                    Log.d(_logTag, "=============> " + ad.getActivityInfo().name);
                }
            }
            if(p.getPermissionData().size()>0)
            {
                Log.d(_logTag,"=========BAD-PERMISSIONS>");
                for(PermissionData pd : p.getPermissionData())
                {
                    Log.d(_logTag,"=============> "+ pd.getPermissionName());
                }
            }

            Log.d(_logTag," ");
        }

        showResultFragment(new ArrayList<AppProblem>(tempBadResults));*/

        //Pasamos esto por ahora para que no se pete el tema
        //List<PackageInfo> _packagesInfo=new ArrayList<PackageInfo>();
        //_packagesInfo.add(allPackages.get(0));
        //_packagesInfo.add(allPackages.get(1));
        //_packagesInfo.add(allPackages.get(2));

        //Merge results with non resolved previous ones and serialize
        Log.e(_logTag, "----------------------> Numero de aplicciones escaneadas: " + allPackages.size());

        _menacesCacheSet.addItems(tempBadResults);
        _menacesCacheSet.writeToJSON();

        if(_clientInterface!=null)
            _clientInterface.onScanResult(allPackages,tempBadResults);

        //_startScanningAnimation(_packageInfo,_foundMenaces);

    }

    public void scanApp(String packageName)
    {
        //Log.d(_logTag,"OOOOOOOOOOOOOOOOOOO> "+"MonitorShieldService:scanApp: Usando app data");
        AppData appData=AppData.getInstance(this);

        Intent toExecuteIntent = new Intent(MonitorShieldService.this, AntivirusActivity.class);

        Intent openAppIntent = getPackageManager().getLaunchIntentForPackage(packageName);

        String appName = StaticTools.getAppNameFromPackage(MonitorShieldService.this, packageName);

        boolean whiteListed=Scanner.isAppWhiteListed(packageName, _whiteListPackages);

        if(whiteListed)
        {
            StaticTools.notificatePush(MonitorShieldService.this,_currentNotificationId++, _appIcon,
                    appName + " " + getString(R.string.trusted_message), appName, "App " + appName + " " + getString(R.string.trusted_by_app), openAppIntent);
        }
        else
        {
            //We have it in our white package list
            /*if(ProblemsDataSetTools.checkIfPackageInCollection(packageName, _userWhiteList.getSet()))
            {
                NotificationTools.notificatePush(MonitorShieldService.this, _currentNotificationId++, _appIcon,
                        appName + " " + getString(R.string.trusted_message), appName, "App " + appName + " " + getString(R.string.trusted_by_user), openAppIntent);
            }
            else
            {*/
                PackageInfo pi=null;
                try
                {
                    pi=StaticTools.getPackageInfo(this,packageName, PackageManager.GET_ACTIVITIES | PackageManager.GET_PERMISSIONS);
                }
                catch(PackageManager.NameNotFoundException ex)
                {
                    pi=null;
                }

                if(pi!=null)
                {
                    AppProblem bpbr=new AppProblem(pi.packageName);
                    List<ActivityInfo> recycleList=new ArrayList<ActivityInfo>();
                    Scanner.scanForBlackListedActivityApp(pi, bpbr, _blackListActivities, recycleList);
                    Scanner.scanForSuspiciousPermissionsApp(pi, bpbr, _suspiciousPermissions);
                    Scanner.scanInstalledAppFromGooglePlay(this, bpbr);

                    if(bpbr.isMenace())
                    {
                        //Do not scan if we haven't done any
                        if(appData.getFirstScanDone())
                        {
                            _menacesCacheSet.addItem(bpbr);
                            _menacesCacheSet.writeToJSON();
                        }

                        if(_clientInterface!=null)
                        {
                            _clientInterface.onMonitorFoundMenace(bpbr);
                        }

                        StaticTools.notificatePush(MonitorShieldService.this, _currentNotificationId++, _appIcon,
                                appName + " " + getString(R.string.has_been_scanned), appName, getString(R.string.enter_to_solve_problems), toExecuteIntent);

                    }
                    else
                        StaticTools.notificatePush(MonitorShieldService.this, _currentNotificationId++, _appIcon,
                            appName + " " + getString(R.string.is_secure), appName, getString(R.string.has_no_threats), toExecuteIntent);
                }
            //}
        }
    }

    protected List<PackageInfo> _removeWhiteListPackagesFromPackageList(List<PackageInfo> packagesToSearch, Set<? extends PackageData> whiteListPackages)
    {
        boolean found=false;

        List<PackageInfo> trimmedPackageList=new ArrayList<PackageInfo>(packagesToSearch);

        //Check against whitelist
        for(PackageData pd : whiteListPackages)
        {
            PackageInfo p = null;
            int index = 0;
            String mask = pd.getPackageName();
            found = false;

            while (found == false && index < trimmedPackageList.size())
            {
                p = trimmedPackageList.get(index);

                if (StaticTools.stringMatchesMask(p.packageName, mask))
                    trimmedPackageList.remove(index);
                else
                    ++index;
            }
        }

        return trimmedPackageList;
    }
}
