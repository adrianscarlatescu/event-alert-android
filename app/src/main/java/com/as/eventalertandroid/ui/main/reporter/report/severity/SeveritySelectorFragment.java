package com.as.eventalertandroid.ui.main.reporter.report.severity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.SeverityDTO;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SeveritySelectorFragment extends Fragment implements SeveritySelectorAdapter.ClickListener {

    @BindView(R.id.severitySelectorRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private ValidationListener validationListener;
    private final SeveritySelectorAdapter adapter = new SeveritySelectorAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_severity_selector, container, false);
        unbinder = ButterKnife.bind(this, view);

        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClicked(SeveritySelectorAdapter source) {
        // Nothing to do
    }

    public void setData(List<SeverityDTO> severities, SeverityDTO selectedSeverity) {
        adapter.setSeverities(severities);
        adapter.setSelectedSeverity(selectedSeverity);
    }

    public void setOnValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    @OnClick(R.id.severitySelectorValidateButton)
    void onValidateClicked() {
        validationListener.onValidateClicked(this, adapter.getSelectedSeverity());
        requireActivity().onBackPressed();
    }

    public interface ValidationListener {
        void onValidateClicked(SeveritySelectorFragment source, SeverityDTO selectedSeverity);
    }

}
