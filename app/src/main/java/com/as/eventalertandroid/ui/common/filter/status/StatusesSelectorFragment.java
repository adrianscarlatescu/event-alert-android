package com.as.eventalertandroid.ui.common.filter.status;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.StatusDTO;

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

public class StatusesSelectorFragment extends Fragment implements StatusesSelectorAdapter.ClickListener {

    @BindView(R.id.itemAllStatusesSelectorTextView)
    TextView allStatusesTextView;
    @BindView(R.id.itemAllStatusesSelectorCheckBox)
    CheckBox allStatusesCheckBox;
    @BindView(R.id.statusesSelectorRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private ValidationListener validationListener;
    private final StatusesSelectorAdapter adapter = new StatusesSelectorAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statuses_selector, container, false);
        unbinder = ButterKnife.bind(this, view);

        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        if (adapter.isAllChecked()) {
            allStatusesCheckBox.setChecked(true);
        }

        allStatusesTextView.setText(String.format(getString(R.string.all_statuses), adapter.getItemCount()));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClicked(StatusesSelectorAdapter source) {
        allStatusesCheckBox.setChecked(adapter.isAllChecked());
    }

    public void setData(List<StatusDTO> statuses, List<StatusDTO> selectedStatuses) {
        adapter.setStatuses(statuses);
        adapter.setSelectedStatuses(selectedStatuses);
    }

    public void setOnValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    @OnClick(R.id.itemAllStatusesSelectorLinearLayout)
    void onAllStatusesClicked() {
        boolean isChecked = allStatusesCheckBox.isChecked();
        allStatusesCheckBox.setChecked(!isChecked);
        adapter.setSelectedStatuses(isChecked ? null : adapter.getStatuses());
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.statusesSelectorValidateButton)
    void onValidateClicked() {
        validationListener.onValidateClicked(this, adapter.getSelectedStatuses());
        requireActivity().onBackPressed();
    }

    public interface ValidationListener {
        void onValidateClicked(StatusesSelectorFragment source, List<StatusDTO> selectedStatuses);
    }

}
