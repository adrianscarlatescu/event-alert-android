package com.as.eventalertandroid.ui.auth;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.as.eventalertandroid.R;

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

    @BindView(R.id.registerEmailEditText)
    EditText emailEditText;
    @BindView(R.id.registerPasswordEditText)
    EditText passwordEditText;
    @BindView(R.id.registerConfirmPasswordEditText)
    EditText confirmPasswordEditText;

    private Unbinder unbinder;
    private RegisterListener listener;

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
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick(R.id.registerButton)
    void onRegisterClicked() {
        listener.onRegisterRequest(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString(),
                confirmPasswordEditText.getText().toString());
    }

    void clearFields() {
        emailEditText.setText("");
        passwordEditText.setText("");
        confirmPasswordEditText.setText("");
    }

}
