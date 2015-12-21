package com.michael.android.performancetest.viewmodel;

import android.databinding.BaseObservable;

import com.michael.android.performancetest.BR;

/**
 * Created by michael on 15/12/21.
 */
public class Toggles extends BaseObservable
{
    /**
     * main activity working flag
     */
    private boolean mainActivityWorking;

    public void setMainActivityWorking(boolean isWorking)
    {
        mainActivityWorking = isWorking;

        notifyPropertyChanged(BR.toggles);
    }

    public boolean isMainActivityWorking()
    {
        return mainActivityWorking;
    }
}
