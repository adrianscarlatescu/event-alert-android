package com.as.eventalertandroid.ui.common.filter.severity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.SeverityDTO;

import java.util.ArrayList;
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

public class SeveritiesSelectorFragment extends Fragment implements SeveritiesSelectorAdapter.ClickListener {

    @BindView(R.id.itemAllSeveritiesSelectorTextView)
    TextView allSeveritiesTextView;
    @BindView(R.id.itemAllSeveritiesSelectorCheckBox)
    CheckBox allSeveritiesCheckBox;
    @BindView(R.id.severitiesSelectorRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private ValidationListener validationListener;
    private final SeveritiesSelectorAdapter adapter = new SeveritiesSelectorAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_severities_selector, container, false);
        unbinder = ButterKnife.bind(this, view);

        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        allSeveritiesCheckBox.setChecked(adapter.isAllChecked());

        allSeveritiesTextView.setText(String.format(getString(R.string.all_severities), adapter.getItemCount()));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClicked(SeveritiesSelectorAdapter source) {
        allSeveritiesCheckBox.setChecked(adapter.isAllChecked());
    }

    public void setData(List<SeverityDTO> severities, List<SeverityDTO> selectedSeverities) {
        adapter.setSeverities(severities);
        adapter.setSelectedSeverities(selectedSeverities);
    }

    public void setOnValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    @OnClick(R.id.itemAllSeveritiesSelectorLinearLayout)
    void onAllSeveritiesClicked() {
        boolean isChecked = allSeveritiesCheckBox.isChecked();
        allSeveritiesCheckBox.setChecked(!isChecked);
        adapter.setSelectedSeverities(isChecked ? null : new ArrayList<>(adapter.getSeverities()));
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.severitiesSelectorValidateButton)
    void onValidateClicked() {
        validationListener.onValidateClicked(this, new ArrayList<>(adapter.getSelectedSeverities()));
        requireActivity().onBackPressed();
    }

    public interface ValidationListener {
        void onValidateClicked(SeveritiesSelectorFragment source, List<SeverityDTO> selectedSeverities);
    }

}
