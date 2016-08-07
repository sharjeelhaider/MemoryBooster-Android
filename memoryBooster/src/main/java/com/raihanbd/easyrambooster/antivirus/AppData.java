package com.raihanbd.easyrambooster.antivirus;

import android.content.Context;

import org.joda.time.DateTime;

import java.io.IOException;
import java.io.Serializable;


public class AppData implements Serializable
{
    transient final static String filePath="state.data";

	transient static AppData _instance=null;

    private boolean _voted;
	public boolean getVoted() { return _voted; }
	public void setVoted(boolean voted) { _voted=voted; }

	private boolean _firstScanDone=false;
	public boolean getFirstScanDone() { return _firstScanDone;}
	public void setFirstScanDone(boolean firstScanDone) { _firstScanDone=firstScanDone; }

	private boolean _eulaAccepted=false;
	public boolean getEulaAccepted() { return _eulaAccepted;}
	public void setEulaAccepted(boolean eulaAccepted) { _eulaAccepted=eulaAccepted; }

	transient static public DateTime kNullDate=new DateTime(1973,1,1,0,0);
    private DateTime _lastScanDate=new DateTime(1973,1,1,0,0);
	public DateTime getLastScanDate() { return _lastScanDate;}
	public void setLastScanDate(DateTime date) {_lastScanDate=date;}

    private DateTime _lastAdDate=new DateTime(1973,1,1,0,0);
    public DateTime getLastAdDate() { return _lastAdDate;}
    public void setLastAdDate(DateTime date) {_lastAdDate=date;}


	static public boolean isAppDataInited() {return _instance!=null;}
	static public  synchronized AppData getInstance(Context context)
	{
		if(_instance!=null)
		{
			//Log.d("AppData", "OOOOOOOOOOOOOOOOOOO> " + "AppData:getInstance: AppData instance existe devolviendo anterior referencia");
			return _instance;
		}
		else
		{
			//Log.d("AppData", "OOOOOOOOOOOOOOOOOOO> " + "AppData:getInstance: AppData instance NO EXISTE devolviendo Nueva referencia");

			_instance=StaticTools.deserializeFromDataFolder(context,filePath);

			if(_instance==null)
				_instance = new AppData();

			return _instance;
		}
	}

    /*List<String> _pendingMenaces=null;
    public List<String> getPendingMenaces() { return _pendingMenaces; }
    public void setPendingMenaces(Set<AppProblem> bprdl)
	{
		_pendingMenaces=new ArrayList<String>();
		for(AppProblem bprd : bprdl)
		{
			_pendingMenaces.add(bprd.getPackageName());
		}
	}*/

	public AppData() 
	{
		
	}
	
	
	public synchronized  void serialize(Context ctx)
	{
		try
		{
			StaticTools.serializeToDataFolder(ctx, this, filePath);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
}
