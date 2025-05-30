package com.as.eventalertandroid.ui.common.filter.type;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.TypeDTO;

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

public class TypesSelectorFragment extends Fragment implements TypesSelectorAdapter.ClickListener {

    @BindView(R.id.itemAllTypesSelectorTextView)
    TextView allTypesTextView;
    @BindView(R.id.itemAllTypesSelectorCheckBox)
    CheckBox allTypesCheckBox;
    @BindView(R.id.typesSelectorRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private ValidationListener validationListener;
    private final TypesSelectorAdapter adapter = new TypesSelectorAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_types_selector, container, false);
        unbinder = ButterKnife.bind(this, view);

        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        allTypesCheckBox.setChecked(adapter.isAllChecked());

        allTypesTextView.setText(String.format(getString(R.string.all_types), adapter.getItemCount()));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClicked(TypesSelectorAdapter source) {
        allTypesCheckBox.setChecked(adapter.isAllChecked());
    }

    public void setData(List<TypeDTO> types, List<TypeDTO> selectedTypes) {
        adapter.setTypes(types);
        adapter.setSelectedTypes(selectedTypes);
    }

    public void setOnValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    @OnClick(R.id.itemAllTypesSelectorLinearLayout)
    void onAllTypesClicked() {
        boolean isChecked = allTypesCheckBox.isChecked();
        allTypesCheckBox.setChecked(!isChecked);
        adapter.setSelectedTypes(isChecked ? null : new ArrayList<>(adapter.getTypes()));
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.typesSelectorValidateButton)
    void onValidateClicked() {
        validationListener.onValidateClicked(this, new ArrayList<>(adapter.getSelectedTypes()));
        requireActivity().onBackPressed();
    }

    public interface ValidationListener {
        void onValidateClicked(TypesSelectorFragment source, List<TypeDTO> selectedTypes);
    }

}
