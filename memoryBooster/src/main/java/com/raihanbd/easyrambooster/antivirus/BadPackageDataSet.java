/**
 * Created by hexdump on 27/01/16.
 */
package com.raihanbd.easyrambooster.antivirus;

;
;
;


/**
 * Created by hexdump on 22/01/16.
 */
/*public class BadPackageDataSet
{
    private Set<AppProblem> _set;
    Set<AppProblem> getSet() {return _set;}
    void setSet(Set<AppProblem> set) { _set=set;}

    IDataSetChangesListener _dataSetChangesListener=null;
    public void setDataSetChangesListener(IDataSetChangesListener listener)  { _dataSetChangesListener=listener;   }
    public void unregisterDataSetChangesListener() { _dataSetChangesListener=null;}

    String _filePath=null;

    Context _context= null;

    public BadPackageDataSet(Context context, String serializeFileName)
    {
        _context=context;
        _set=new HashSet<AppProblem>();
        _filePath= MediaTools.getInternalDataPath(_context)+ File.separatorChar+serializeFileName;

        //Generate file if it does not exist
        if(!MediaTools.existsFile(_filePath))
        {
            try
            {
                FileTools.writeTextFile(_filePath, "{\n" +
                        "  \"data\": [" +
                        "  ]\n" +
                        "}\n");
            }
            catch(IOException ioEx)
            {
                ioEx.printStackTrace();
            }
        }
        else
            loadData();
    }

    boolean checkIfPackageInList(String packageName)
    {
        for(AppProblem bpd : _set)
        {
            if(bpd.getPackageName().equals(packageName))
                return true;
        }

        return false;
    }

    public void addPackage(AppProblem pd)
    {
        _set.add(pd);

        if (_dataSetChangesListener != null)
            _dataSetChangesListener.onSetChanged();
    }

    public void addPackages(Set<AppProblem> packagesDataToAdd)
    {
        _set.addAll(packagesDataToAdd);
        if(_dataSetChangesListener!=null)
            _dataSetChangesListener.onSetChanged();
    }


    public boolean removePackage(AppProblem pd)
    {
        boolean b=_set.remove(pd);
        if(b)
        {
            if (_dataSetChangesListener != null)
                _dataSetChangesListener.onSetChanged();
        }
        return b;
    }

    public boolean removePackage(String packageName)
    {
        boolean found=false;
        int index=0;

        Iterator<AppProblem> iterator=_set.iterator();
        AppProblem bpdTemp=null;
        AppProblem bpd=null;
        while(!found && iterator.hasNext())
        {
            bpdTemp=iterator.next();
            if(bpdTemp.getPackageName().equals(packageName))
                bpd=bpdTemp;
        }

        if(bpd!=null)
            return removePackage(bpd);
        else
            return false;
    }

    public void clear()
    {
        _set.clear();

        if (_dataSetChangesListener != null)
            _dataSetChangesListener.onSetChanged();
    }

    public int getMenaceCount() {return _set.size(); }
    //Load WhiteList
    public void loadData()
    {
        try
        {
            String jsonFile = JSonTools.loadJSONFromFile(_context, _filePath);
            JSONObject obj = new JSONObject(jsonFile);

            JSONArray badAppsArray = obj.getJSONArray("data");

            for (int i = 0; i < badAppsArray.length(); i++)
            {
                JSONObject badAppObj = badAppsArray.getJSONObject(i);
                AppProblem bpd = new AppProblem("tempname");
                bpd.loadFromJSON(badAppObj);
                _set.add(bpd);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    //Load WhiteList
    public synchronized void writeData()
    {
        try
        {
            JSONObject jo;
            JSONArray jsonArray=new JSONArray();
            for(AppProblem pd : _set)
            {
                jo=pd.buildJSONObject();
                jsonArray.put(jo);
            }

            JSONObject rootObj=new JSONObject();
            rootObj.put("data",jsonArray);

            FileTools.writeTextFile(_filePath,rootObj.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


}*/
