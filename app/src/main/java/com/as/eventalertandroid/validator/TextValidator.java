package com.as.eventalertandroid.validator;

import android.text.Editable;
import android.text.TextWatcher;

public class TextValidator implements TextWatcher {

    private final Validator validator;

    private TextValidator(Validator validator) {
        this.validator = validator;
    }

    public static TextValidator of(Validator validator) {
        return new TextValidator(validator);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        validator.validate();
    }

}
