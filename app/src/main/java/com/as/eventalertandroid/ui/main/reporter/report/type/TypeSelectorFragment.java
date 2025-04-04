package com.as.eventalertandroid.ui.main.reporter.report.type;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.TypeDTO;

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

public class TypeSelectorFragment extends Fragment implements TypeSelectorAdapter.ClickListener {

    @BindView(R.id.typeSelectorRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private ValidationListener validationListener;
    private final TypeSelectorAdapter adapter = new TypeSelectorAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_type_selector, container, false);
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
    public void onItemClicked(TypeSelectorAdapter source) {
        // Nothing to do
    }

    public void setData(List<TypeDTO> types, TypeDTO selectedTypes) {
        adapter.setTypes(types);
        adapter.setSelectedType(selectedTypes);
    }

    public void setOnValidationListener(TypeSelectorFragment.ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    @OnClick(R.id.typeSelectorValidateButton)
    void onValidateClicked() {
        if (adapter.getSelectedType() == null) {
            Toast.makeText(requireContext(), R.string.message_type_required, Toast.LENGTH_SHORT).show();
            return;
        }
        validationListener.onValidateClicked(this, adapter.getSelectedType());
        requireActivity().onBackPressed();
    }

    public interface ValidationListener {
        void onValidateClicked(TypeSelectorFragment source, TypeDTO selectedType);
    }

}
