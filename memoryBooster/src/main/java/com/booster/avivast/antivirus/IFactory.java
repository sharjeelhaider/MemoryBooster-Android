package com.booster.avivast.antivirus;

/**
 * Created by hexdump on 03/02/16.
 */
public interface IFactory<T>
{
    T createInstance(String s);
}
