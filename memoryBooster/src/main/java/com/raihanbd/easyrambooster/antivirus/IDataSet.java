package com.raihanbd.easyrambooster.antivirus;

import java.util.Collection;
import java.util.Set;

/**
 * Created by hexdump on 03/02/16.
 */
public interface IDataSet<T>
{
    int getItemCount();
    boolean addItem(T item);
    boolean removeItem(T item);
    boolean addItems(Collection<? extends T> item);
    Set<T> getSet();
}
