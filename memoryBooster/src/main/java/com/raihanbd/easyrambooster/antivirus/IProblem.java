package com.raihanbd.easyrambooster.antivirus;


import android.content.Context;

/**
 * Created by hexdump on 03/02/16.
 */
public interface IProblem extends IJSONSerializer
{
    enum ProblemType { AppProblem, SystemProblem}

    public ProblemType getType();
    public boolean isDangerous();
    public boolean problemExists(Context context);
}


