package com.as.eventalertandroid.ui.main.home.filter.severity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.SeverityDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    CheckBox allTagsCheckBox;
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

        if (adapter.isAllChecked()) {
            allTagsCheckBox.setChecked(true);
        }

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
        allTagsCheckBox.setChecked(adapter.isAllChecked());
    }

    public void setData(List<SeverityDTO> severities, Set<SeverityDTO> selectedSeverities) {
        adapter.setSeverities(severities);
        adapter.setSelectedSeverities(selectedSeverities);
    }

    public void setOnValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    @OnClick(R.id.itemAllSeveritiesSelectorLinearLayout)
    void onAllTagsClicked() {
        boolean isChecked = allTagsCheckBox.isChecked();
        allTagsCheckBox.setChecked(!isChecked);
        adapter.setSelectedSeverities(isChecked ? new HashSet<>() : new HashSet<>(adapter.getSeverities()));
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.severitiesSelectorValidateButton)
    void onValidateClicked() {
        if (adapter.getSelectedSeverities().isEmpty()) {
            Toast.makeText(requireContext(), R.string.message_min_severity_required, Toast.LENGTH_SHORT).show();
            return;
        }
        validationListener.onValidateClicked(this, adapter.getSelectedSeverities());
        requireActivity().onBackPressed();
    }

    public interface ValidationListener {
        void onValidateClicked(SeveritiesSelectorFragment source, Set<SeverityDTO> selectedSeverities);
    }

}
