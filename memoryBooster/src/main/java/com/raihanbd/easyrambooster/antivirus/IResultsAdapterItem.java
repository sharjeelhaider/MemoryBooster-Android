package com.raihanbd.easyrambooster.antivirus;

/**
 * Created by hexdump on 03/02/16.
 */
public interface IResultsAdapterItem
{
    enum ResultsAdapterItemType { Header, AppMenace, SystemMenace}

    public ResultsAdapterItemType getType();

}


