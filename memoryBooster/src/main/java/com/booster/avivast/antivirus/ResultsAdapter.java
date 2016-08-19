package com.booster.avivast.antivirus;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.booster.avivast.R;


/**
 * Created by Magic Frame on 13/01/2016.
 */

public class ResultsAdapter extends ArrayAdapter<IResultsAdapterItem>
{
    final int kHEADER_TYPE=0;
    final int kAPP_TYPE=1;
    final int kSYSTEM_TYPE=2;

    Context _context;

    int _appHeaderIndex=-1;
    int _systemMenacesHeaderIndex =-1;

    private IResultItemSelectedListener _onItemChangedStateListener =null;
    public void setResultItemSelectedStateChangedListener(IResultItemSelectedListener listemer) { _onItemChangedStateListener =listemer; }

    public ResultsAdapter(Context context, List<IProblem> problems)
    {
        super(context, R.layout.results_list_item, new ArrayList<IResultsAdapterItem>());

        _context=context;

        refreshByProblems(problems);
    }

    public void refreshByProblems(List<IProblem> bpdl)
    {
        clear();

        Collection<IProblem> appProblems=new ArrayList<IProblem>();
        ProblemsDataSetTools.getAppProblems(bpdl, appProblems);

        if(appProblems.size()>0)
        {
            _appHeaderIndex=0;
            ResultsAdapterHeaderItem headerItem=new ResultsAdapterHeaderItem(_context.getString(R.string.applications_header_text));
            add(headerItem);
            _addProblems(appProblems);
        }
        else
            _appHeaderIndex=-1;

        Collection<IProblem> systemProblems=new ArrayList<IProblem>();
        ProblemsDataSetTools.getSystemProblems(bpdl,systemProblems);

        if(systemProblems.size()>0)
        {
            _systemMenacesHeaderIndex =getCount();
            ResultsAdapterHeaderItem headerItem=new ResultsAdapterHeaderItem(_context.getString(R.string.system_header_text));
            add(headerItem);
            _addProblems(systemProblems);
        }
        else
            _systemMenacesHeaderIndex=-1;
    }

    public void refreshByResults(List<IResultsAdapterItem> rail)
    {
        clear();
        addAll(rail);
    }



    public void _addProblems(Collection<IProblem> problems)
    {
        ResultsAdapterProblemItem rapi=null;
        for(IProblem p : problems)
        {
            rapi=new ResultsAdapterProblemItem(p);
            add(rapi);
        }
    }

    public View _createView(final int position, ViewGroup parent)
    {
        int layoutId=-1;

        if(position==_appHeaderIndex || position== _systemMenacesHeaderIndex)
            layoutId=R.layout.results_list_header;
        else
            layoutId=R.layout.results_list_item;

        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v=inflater.inflate(layoutId, parent, false);

        return v;

    }

    public void _fillRowData(int position, View rootView)
    {
        if(position==_appHeaderIndex || position== _systemMenacesHeaderIndex)
        {
            ResultsAdapterHeaderItem obj = (ResultsAdapterHeaderItem)getItem(position);
            ResultsAdapterHeaderItem header=(ResultsAdapterHeaderItem) obj;
            TextView headerText=(TextView) rootView.findViewById(R.id.Titlelabel);
            headerText.setText(header.getDescription());
        }
        else if(_systemMenacesHeaderIndex==-1 || position< _systemMenacesHeaderIndex) //We are receiving something that is not a header and no system menaces
        {
            final ResultsAdapterProblemItem ri  = (ResultsAdapterProblemItem)getItem(position);
            final AppProblem ap=ri.getAppProblem();

            TextView textView = (TextView) rootView.findViewById(R.id.Titlelabel);
            TextView riskText = (TextView) rootView.findViewById(R.id.qualityApp);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.logo);
            if(ap.isDangerous())
            {
                riskText.setTextColor(ContextCompat.getColor(getContext(),R.color.HighRiskColor));
                riskText.setText(R.string.high_risk);
            }
            else
            {
                riskText.setTextColor(ContextCompat.getColor(getContext(),R.color.MediumRiskColor));
                riskText.setText(R.string.medium_risk);
            }

            RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.itemParent);
            relativeLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (_onItemChangedStateListener != null)
                        _onItemChangedStateListener.onItemSelected(ap);
                }
            });
            imageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (_onItemChangedStateListener != null)
                        _onItemChangedStateListener.onItemSelected(ap);
                }
            });


            textView.setText(StaticTools.getAppNameFromPackage(getContext(), ap.getPackageName()));
            imageView.setImageDrawable(StaticTools.getIconFromPackage(ap.getPackageName(), getContext()));
        }
        else
        {
            final ResultsAdapterProblemItem ri  = (ResultsAdapterProblemItem)getItem(position);
            final SystemProblem sp=ri.getSystemProblem();

            TextView textView = (TextView) rootView.findViewById(R.id.Titlelabel);
            TextView riskText = (TextView) rootView.findViewById(R.id.qualityApp);
            ImageView imageView = (ImageView) rootView.findViewById(R.id.logo);

            if(sp.isDangerous())
            {
                riskText.setTextColor(ContextCompat.getColor(getContext(),R.color.HighRiskColor));
                riskText.setText(R.string.high_risk);
            }
            else
            {
                riskText.setTextColor(ContextCompat.getColor(getContext(),R.color.MediumRiskColor));
                riskText.setText(R.string.medium_risk);
            }

            RelativeLayout linearLayout = (RelativeLayout) rootView.findViewById(R.id.itemParent);
            linearLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(_onItemChangedStateListener!=null)
                        _onItemChangedStateListener.onItemSelected(sp);
                }
            });
            imageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (_onItemChangedStateListener != null)
                        _onItemChangedStateListener.onItemSelected(sp);
                }
            });

            textView.setText(sp.getTitle(getContext()));
            imageView.setImageDrawable(sp.getIcon(getContext()));
        }
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final View rowView;

        if(convertView == null)
            rowView=_createView(position,parent);
        else
            rowView = convertView;



        _fillRowData(position, rowView);

        return rowView;

    }

    @Override
    public int getViewTypeCount() { return 3; }

    @Override
    public int getItemViewType(int position)
    {
        if(position==_appHeaderIndex || position== _systemMenacesHeaderIndex)
            return kHEADER_TYPE;
        else if(position< _systemMenacesHeaderIndex)
            return kAPP_TYPE;
        else
            return kSYSTEM_TYPE;
    }


    public int getRealCount()
    {
        int count=super.getCount();
        if(_appHeaderIndex!=-1)
            --count;
        if(_systemMenacesHeaderIndex !=-1)
            --count;

        return count;
    }
}
