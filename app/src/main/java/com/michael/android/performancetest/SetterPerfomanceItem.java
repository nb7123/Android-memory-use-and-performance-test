package com.michael.android.performancetest;

import android.support.annotation.NonNull;

/**
 * Created by michael on 15/12/18.
 */
public class SetterPerfomanceItem
{
    long spendTime;
    String methodName;
    String type;

    public SetterPerfomanceItem(@NonNull long spendTime,
                                @NonNull String methodName,
                                @NonNull String type)
    {
        this.spendTime = spendTime;
        this. methodName = methodName;
        this.type = type;
    }
}
