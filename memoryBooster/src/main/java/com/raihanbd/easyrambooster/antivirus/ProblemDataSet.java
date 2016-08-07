package com.raihanbd.easyrambooster.antivirus;

/**
 * Created by hexdump on 03/02/16.
 */

/*
public class ProblemDataSet
{
    private Set<IProblem> _set;
    Set<IProblem> getSet() {return _set;}
    void setSet(Set<IProblem> set) { _set=set;}

    IDataSetChangesListener _dataSetChangesListener=null;
    public void setDataSetChangesListener(IDataSetChangesListener listener)  { _dataSetChangesListener=listener;   }
    public void unregisterDataSetChangesListener() { _dataSetChangesListener=null;}

    String _filePath=null;

    Context _context= null;

    public ProblemDataSet(Context context, String serializeFileName)
    {
        _context=context;
        _set=new HashSet<IProblem>();
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

    static boolean checkIfPackageInCollection(String packageName, Collection<IProblem> problems)
    {
        for(IProblem p : problems)
        {
            if(p.getType()== IProblem.ProblemType.AppProblem)
            {
                if(((AppProblem)p).getPackageName().equals(packageName))
                    return true;
            }
        }

        return false;
    }

    public void addPackage(IProblem pd)
    {
        _set.add(pd);

        if (_dataSetChangesListener != null)
            _dataSetChangesListener.onSetChanged();
    }

    public void addItems(Set<IProblem> packagesDataToAdd)
    {
        _set.addAll(packagesDataToAdd);
        if(_dataSetChangesListener!=null)
            _dataSetChangesListener.onSetChanged();
    }


    public boolean removeItem(IProblem pd)
    {
        boolean b=_set.remove(pd);
        if(b)
        {
            if (_dataSetChangesListener != null)
                _dataSetChangesListener.onSetChanged();
        }
        return b;
    }

    public void clear()
    {
        _set.clear();

        if (_dataSetChangesListener != null)
            _dataSetChangesListener.onSetChanged();
    }

    public int getItemCount() {return _set.size(); }

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
            for(IProblem pd : _set)
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


}
*/