package com.as.eventalertandroid.ui.auth;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.validator.TextValidator;
import com.as.eventalertandroid.validator.Validator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        super();
    }

    @BindView(R.id.registerEmailTextInputLayout)
    TextInputLayout emailLayout;
    @BindView(R.id.registerEmailTextInputEditText)
    TextInputEditText emailEditText;

    @BindView(R.id.registerPasswordTextInputLayout)
    TextInputLayout passwordLayout;
    @BindView(R.id.registerPasswordTextInputEditText)
    TextInputEditText passwordEditText;

    @BindView(R.id.registerConfirmPasswordTextInputLayout)
    TextInputLayout confirmPasswordLayout;
    @BindView(R.id.registerConfirmPasswordTextInputEditText)
    TextInputEditText confirmPasswordEditText;

    private Unbinder unbinder;
    private RegisterListener listener;

    private boolean isEmailFirstFocusOut;
    private final Validator emailValidator = () -> {
        String emailStr = emailEditText.getEditableText().toString();

        String messageEmailRequired = getString(R.string.message_email_required);
        String messageEmailLength = String.format(getString(R.string.message_email_length), Constants.LENGTH_50);
        String messageInvalidEmail = getString(R.string.message_invalid_email);

        if (emailStr.isEmpty()) {
            emailLayout.setError(messageEmailRequired);
            return false;
        }
        if (emailStr.length() > Constants.LENGTH_50) {
            emailLayout.setError(messageEmailLength);
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailLayout.setError(messageInvalidEmail);
            return false;
        }
        if (emailLayout.getError() != null && (emailLayout.getError().equals(messageEmailRequired) || emailLayout.getError().equals(messageEmailLength) || emailLayout.getError().equals(messageInvalidEmail))) {
            emailLayout.setError(null);
            emailLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator passwordValidator = () -> {
        String passwordStr = passwordEditText.getEditableText().toString();

        String messagePasswordRequired = getString(R.string.message_password_required);
        String messagePasswordLength = String.format(getString(R.string.message_password_length), Constants.LENGTH_8, Constants.LENGTH_50);

        if (passwordStr.isEmpty()) {
            passwordLayout.setError(messagePasswordRequired);
            return false;
        }
        if (passwordStr.length() < Constants.LENGTH_8 || passwordStr.length() > Constants.LENGTH_50) {
            passwordLayout.setError(messagePasswordLength);
            return false;
        }
        if (passwordLayout.getError() != null && (passwordLayout.getError().equals(messagePasswordRequired) || passwordLayout.getError().equals(messagePasswordLength))) {
            passwordLayout.setError(null);
            passwordLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator confirmPasswordValidator = () -> {
        String confirmPasswordStr = confirmPasswordEditText.getEditableText().toString();

        String messageConfirmationPasswordRequired = getString(R.string.message_confirmation_password_required);

        if (confirmPasswordStr.isEmpty()) {
            confirmPasswordLayout.setError(messageConfirmationPasswordRequired);
            return false;
        }
        if (confirmPasswordLayout.getError() != null && confirmPasswordLayout.getError().equals(messageConfirmationPasswordRequired)) {
            confirmPasswordLayout.setError(null);
            confirmPasswordLayout.setErrorEnabled(false);
        }

        return true;
    };

    private final Validator passwordsMatchValidator = () -> {
        String passwordStr = passwordEditText.getEditableText().toString();
        String confirmPasswordStr = confirmPasswordEditText.getEditableText().toString();

        String messageDifferentPasswords = getString(R.string.message_different_passwords);

        if (!confirmPasswordStr.isEmpty() && !passwordStr.equals(confirmPasswordStr)) {
            confirmPasswordLayout.setError(getString(R.string.message_different_passwords));
            return false;
        }
        if (confirmPasswordLayout.getError() != null && confirmPasswordLayout.getError().equals(messageDifferentPasswords)) {
            confirmPasswordLayout.setError(null);
            confirmPasswordLayout.setErrorEnabled(false);
        }

        return true;
    };

    public interface RegisterListener {
        void onRegisterRequest(String email, String password, String confirmPassword);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (RegisterFragment.RegisterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        unbinder = ButterKnife.bind(this, view);

        emailEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !isEmailFirstFocusOut) {
                emailEditText.addTextChangedListener(TextValidator.of(emailValidator));
                if (!emailEditText.getEditableText().toString().isEmpty()) {
                    emailValidator.validate();
                }
                isEmailFirstFocusOut = true;
            }
        });
        passwordEditText.addTextChangedListener(TextValidator.of(() -> passwordValidator.validate() & passwordsMatchValidator.validate()));
        confirmPasswordEditText.addTextChangedListener(TextValidator.of(() -> confirmPasswordValidator.validate() & passwordsMatchValidator.validate()));

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.registerButton)
    void onRegisterClicked() {
        if (!validateForm()) {
            return;
        }

        listener.onRegisterRequest(
                emailEditText.getEditableText().toString(),
                passwordEditText.getEditableText().toString(),
                confirmPasswordEditText.getEditableText().toString()
        );
    }

    void clearFields() {
        emailEditText.setText(null);
        passwordEditText.setText(null);
        confirmPasswordEditText.setText(null);
    }

    private boolean validateForm() {
        return emailValidator.validate() &
                passwordValidator.validate() &
                confirmPasswordValidator.validate() &
                passwordsMatchValidator.validate();
    }

}
