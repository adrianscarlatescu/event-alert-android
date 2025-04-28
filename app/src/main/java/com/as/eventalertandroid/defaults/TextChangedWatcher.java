package com.as.eventalertandroid.defaults;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.function.BooleanSupplier;

public class TextChangedWatcher implements TextWatcher {

    private final BooleanSupplier validator;

    public TextChangedWatcher(BooleanSupplier validator) {
        this.validator = validator;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        validator.getAsBoolean();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
