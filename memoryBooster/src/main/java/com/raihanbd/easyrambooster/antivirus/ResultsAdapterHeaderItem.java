package com.raihanbd.easyrambooster.antivirus;

/**
 * Created by hexdump on 29/01/16.
 */
class ResultsAdapterHeaderItem implements IResultsAdapterItem
{
    String _description=null;

    public ResultsAdapterHeaderItem(String description)
    {
        _description=description;
    }

    public String getDescription() { return _description;}

    public ResultsAdapterItemType getType() { return ResultsAdapterItemType.Header;}
}
