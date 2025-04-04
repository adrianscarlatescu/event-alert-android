package com.as.eventalertandroid.ui.main.reporter.report.status;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class StatusSelectorFragment extends Fragment implements StatusSelectorAdapter.ClickListener {

    @BindView(R.id.statusSelectorRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private ValidationListener validationListener;
    private final StatusSelectorAdapter adapter = new StatusSelectorAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_status_selector, container, false);
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
    public void onItemClicked(StatusSelectorAdapter source) {
        // Nothing to do
    }

    public void setData(List<StatusDTO> statuses, StatusDTO selectedStatus) {
        adapter.setStatuses(statuses);
        adapter.setSelectedStatus(selectedStatus);
    }

    public void setOnValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    @OnClick(R.id.statusSelectorValidateButton)
    void onValidateClicked() {
        if (adapter.getSelectedStatus() == null) {
            Toast.makeText(requireContext(), R.string.message_status_required, Toast.LENGTH_SHORT).show();
            return;
        }
        validationListener.onValidateClicked(this, adapter.getSelectedStatus());
        requireActivity().onBackPressed();
    }

    public interface ValidationListener {
        void onValidateClicked(StatusSelectorFragment source, StatusDTO selectedStatus);
    }

}
