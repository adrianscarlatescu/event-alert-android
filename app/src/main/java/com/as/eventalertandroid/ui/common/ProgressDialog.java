package com.as.eventalertandroid.ui.common;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;

import com.as.eventalertandroid.R;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressDialog extends Dialog {

    @BindView(R.id.dialogProgressBar)
    ProgressBar progressBar;

    private static final long MIN_DURATION = 250L;

    private long startTime;

    public ProgressDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        ButterKnife.bind(this);
        setCancelable(false);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void show() {
        startTime = System.currentTimeMillis();
        super.show();
    }

    @Override
    public void dismiss() {
        dismiss(null);
    }

    public void dismiss(DismissCallback dismissCallback) {
        long now = System.currentTimeMillis();
        long elapsed = now - startTime;
        long delay = Math.max(0, MIN_DURATION - elapsed);
        progressBar.animate()
                .alpha(0)
                .setStartDelay(delay)
                .withEndAction(() -> {
                    super.dismiss();
                    if (dismissCallback != null) {
                        dismissCallback.onDismissed();
                    }
                });
    }

    public interface DismissCallback {
        void onDismissed();
    }

}
