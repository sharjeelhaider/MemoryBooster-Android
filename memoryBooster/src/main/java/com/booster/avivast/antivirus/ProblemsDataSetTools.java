package com.booster.avivast.antivirus;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by hexdump on 04/02/16.
 */
public class ProblemsDataSetTools
{
    public static Set<IProblem> getAppProblemsSet(IDataSet<? extends IProblem> problems)
    {
        Set<IProblem> hashSet=new HashSet<IProblem>();
        getAppProblems(problems.getSet(), hashSet);
        return hashSet;
    }

    public static List<IProblem> getAppProblemsList(IDataSet<? extends IProblem> problems)
    {
        List<IProblem> list=new ArrayList<IProblem>();
        getAppProblems(problems.getSet(), list);
        return list;
    }

    public static Set<IProblem> getSystemProblemsSet(IDataSet<? extends IProblem> problems)
    {
        Set<IProblem> hashSet=new HashSet<IProblem>();
        getSystemProblems(problems.getSet(), hashSet);
        return hashSet;
    }

    public static List<IProblem> getSystemProblemsList(IDataSet<? extends IProblem> problems)
    {
        List<IProblem> list=new ArrayList<IProblem>();
        getSystemProblems(problems.getSet(), list);
        return list;
    }


    public static Collection<? extends IProblem> getAppProblems(Collection<? extends IProblem> problems, Collection<IProblem> target)
    {
        for(IProblem p: problems)
        {
            if(p.getType()== IProblem.ProblemType.AppProblem)
                target.add(p);
        }

        return target;
    }

    public static Collection<? extends IProblem> getSystemProblems(Collection<? extends IProblem> problems, Collection<IProblem> target)
    {
        for(IProblem p: problems)
        {
            if(p.getType()== IProblem.ProblemType.SystemProblem)
                target.add(p);
        }

        return target;
    }

    public static Set<PackageData> getAppProblemsAsPackageDataList(IDataSet<? extends IProblem> problems)
    {
        Set<PackageData> pd=new HashSet<PackageData>();

        Set<? extends IProblem> colProblems=problems.getSet();

        for(IProblem p: colProblems)
        {
            if(p.getType()== IProblem.ProblemType.AppProblem)
                pd.add((AppProblem)p);
        }

        return pd;
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

    static boolean removeNotExistingProblems(Context context, IDataSet<IProblem> dataSet)
    {
        boolean dirty=false;

        ArrayList<IProblem> toRemove=new ArrayList<IProblem>();

        Set<IProblem> problems=dataSet.getSet();

        for(IProblem p: problems)
        {
            if(!p.problemExists(context))
            {
                toRemove.add(p);
                dirty=true;
            }
        }

        problems.removeAll(toRemove);

        return dirty;
    }

    public static boolean removeAppProblemByPackage(IDataSet<IProblem>  dataSet, String packageName)
    {
        Set<IProblem> set=dataSet.getSet();

        AppProblem problem=null;

        for(IProblem p: set)
        {
            if(p.getType()== IProblem.ProblemType.AppProblem)
            {
                problem=(AppProblem) p;
                if(packageName.equals(problem.getPackageName()))
                {
                    return dataSet.removeItem(p);
                }
            }
        }

        return false;
    }

    public static void printProblems(IDataSet<IProblem> dataSet)
    {
        //Log.d("Lista","================== LISTA DE PROBLEMAS =============");

        for (IProblem p : dataSet.getSet())
        {
            if(p.getType()== IProblem.ProblemType.AppProblem)
            {
                AppProblem appProblem=(AppProblem) p;
                //Log.d("PACKAGE","  "+appProblem.getPackageName());
            }
            else
            {
                SystemProblem appProblem=(SystemProblem) p;
                //Log.d("SYSTEM SETTING","  "+appProblem.getClass().getSimpleName());
            }
        }
    }


/*
    static boolean isSystemProblemInCollection(Class<? extends SystemProblem> problem, Collection<IProblem> problems)
    {
        for(IProblem p : problems)
        {
            if(p.getType()== IProblem.ProblemType.SystemProblem && p.getClass()==problem.getClass())
            {
                return true;
            }
        }

        return false;
    }*/
}
