package com.as.eventalertandroid.ui.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.defaults.Constants;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {

    public LoginFragment() {
        super();
    }

    @BindView(R.id.loginEmailEditText)
    EditText emailEditText;
    @BindView(R.id.loginPasswordEditText)
    EditText passwordEditText;

    private Unbinder unbinder;
    private LoginListener listener;

    public interface LoginListener {
        void onLoginRequest(final String email, final String password);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (LoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (getContext() != null) {
            SharedPreferences pref = getContext().getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);
            String email = pref.getString(Constants.USER_EMAIL, "");
            String password = pref.getString(Constants.USER_PASSWORD, "");
            setFields(email, password);
        }

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.loginButton)
    void onLoginClicked() {
        listener.onLoginRequest(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString());
    }

    void setFields(String email, String password) {
        emailEditText.setText(email);
        passwordEditText.setText(password);
    }

}
