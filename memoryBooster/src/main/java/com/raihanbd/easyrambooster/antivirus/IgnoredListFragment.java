package com.raihanbd.easyrambooster.antivirus;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import geniuscloud.memory.booster.R;

/**
 * Created by Magic Frame on 27/01/2016.
 */
public class IgnoredListFragment extends Fragment
{

    AntivirusActivity getMainActivity() {return (AntivirusActivity) getActivity();}
    IgnoredAdapter _ignoredAdapter =null;
    UserWhiteList _userWhiteList =null;
    //List<IProblem> _workingList=null;

    private ListView _listView;
    private TextView _ignoredCounter;

    public void setData(Context context,UserWhiteList userWhiteList)
    {
        _userWhiteList = userWhiteList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.ignored_list_fragment, container, false);
        _listView = (ListView)rootView.findViewById(R.id.ignoredList);
        _ignoredAdapter=new IgnoredAdapter(getMainActivity(),new ArrayList<IProblem>(_userWhiteList.getSet()));
        _ignoredCounter = (TextView) rootView.findViewById(R.id.ignoredCounterText);

        _ignoredAdapter.setOnAdapterItemRemovedListener(new IOnAdapterItemRemoved<IProblem>()
        {
            @Override
            public void onItemRemoved(IProblem item)
            {
                MenacesCacheSet menaceCacheSet = getMainActivity().getMenacesCacheSet();
                _userWhiteList.removeItem(item);
                _userWhiteList.writeToJSON();
                menaceCacheSet.addItem(item);
                menaceCacheSet.writeToJSON();

                //ProblemsDataSetTools.printProblems(_userWhiteList);

                _updateFoundThreatsText(_ignoredCounter, _userWhiteList.getItemCount());

                if (_ignoredAdapter.getCount() <= 0)
                    getMainActivity().goBack();
            }
        });
        _listView.setAdapter(_ignoredAdapter);
        _updateFoundThreatsText(_ignoredCounter, _userWhiteList.getItemCount());

        //ProblemsDataSetTools.printProblems(_userWhiteList);
        return rootView;
    }

    void _updateFoundThreatsText(TextView textView, int appCount)
    {
        String finalStr=getString(R.string.ignored_counter);
        finalStr= StaticTools.fillParams(finalStr, "#", Integer.toString(appCount));
        textView.setText(finalStr);
    }


    //getExistant problems
    static public List<IProblem> getExistingProblems(Context context, Collection<IProblem> ignoredList)
    {
        //Create list of apps that are whitelisted and installed
        List<IProblem> existingProblems=new ArrayList<IProblem>();

        for(IProblem p : ignoredList)
        {
            if(p.getType()== IProblem.ProblemType.AppProblem)
            {
                AppProblem appProblem= (AppProblem) p;
                if(StaticTools.isPackageInstalled(context, appProblem.getPackageName()))
                    existingProblems.add(appProblem);
            }
            else if(p.problemExists(context))
                existingProblems.add(p);
        }

        return existingProblems;
    }



    @Override
    public void onResume()
    {
        super.onResume();

        /*_workingList=getExistingProblems(getMainActivity(), _workingList);
        _ignoredAdapter.refresh(_workingList);
        _updateFoundThreatsText(_ignoredCounter, _workingList.size());

        if(_ignoredAdapter.isEmpty())
            getMainActivity().goBack();*/

        //Remove not existant menaces and save this as current list if it was modified
        /*boolean dirty=ProblemsDataSetTools.removeNotExistingProblems(getActivity(),_userWhiteList);
        if(dirty)
            _userWhiteList.writeToJSON();*/
        getMainActivity().updateMenacesAndWhiteUserList();

        //Add new existant apps
        _ignoredAdapter.refresh(new ArrayList<IProblem>(_userWhiteList.getSet()));

        _updateFoundThreatsText(_ignoredCounter, _userWhiteList.getItemCount());

        ProblemsDataSetTools.printProblems(_userWhiteList);

        if(_userWhiteList.getItemCount()<=0)
        {
            getMainActivity().goBack();
        }

    }
}
