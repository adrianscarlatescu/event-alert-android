package com.as.eventalertandroid.enums;

import com.as.eventalertandroid.R;

import androidx.annotation.StringRes;

public enum Gender {

    MALE(R.string.gender_male), FEMALE(R.string.gender_female);

    @StringRes
    int name;

    Gender(@StringRes int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }

}
