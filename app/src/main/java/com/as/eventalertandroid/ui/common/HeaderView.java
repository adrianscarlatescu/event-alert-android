package com.as.eventalertandroid.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.as.eventalertandroid.R;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HeaderView extends LinearLayout {

    @BindView(R.id.headerTitleTextView)
    TextView titleTextView;

    public HeaderView(Context context) {
        super(context);
        init(null);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.layout_header, this);
        ButterKnife.bind(this);

        if (attrs == null) {
            return;
        }

        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HeaderView);
        String title = ta.getString(R.styleable.HeaderView_headerTitle);

        if (title != null && !title.isEmpty()) {
            titleTextView.setText(title);
        }

        ta.recycle();
    }

    @OnClick(R.id.headerBackImageView)
    void onBackClicked() {
        ((Activity) getContext()).onBackPressed();
    }

}
