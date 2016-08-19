package com.booster.avivast.antivirus;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by hexdump on 03/02/16.
 */
public interface IJSONSerializer
{
    public JSONObject buildJSONObject() throws JSONException;
    public void loadFromJSON(JSONObject appObject);
    public void writeToJSON(String filePath);
}
