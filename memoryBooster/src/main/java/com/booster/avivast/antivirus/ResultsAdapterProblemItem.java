package com.booster.avivast.antivirus;

/**
 * Created by hexdump on 29/01/16.
 */
class ResultsAdapterProblemItem implements IResultsAdapterItem
{
    IProblem _problem=null;

    public ResultsAdapterProblemItem(IProblem problem)
    {
        _problem=problem;
    }

    public IProblem getProblem() { return _problem; }

    public AppProblem getAppProblem() throws ClassCastException
    {
        if(AppProblem.class.isAssignableFrom(_problem.getClass()))
            return (AppProblem)_problem;
        else
            throw new ClassCastException();
    }

    public SystemProblem getSystemProblem() throws ClassCastException
    {
        if(SystemProblem.class.isAssignableFrom(_problem.getClass()))
            return (SystemProblem)_problem;
        else
            throw new ClassCastException();
    }


    public ResultsAdapterItemType getType() { return _problem.getType()== IProblem.ProblemType.AppProblem ? ResultsAdapterItemType.AppMenace : ResultsAdapterItemType.SystemMenace;}
}
