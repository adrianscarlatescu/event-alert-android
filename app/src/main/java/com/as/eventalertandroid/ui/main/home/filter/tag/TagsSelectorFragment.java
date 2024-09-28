package com.as.eventalertandroid.ui.main.home.filter.tag;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.as.eventalertandroid.R;
import com.as.eventalertandroid.net.model.EventTag;

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

public class TagsSelectorFragment extends Fragment implements TagsSelectorAdapter.ClickListener {

    @BindView(R.id.itemAllTagsSelectorTextView)
    TextView allTagsTextView;
    @BindView(R.id.itemAllTagsSelectorCheckBox)
    CheckBox allTagsCheckBox;
    @BindView(R.id.tagsSelectorRecyclerView)
    RecyclerView recyclerView;

    @BindDrawable(R.drawable.vertical_separator)
    Drawable separator;

    private Unbinder unbinder;
    private ValidationListener validationListener;
    private final TagsSelectorAdapter adapter = new TagsSelectorAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter.setOnClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags_selector, container, false);
        unbinder = ButterKnife.bind(this, view);

        DividerItemDecoration decoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        decoration.setDrawable(separator);

        recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(adapter);

        if (adapter.isAllChecked()) {
            allTagsCheckBox.setChecked(true);
        }

        allTagsTextView.setText(String.format(getString(R.string.all_tags), adapter.getItemCount()));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClicked(TagsSelectorAdapter source) {
        allTagsCheckBox.setChecked(adapter.isAllChecked());
    }

    public void setData(List<EventTag> tags, Set<EventTag> selectedTags) {
        adapter.setTags(tags);
        adapter.setSelectedTags(selectedTags);
    }

    public void setOnValidationListener(ValidationListener validationListener) {
        this.validationListener = validationListener;
    }

    @OnClick(R.id.itemAllTagsSelectorLinearLayout)
    void onAllTagsClicked() {
        boolean isChecked = allTagsCheckBox.isChecked();
        allTagsCheckBox.setChecked(!isChecked);
        adapter.setSelectedTags(isChecked ? new HashSet<>() : new HashSet<>(adapter.getTags()));
        adapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tagsSelectorValidateButton)
    void onValidateClicked() {
        if (adapter.getSelectedTags().isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.message_minimum_one_tag), Toast.LENGTH_SHORT).show();
            return;
        }
        validationListener.onValidateClicked(this, adapter.getSelectedTags());
        requireActivity().onBackPressed();
    }

    public interface ValidationListener {
        void onValidateClicked(TagsSelectorFragment source, Set<EventTag> selectedTags);
    }

}
