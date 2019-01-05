package com.afnan.datetimelibrary.date;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Calendar;

@SuppressWarnings("WeakerAccess")
public interface DateRangeLimiter extends Parcelable {
    int getMinYear();

    int getMaxYear();

    @NonNull Calendar getStartDate();

    @NonNull Calendar getEndDate();

    boolean isOutOfRange(int year, int month, int day);

    @NonNull Calendar setToNearestDate(@NonNull Calendar day);
}