package com.as.eventalertandroid.enums;

import com.as.eventalertandroid.R;

import androidx.annotation.StringRes;

public enum Role {

    ROLE_USER(R.string.role_user), ROLE_ADMIN(R.string.role_admin);

    @StringRes
    int name;

    Role(@StringRes int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }

}
