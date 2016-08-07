package com.raihanbd.easyrambooster.antivirus;

import android.content.Context;


/**
 * Created by hexdump on 22/01/16.
 */
public class MenacesCacheSet extends JSONDataSet<IProblem>
{
    public MenacesCacheSet(Context context)
    {
        super(context,"menacescache.json",new ProblemFactory());
    }
}
