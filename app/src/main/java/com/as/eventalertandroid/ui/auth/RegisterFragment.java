package com.as.eventalertandroid.ui.auth;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.defaults.Constants;
import com.as.eventalertandroid.defaults.TextChangedWatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.BooleanSupplier;

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

    private final BooleanSupplier emailValidator = () -> {
        String emailStr = emailEditText.getEditableText().toString();
        if (emailStr.isEmpty()) {
            emailLayout.setError(getString(R.string.message_email_required));
            return false;
        }
        if (emailStr.length() > Constants.LENGTH_50) {
            emailLayout.setError(String.format(getString(R.string.message_email_length), Constants.LENGTH_50));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            emailLayout.setError(getString(R.string.message_invalid_email));
            return false;
        }

        emailLayout.setError(null);
        emailLayout.setErrorEnabled(false);
        return true;
    };

    private final BooleanSupplier passwordValidator = () -> {
        String passwordStr = passwordEditText.getEditableText().toString();
        if (passwordStr.isEmpty()) {
            passwordLayout.setError(getString(R.string.message_password_required));
            return false;
        }
        if (passwordStr.length() < Constants.LENGTH_8 || passwordStr.length() > Constants.LENGTH_50) {
            passwordLayout.setError(String.format(getString(R.string.message_password_length), Constants.LENGTH_8, Constants.LENGTH_50));
            return false;
        }

        passwordLayout.setError(null);
        passwordLayout.setErrorEnabled(false);
        return true;
    };

    private final BooleanSupplier confirmPasswordValidator = () -> {
        String passwordStr = passwordEditText.getEditableText().toString();
        String confirmPasswordStr = confirmPasswordEditText.getEditableText().toString();
        if (confirmPasswordStr.isEmpty()) {
            confirmPasswordLayout.setError(getString(R.string.message_confirmation_password_required));
            return false;
        }
        if (!passwordStr.equals(confirmPasswordStr)) {
            confirmPasswordLayout.setError(getString(R.string.message_different_passwords));
            return false;
        }

        confirmPasswordLayout.setError(null);
        confirmPasswordLayout.setErrorEnabled(false);
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

        emailEditText.addTextChangedListener(new TextChangedWatcher(emailValidator));
        passwordEditText.addTextChangedListener(new TextChangedWatcher(() -> passwordValidator.getAsBoolean() & confirmPasswordValidator.getAsBoolean()));
        confirmPasswordEditText.addTextChangedListener(new TextChangedWatcher(confirmPasswordValidator));

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
        return emailValidator.getAsBoolean() &
                passwordValidator.getAsBoolean() &
                confirmPasswordValidator.getAsBoolean();
    }

}
