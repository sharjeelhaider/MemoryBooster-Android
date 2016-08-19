package com.booster.avivast.antivirus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

/**
 * Created by hexdump on 15/01/16.
 */

public class PackageData
{
    private String _packageName;
    public String getPackageName() { return _packageName; }
    public void setPackageName(String packageName) { _packageName=packageName;}

    //Factory method
    public PackageData() {}

    public PackageData(String packageName)
    {
        setPackageName(packageName);
    }

    public int hashCode()
    {
        return (int) _packageName.hashCode();
    }

    public boolean equals(Object o)
    {
        if(o == null || o.getClass()!=this.getClass())
            return false;

        PackageData other = (PackageData) o;
        return _packageName.equals(other._packageName);
    }

    public JSONObject buildJSONObject() throws JSONException
    {
        JSONObject jsonObj=new JSONObject();
        jsonObj.put("packageName",_packageName);
        return jsonObj;
    }

    /*public AppProblem createBadPackageResultData(Context context)
    {
        try
        {
            PackageInfo pi = ActivityTools.getPackageInfo(context,getPackageName(),PackageManager.GET_ACTIVITIES | PackageManager.GET_PERMISSIONS);
            if(pi!=null)
            {
                AppProblem bprd = new AppProblem(pi.packageName);
                return bprd;
            }
            else
                return null;
        }
        catch(PackageManager.NameNotFoundException ex)
        {
            return null;
        }
    }*/

    public static List<PackageData> getPackagesByName(Set<PackageData> packages, String filter, List<PackageData> result)
    {
        boolean wildcard=false;

        result.clear();

        if(filter.charAt(filter.length()-1)=='*')
        {
            wildcard=true;
            filter=filter.substring(0,filter.length()-2);
        }
        else
            wildcard=false;

        for (PackageData packInfo : packages)
        {
            if(packInfo._packageName.startsWith(filter))
            {
                result.add(packInfo);

                //Just one package if we were not using a wildcard
                if (!wildcard)
                    break;
            }
        }

        return result;
    }

    public static boolean isPackageInListByName(Set<PackageData> packages, String filter)
    {
        boolean wildcard=false;

        if(filter.charAt(filter.length()-1)=='*')
        {
            wildcard=true;
            filter=filter.substring(0,filter.length()-2);
        }
        else
            wildcard=false;

        for (PackageData packInfo : packages)
        {
            if(packInfo._packageName.startsWith(filter))
                return true;
        }

        return false;
    }


}
