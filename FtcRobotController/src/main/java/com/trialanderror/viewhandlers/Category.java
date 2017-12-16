package com.trialanderror.viewhandlers;

import android.content.Context;
import android.view.View;

public interface Category {
    //Random number to prevent id collision
    int ID_OFFSET = 102223;

    View getView(Context context);

    String getResult();

    String getName();
}